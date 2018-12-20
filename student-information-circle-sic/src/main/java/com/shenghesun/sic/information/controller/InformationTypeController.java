package com.shenghesun.sic.information.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.information.entity.InformationType;
import com.shenghesun.sic.information.model.InformationTypeModel;
import com.shenghesun.sic.information.service.InformationTypeService;
import com.shenghesun.sic.utils.JsonUtil;

@RestController
@RequestMapping(value = "/m/infor_type")
public class InformationTypeController {
	
	@Autowired
	private InformationTypeService informationTypeService; 

	/**
	 * 获取可用的信息类型
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public JSONObject list(HttpServletRequest request) {
		List<InformationType> list = informationTypeService.findByRemoved(false);
		JSONArray arr = this.formatTypeArray(list);
		return JsonUtil.getSuccessJSONObject(arr);
	}
	private JSONArray formatTypeArray(List<InformationType> list) {
		JSONArray arr = new JSONArray();
		if(list != null && list.size() > 0) {
			for(InformationType it : list) {
				arr.add(new InformationTypeModel(it.getCode(), it.getName()));
			}
		}
		return arr;
	}
}
