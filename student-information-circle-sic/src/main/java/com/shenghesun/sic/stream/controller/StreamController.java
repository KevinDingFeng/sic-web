package com.shenghesun.sic.stream.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.entity.InformationType;
import com.shenghesun.sic.information.model.InformationModel;
import com.shenghesun.sic.information.model.InformationTypeModel;
import com.shenghesun.sic.information.service.InformationService;
import com.shenghesun.sic.information.service.InformationTypeService;
import com.shenghesun.sic.stream.support.AdStreamProvider;
import com.shenghesun.sic.stream.support.FlexibleStreamProvider;
import com.shenghesun.sic.stream.support.InformationStreamPoolService;
import com.shenghesun.sic.stream.support.InformationStreamProvider;
import com.shenghesun.sic.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/stream")
@Slf4j
public class StreamController {

	// 需要有一个 缓存 记录用户上一次阅读的位置，可以根据这个位置，返货默认的数据
	// 然后才能用户才能根据这个位置获取上一页和下一页的内容
	// 如果不存在这个 缓存，可以返回当前天的最起始位置的数据
	// {日期字符串 ：当前天信息流起始位置}
	// {流位置 ： 信息流数据组（只包括信息流数据的 JSONArray）}
	// 广告流数据，在请求产生后，通过 广告流池组件 获取（这样的流程便于后续对广告定向投放等等的控制）
	
	@Autowired
	private InformationStreamProvider informationStreamProvider;
	@Autowired
	private AdStreamProvider adStreamProvider;
	@Autowired
	private FlexibleStreamProvider flexibleStreamProvider;
	@Autowired
	private InformationStreamPoolService informationStreamPoolService;
	
	/**
	 * 根据传入的 type 和 index 获取对应的信息流数据
	 * 	controller 层不考虑数据结构，只负责传入对应的参数，获取数据
	 * @param req
	 * @param index 获取信息的坐标，相当于翻页中的页数，首次获取可以为 null ，后台获取默认值填充
	 * @param type 类型，如果不返回，后天获取默认值填充
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public JSONObject list(HttpServletRequest req, 
			@RequestParam(value = "index", required = false) String index, 
			@RequestParam(value = "type", required = false) String type) {
		log.info("type = " + type + " index = " + index);
		// 没必要记录用户上一次的阅读坐标，如果 index 值不存在，直接使用当前天的起始位置即可
		if(StringUtils.isBlank(type)) {
			type = informationStreamPoolService.getDefaultType();//获取默认类型
		}
//		String index = null;
		if(StringUtils.isBlank(index) || Integer.parseInt(index) < 1) {
			// 获取默认的值
			index = informationStreamProvider.getDefaultIndex(type);//获取默认坐标，不考虑默认坐标的提供逻辑
//		}else {
//			index = informationStreamProvider.getIndex(indexVal);//根据传入的数值，生成坐标值
		}
		// 根据 index 获取 信息流集合
		JSONArray informationStreamArr = informationStreamProvider.getByIndexAndType(type, index);//根据类型和坐标获取数据
		// req 中包含 token 信息，表示该用户登录过，可以做后续的优化；如果 不存在 token ，则直接返回普通数据
		JSONObject adStream = adStreamProvider.getByRequest(req);// 根据 request 通过 广告流池模块 获取 广告流数据
		JSONObject flexibleStream = flexibleStreamProvider.getByRequest(req);// 根据 request 通过 软文流池模块 获取 软文流数据
		
		JSONObject json = new JSONObject();
		
		json.put("inforArr", informationStreamArr);// 放置 信息流集合 数据
		json.put("ad", adStream);// 放置 广告流 数据
		json.put("flexible", flexibleStream);// 放置 软文流 数据
//		json.put("index", informationStreamProvider.getIndexVal(index));//当前的 index数值 
		json.put("index", index);
		json.put("type", type);
		
		return JsonUtil.getSuccessJSONObject(json);
//		String data = "{\"code\":\"200\",\"data\":{\"ad\":{\"imgs\":\"1.jpg,2.jpg,3.jpg\",\"landingPage\":\"http://www.baidu.com\",\"title\":\"标题1\",\"uuid\":\"0129c3a6ee594c57b2b7163ec88b3988\",\"sid\":1},\"inforArr\":[{\"imgs\":\"1phb.jpg,586p.jpg,y6c6.jpg\",\"cost\":true,\"title\":\"你还记得《如懿传》里的穿帮镜头吗？\",\"uuid\":\"df2b64a2bbce4d38916aa8ea113d91f0\"}],\"flexible\":{\"imgs\":\"1.jpg,2.jpg,3.jpg\",\"landingPage\":\"http://www.baidu.com\",\"title\":\"标题1\",\"uuid\":\"0129c3a6ee594c57b2b7163ec88b3988\",\"sid\":1},\"index\":\"1\",\"type\":\"Recommend\"},\"message\":\"success\"}";
//		return JSONObject.parseObject(data);
	}
	
	@Autowired
	private InformationTypeService informationTypeService;
	//获取分类类型和类型对应的开始节点
	@RequestMapping(value = "/types", method = RequestMethod.GET)
	public JSONObject types() {
		List<InformationType> list = informationTypeService.findByRemoved(false);
		List<InformationTypeModel> models = this.typesFormation(list);
		return JsonUtil.getSuccessJSONObject(models);
//		String data = "{\"code\":\"200\",\"data\":[{\"code\":\"Recommend\",\"name\":\"推荐\",\"index\":\"6\"},{\"code\":\"Headlines\",\"name\":\"校园头条\",\"index\":\"7\"},{\"code\":\"Education\",\"name\":\"教育\",\"index\":\"4\"},{\"code\":\"Movies\",\"name\":\"影视\",\"index\":\"6\"},{\"code\":\"ESports\",\"name\":\"电竞\",\"index\":\"7\"},{\"code\":\"Constellation\",\"name\":\"星座\",\"index\":\"4\"},{\"code\":\"Constellation1\",\"name\":\"星座1\",\"index\":\"4\"},{\"code\":\"Constellation2\",\"name\":\"星座2\",\"index\":\"4\"},{\"code\":\"Constellation3\",\"name\":\"星座3\",\"index\":\"4\"},{\"code\":\"Constellation4\",\"name\":\"星座4\",\"index\":\"4\"}],\"message\":\"success\"}";
//		return JSONObject.parseObject(data);
	}
	
	private List<InformationTypeModel> typesFormation(List<InformationType> list){
		if(list == null || list.size() < 1) {
			return new ArrayList<>();
		}
		List<InformationTypeModel> models = new ArrayList<>();
		for(InformationType type : list) {
			InformationTypeModel model = new InformationTypeModel(type.getCode(), type.getName());
			model.setIndex(informationStreamProvider.getDefaultIndex(type.getCode()));
			models.add(model);
		}
		return models;
	}
	
	@Autowired
	private InformationService informationService;
	
	@RequestMapping(value = "/detail/{uuid}", method = RequestMethod.GET)
	public JSONObject detail(HttpServletRequest req, 
			@PathVariable("uuid") String uuid) {
		log.info("information detail " + uuid);
		Information info = informationService.findByUuid(uuid);
		InformationModel model = this.getModel(info);
		
		return JsonUtil.getSuccessJSONObject(model);
	}

	private InformationModel getModel(Information info) {
		InformationModel model = new InformationModel();
		model.setUuid(info.getUuid());
		model.setTitle(info.getTitle());
		model.setImgs(info.getImgs());
		model.setContext(info.getContext());
		model.setCost(info.isCost());
		return model;
	}
}
