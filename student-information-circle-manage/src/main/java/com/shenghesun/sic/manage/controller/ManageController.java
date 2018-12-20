package com.shenghesun.sic.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/")
public class ManageController {

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
//		Subject subject = SecurityUtils.getSubject();
//		
//		if(subject.getPrincipal() != null) {
//			LoginInfo info = (LoginInfo)subject.getPrincipal();
//			return JsonUtils.getSuccessJSONObject(info);	
//		}
//		return JsonUtils.getFailJSONObject();
		return "redirect:/login";
	}
}
