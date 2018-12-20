package com.shenghesun.sic.auth.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.auth.support.LoginService;
import com.shenghesun.sic.config.CustomConfig;
import com.shenghesun.sic.system.entity.SysUser;
import com.shenghesun.sic.system.service.SysPoolService;
import com.shenghesun.sic.system.service.SysUserService;
import com.shenghesun.sic.utils.JsonUtil;
import com.shenghesun.sic.utils.RandomUtil;
import com.shenghesun.sic.wx.entity.WxUserInfo;
import com.shenghesun.sic.wx.service.WxUserInfoService;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;

@RestController
@Slf4j
public class AuthController {
	@Autowired
	private WxMaService wxService;
	@Autowired
	private WxUserInfoService wxUserInfoService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysPoolService sysPoolService;
	@Autowired
	private LoginService loginService;
	
	@Value("${sys.pool.id.default}")
	private Long SYS_POOL_ID;

	@RequestMapping(value = "/auth", method = RequestMethod.GET)
	public JSONObject getLoginStatus(@RequestParam(value = "code") String code,
			@RequestParam(value = "signature") String signature, @RequestParam(value = "rawData") String rawData,
			@RequestParam(value = "encryptedData") String encryptedData, @RequestParam(value = "iv") String iv)
			throws WxErrorException {

		WxMaJscode2SessionResult session = this.wxService.getUserService().getSessionInfo(code);
		String sessionKey = session.getSessionKey();
		// 用户授权 提交的微信信息 校验
		if (!this.wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
			return JsonUtil.getFailJSONObject("用户信息校验失败");
		}
		WxMaUserInfo userInfo = this.wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);// 解密用户信息
		
		String openId = userInfo.getOpenId();
		WxUserInfo user = wxUserInfoService.findByOpenId(openId);
		SysUser sysUser = null;
		if (user == null) {
			user = new WxUserInfo();//获取默认的微信用户信息
			sysUser = this.getDefaultSysUser(openId);//获取默认的系统用户信息
		}else {
			sysUser = sysUserService.findByOpenId(openId);
		}
		this.setCopyProperties(userInfo, user);//每次授权流程都会更新用户的基础信息
		user = wxUserInfoService.save(user);
		if(sysUser != null) {
			sysUser.setName(user.getNickName());
			sysUser = sysUserService.save(sysUser);//微信用户第一次授权，需要绑定生成一条系统普通用户信息
		}
		//判断用户设置的必要信息是否完整，只有完整的情况下，才可以继续操作重要环节；如果信息不完整，不提供高阶权限
		JSONObject json = new JSONObject();
		String authToken = loginService.login(user.getId(), openId);// 生成3rd token 返回给客户端小程序，此 token 只作为授权通过的标识
		json.put(CustomConfig.WEB_AUTH_TOKEN_PREFIX, authToken);
		//使用 两个 token 判断起来比较繁琐，暂时使用直接的 bool 值区别两种情况
//		if(user.getCellphone() != null && user.getBankAccount() != null) {
//			json.put(CustomConfig.WEB_REGISTER_TOKEN_PREFEX, loginService.register(user.getId(), openId));
//		}
		json.put("registered", wxUserInfoService.registered(user));
		
		log.info("授权成功\\t" + openId + "\\t" + authToken);
		return JsonUtil.getSuccessJSONObject(json);
	}
	private SysUser getDefaultSysUser(String openId) {
		SysUser sysUser = new SysUser();
		sysUser.setOpenId(openId);
		sysUser.setSysPoolId(SYS_POOL_ID);
		sysUser.setSysPool(sysPoolService.findById(SYS_POOL_ID));
		sysUser.setPassword(RandomUtil.randomString(16));
		sysUser.setSalt(RandomUtil.randomString());
		sysUser.setAccount(openId);
//		sysUser.setName(name);//使用昵称，提供修改方法
		return sysUser;
	}
	
	private void setCopyProperties(WxMaUserInfo userInfo, WxUserInfo user) {
		user.setOpenId(userInfo.getOpenId());
		user.setNickName(userInfo.getNickName());
//		user.setGender(userInfo.getGender());
		user.setLanguage(userInfo.getLanguage());
		user.setCity(userInfo.getCity());
		user.setProvince(userInfo.getProvince());
		user.setCountry(userInfo.getCountry());
//		user.setAvatarUrl(userInfo.getAvatarUrl());
//		user.setUnionId(userInfo.getUnionId());
	}
	/**
	 * 校验 token 是否依然有效，需要把 token 值存入 request header 中
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/check_token", method = RequestMethod.GET)
	public JSONObject getLoginStatus(HttpServletRequest req) {
		return loginService.checkToken(req) ? JsonUtil.getSuccessJSONObject() : JsonUtil.getTokenCheckFailJSONObject(JsonUtil.CODE_TOKEN_CHECK_FAIL_VALUE);
	}
}
