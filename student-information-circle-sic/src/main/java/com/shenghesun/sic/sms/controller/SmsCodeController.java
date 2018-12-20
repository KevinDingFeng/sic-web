package com.shenghesun.sic.sms.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.sms.support.SmsCodeService;
import com.shenghesun.sic.utils.JsonUtil;

/**
 * 发短信接口
 * 
 * @author 程任强
 *
 */
@RestController
@RequestMapping(value = "/m/sms")
public class SmsCodeController {

	@Autowired
	private SmsCodeService smsService;

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public JSONObject sendCode(HttpServletRequest request,
			@RequestParam(value = "cellphone", required = false) String cellphone) {
		if (StringUtils.isEmpty(cellphone)) {
			return JsonUtil.getFailJSONObject("invalid cellphone");
		}
		// 一分钟校验
		if (!smsService.timeOutCheck(cellphone)) {
			return JsonUtil.getFailJSONObject("sms frequent");
		}
		// 发短信
		return smsService.sendSmsCode(cellphone) ? JsonUtil.getSuccessJSONObject() : JsonUtil.getFailJSONObject("发送失败");
	}

}
