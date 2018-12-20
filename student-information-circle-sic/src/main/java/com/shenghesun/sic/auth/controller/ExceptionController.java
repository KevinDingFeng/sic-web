package com.shenghesun.sic.auth.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.shenghesun.sic.sso.model.LoginInfo;

import lombok.extern.slf4j.Slf4j;

 /**
  * @ClassName: ExceptionController 
  * @Description: 处理程序异常
  * @author: yangzp
  * @date: 2018年11月30日 下午6:00:33  
  */
@Slf4j
@ControllerAdvice
public class ExceptionController {
	
	private static final String UNAUTHORIZED_MESSAGE = "unauthorizedMessage";
	
	/**
	 * @Title: defaultErrorHandler 
	 * @Description: 处理访问方法时权限不足问题 
	 * @param req
	 * @param e
	 * @return  String 
	 * @author yangzp
	 * @date 2018年11月30日下午6:00:57
	 **/ 
	@ExceptionHandler(value = UnauthorizedException.class)
	public String defaultErrorHandler(Model model, Exception e) {
		Subject subject = SecurityUtils.getSubject();
		LoginInfo info = (LoginInfo)subject.getPrincipal();
		if(info != null) {
			log.info("userId:"+info.getId()+";userName:"+info.getName()+"没有“"+e.getMessage()+"”权限");
		}else {
			log.info("没有“"+e.getMessage()+"”权限");
		}
		
		model.addAttribute(UNAUTHORIZED_MESSAGE,e.getMessage());
		return "403";
	}
}
