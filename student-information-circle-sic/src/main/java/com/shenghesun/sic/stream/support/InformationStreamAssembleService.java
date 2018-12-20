package com.shenghesun.sic.stream.support;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.entity.InformationType;
import com.shenghesun.sic.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 信息流 组装 模块 通过 信息流池模块 提供的接口，获取信息数据，判断是否有条件生成新的信息流数据组，如果有，生成备用 并将记录 信息原数据
 * 和信息流数据组的关系 记录当前天的起始坐标
 * 
 * 撤回信息操作： 当信息数据审核模块发现信息数据存在疑问，需要回撤，不参与信息流推送 接到回撤命令后，返回“处理中”，异步调用处理撤回信息逻辑 撤回逻辑
 * 找到信息对应的信息流数据组，清除该信息的记录，并异步通知操作结果
 * 
 * @author kevin
 *
 */
@Service
@Slf4j
public class InformationStreamAssembleService {

	public static final Long ENOUGH_STANDART = 1L;

	@Autowired
	private InformationStreamPoolService informationStreamPoolService;

	/**
	 * 组装模块 接收到“有新数据，你尝试组装吧”后，异步开启尝试组装流程，并同步返回“我开始尝试了”
	 * 
	 * 通过 信息流池模块 提供的接口，获取信息数据，判断是否有条件生成新的信息流数据组（同步） 如果有，调用 组装方法
	 * 
	 * @return 操作成功的情况下，返回值中的 data 为 操作对应的 信息流数据 uuid ; 字符串数组
	 */
	public JSONObject tryAssemble(long id) {
		// 判断是否满足启动 组装的条件
		Set<InformationType> types = informationStreamPoolService.findTypesById(id);
		if(types == null) {
			return JsonUtil.getFailJSONObject();
		}
		Iterator<InformationType> it = types.iterator();
		JSONArray resultArr = new JSONArray();
		while(it.hasNext()) {
			InformationType type = it.next();
			if (this.canAssemble(type.getId())) {
				// 启动 组装
				JSONObject result = this.assemble(type.getId(), type.getCode());
				// 后续逻辑可以在判断之后添加
				if (JsonUtil.CODE_SUCCESS_VALUE.equals(result.get(JsonUtil.CODE_KEY))) {
					String[] arr = (String[])result.get(JsonUtil.DATA_KEY);
					resultArr.add(arr);
				} else {
					log.info("assemble error : " + result.getString(JsonUtil.DATA_KEY));
//					return JsonUtil.getFailJSONObject();
				}
			}
		}
		return JsonUtil.getSuccessJSONObject(resultArr);
	}

	private boolean canAssemble(long typeId) {
		// 获取 池中没有被发布的数量
		Long num = informationStreamPoolService.getNotUsedNum(typeId);
		return num >= ENOUGH_STANDART;
	}

	@Autowired
	private RedisCacheInformationStreamService redisCacheService;
	 
	/**
	 * 组装模块，开启组装流程（同步）
	 * 	生成备用
	 * 	并将记录 信息原数据 和信息流数据组的关系
	 * 	记录当前天的起始坐标
	 * @param typeId
	 * @param code 类型的 编码或者缩略写法
	 * @return 操作成功的情况下，返回值中的 data 为 操作对应的 信息流数据 uuid ; 字符串数组
	 */
	private JSONObject assemble(long typeId, String code) {
		List<Information> list = informationStreamPoolService.getNotUsedInformations(typeId);
		if(list != null) {
			// 确定当前的 坐标（如果当前天不存在，则获取当前坐标作为当前天的起始坐标存入）
			// 当前的坐标，获取到的就是 push 的位置，每次更新该值时，已经在原基础上做了递增
			// 获取当前类型的坐标
			String index = redisCacheService.getCurrentIndex(code);
			// 整理得到的信息流数组
			JSONArray inforArr = this.getInformationJSONArray(list);
			// 保存到流备用，返回 push 是否成功，如果是 true ，则更新当前 push 坐标，且负责维护当前天的起始坐标
//			JSONObject json = new JSONObject();
//			json.put("data", inforArr);
			if(redisCacheService.push(code, index, inforArr)) {
				//判断当前天 是否存在 起始值
				if(!redisCacheService.existsCurrentDayStartPushIndex(code)) {
					redisCacheService.insertCurrentDayStartPushIndex(code, index);
				}

				String[] uuidsArr = this.getUuidsArr(list);// 使用了的 uuid 数组
				// 记录每个信息 对应的存储坐标；传入的参数应该只是 id 的内容
				redisCacheService.saveUuidAndIndexMapping(uuidsArr, code, index);
				
				//如果发生异常，该值起始不变化
//				String nextIndex = redisCacheService.getNextIndex(code, index);
				String nextIndex = (1 + Integer.parseInt(index)) + "";
				// 更新 当前的坐标
				redisCacheService.updateCurrentIndex(code, nextIndex);

				return JsonUtil.getSuccessJSONObject(uuidsArr);// 返回操作相关的信息数据
			}else {
				return JsonUtil.getFailJSONObject("push error");	
			}
		}else {
			return JsonUtil.getFailJSONObject("empty list");
		}
	}

	private String[] getUuidsArr(List<Information> list) {
		String[] arr = new String[list.size()];
		int i = 0;
		for (Information infor : list) {
			arr[i++] = infor.getUuid();
		}
		return arr;
	}

	/**
	 * 信息流格式确定的位置
	 * @param list
	 * @return
	 */
	private JSONArray getInformationJSONArray(List<Information> list) {
		JSONArray arr = new JSONArray();
		for (Information infor : list) {
			JSONObject json = new JSONObject();
			json.put("uuid", infor.getUuid());
			json.put("title", infor.getTitle());
			json.put("imgs", infor.getImgs());
			json.put("cost", infor.isCost());
			arr.add(json);
//			InformationModel model = new InformationModel();
//			model.setUuid(infor.getUuid());
//			model.setTitle(infor.getTitle());
//			model.setImgs(infor.getImgs());
//			model.setCost(infor.isCost());
//			arr.add(model);
		}
//		JSONObject json = new JSONObject();
//		json.put("name", "kevin");
//		arr.add(json);
		return arr;
	}
	
}
