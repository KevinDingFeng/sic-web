package com.shenghesun.sic.wx.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.sms.support.SmsCodeService;
import com.shenghesun.sic.utils.HttpHeaderUtil;
import com.shenghesun.sic.utils.JsonUtil;
import com.shenghesun.sic.utils.TokenUtil;
import com.shenghesun.sic.wx.entity.WxUserInfo;
import com.shenghesun.sic.wx.model.WxUserInfoModel;
import com.shenghesun.sic.wx.service.WxUserInfoService;

@RestController
@RequestMapping(value = "/m/wx_user_info")
public class WxUserInfoController {
	
	@Autowired
	private WxUserInfoService wxUserService;
	@Autowired
	private SmsCodeService smsService;
	

	/**
	 * 获取 微信用户的基础信息
	 * @param req
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public JSONObject index(HttpServletRequest req) {
		String token = HttpHeaderUtil.getToken(req);
		String openId = TokenUtil.getLoginUserAccount(token);//登录用户的 用户名, 这里使用的是 openId
		Long wxUserId = TokenUtil.getLoginUserId(token);//登录用户的 id, 这里使用的是 wxUserId 
		
		WxUserInfo wxUser = wxUserService.findByIdAndOpenId(wxUserId, openId);
		WxUserInfoModel wxUserModel = this.getModel(wxUser);
		
		return JsonUtil.getSuccessJSONObject(wxUserModel);
	}
	private WxUserInfoModel getModel(WxUserInfo wxUser) {
		WxUserInfoModel model = new WxUserInfoModel();
		model.setNickName(wxUser.getNickName());
		model.setHeadImgUrl(wxUser.getHeadImgUrl());
		model.setCellphone(wxUser.getCellphone());//TODO 加个隐藏功能
		model.setBankAccount(wxUser.getBankAccount());//TODO 加个隐藏功能
		return model;
	}
	/**
	 * 补全或者修改微信用户的关键信息
	 * @param req
	 * @param cellphone
	 * @param code
	 * @param bankAccount
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public JSONObject save(HttpServletRequest req, 
			@RequestParam(value = "cellphone") String cellphone, 
			@RequestParam(value = "code") String code, 
			@RequestParam(value = "bankAccount") String bankAccount) {
		// 信息必须上传完整
		if(StringUtils.isEmpty(cellphone) || StringUtils.isEmpty(code) || StringUtils.isEmpty(bankAccount)) {
			return JsonUtil.getFailJSONObject("信息不完整");
		}
		// 校验 code 是否正确 
		if(!smsService.check(cellphone, code)) {
			return JsonUtil.getFailJSONObject("信息不正确");
		}
		String token = HttpHeaderUtil.getToken(req);
		String openId = TokenUtil.getLoginUserAccount(token);//登录用户的 用户名, 这里使用的是 openId
		Long wxUserId = TokenUtil.getLoginUserId(token);//登录用户的 id, 这里使用的是 wxUserId
		WxUserInfo wxUser = wxUserService.findByIdAndOpenId(wxUserId, openId);
		
		wxUser.setBankAccount(bankAccount);
		wxUser.setCellphone(cellphone);
		wxUser = wxUserService.save(wxUser);
		
		WxUserInfoModel wxUserModel = this.getModel(wxUser);
		return JsonUtil.getSuccessJSONObject(wxUserModel);
	}
	/**
	 * 校验 授权用户 是否有资格进行计费请求
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/check_registered", method = RequestMethod.GET)
	public JSONObject registered(HttpServletRequest req) {
//		String token = HttpHeaderUtil.getToken(req);
//		Long wxUserId = TokenUtil.getLoginUserId(token);//登录用户的 id, 这里使用的是 wxUserId
//		return wxUserService.registered(wxUserService.findById(wxUserId)) ? JsonUtil.getSuccessJSONObject() : JsonUtil.getFailJSONObject();
		return JsonUtil.getSuccessJSONObject();
	}
	
}
