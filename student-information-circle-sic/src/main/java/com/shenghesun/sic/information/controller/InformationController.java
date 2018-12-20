package com.shenghesun.sic.information.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.model.InformationModel;
import com.shenghesun.sic.information.service.InformationService;
import com.shenghesun.sic.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/m/information")
@Slf4j
public class InformationController {

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

