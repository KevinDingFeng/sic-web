package com.shenghesun.sic.stream.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.ad.entity.Ad;
import com.shenghesun.sic.ad.entity.Ad.Subject;
import com.shenghesun.sic.ad.service.AdService;
import com.shenghesun.sic.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 软文池管理模块
 * 	软文审核通过之后，发通知给该模块；该模块发起组装软文数据流程
 * @author kevin
 *
 */
@Service
@Slf4j
public class FlexibleStreamPoolService {
	
	@Autowired
	private FlexibleStreamAssembleService flexibleStreamAssembleService;
	@Autowired
	private AdService adService;

	/**
	 * 软文池模块 接收到 “有新的软文数据产生”后，异步开启处理新软文的流程，并同步返回“我收到了”
	 * @return
	 */
	public JSONObject newFlexibleNotify(long id) {
		//开启 异步的 处理新软文的流程
		this.headleNewFlexible(id);
		return JsonUtil.getSuccessJSONObject();
	}
	/**
	 * 处理新软文的流程
	 * 	池模块“处理新数据”，同步通知 组装模块 “有新数据，你尝试组装吧”，并等待结果
	 * 		接收到返回结果“我开始尝试了”后，继续
	 * @return
	 */
	public void headleNewFlexible(long id) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				JSONObject result = flexibleStreamAssembleService.assemble(id);
				// 根据实际需求，添加对返回值处理逻辑
				// 修改使用过的 信息 uuid 的状态
				if(JsonUtil.CODE_SUCCESS_VALUE.equals(result.getString(JsonUtil.CODE_KEY))) {
					log.info("notify flexible assemble success");
				}else {
					log.info("notify flexible assemble fail : " + result.getString(JsonUtil.DATA_KEY));
				}
			}
		}.start();
	}
	public Ad getFlexibleById(long id) {
		return adService.findByIdAndSubject(id, Subject.Flexible);
	}
}
