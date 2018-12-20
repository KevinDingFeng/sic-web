package com.shenghesun.sic.login.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.shenghesun.sic.sso.model.LoginInfo;

/**
 * 页面可以通过 ${loginUser} 方式获取到登录信息
 * 
 * @author kevin
 *
 */
@ControllerAdvice("com.shenghesun.sic.sys")
public class CommonControllerSetup {

	@ModelAttribute("loginUser")
	public LoginInfo loginUser(HttpServletRequest req) {
		LoginInfo login = null;
		Subject sub = SecurityUtils.getSubject();
		if (sub != null) {
			Object obj = sub.getPrincipal();
			if (obj != null) {
				login = (LoginInfo) obj;
			}
		}
		if (login != null) {
			return login;
		} else {
			return null;
		}
	}

}
