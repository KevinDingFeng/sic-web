package com.shenghesun.sic.login.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.system.entity.SysUser;
import com.shenghesun.sic.system.service.SysUserService;
import com.shenghesun.sic.utils.JsonUtil;
import com.shenghesun.sic.utils.PasswordUtil;

@Controller
public class LoginController {
	
	@Autowired
	private SysUserService sysUserService;
	
	/**
	 * 去往登录页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		
		SysUser sysUser = new SysUser();
		model.addAttribute("entity", sysUser);
		
		return "login/login";
	}
	

	/**
	 * 加密算法，接收登录名和明文的密码 如果登录名在数据库存在，则找到对应的盐值，完成对明文密码加密操作，把密文返回
	 * 如果登录名在数据库不存在或明文密码为空，则返回错误信息 不对密码是否正确进行校验，只负责加密
	 * 
	 * @param name
	 * @param password
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/encrypt", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject encrypt(@RequestParam(value = "account") String account, @RequestParam(value = "password") String password) {
		// 判断输入的密码是否有效
		if (StringUtils.isEmpty(password)) {
			System.out.println("密码不能未空");
			return JsonUtil.getFailJSONObject("输入信息有误");
		}
		// 获取登录名对应的数据
		SysUser user = sysUserService.findByAccount(account);
		if (user == null || user.getSalt() == null) {
			System.out.println("用户名不存在或用户的盐值不存在");
			return JsonUtil.getFailJSONObject("输入信息有误");
		}
		return JsonUtil.getSuccessJSONObject(PasswordUtil.encrypt(password, user.getSalt()));
	}
	
	
	/**
	 * 提交用户信息，进行登录校验 登录成功后跳转到指定的页面 登录失败，返回 传入的密码，目前版本是已经通过 /encrypt
	 * 完成加密的内容，所以后续登录判断中，无需再次加密 9
	 * 
	 * @param user
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, 
			@RequestParam(value = "account") String account, 
			@RequestParam(value = "password") String password, Model model) {
		try {
			Subject subject = SecurityUtils.getSubject();
			
//			if(subject.getPrincipal() != null) {
//				LoginInfo info = (LoginInfo)subject.getPrincipal();
//				return JsonUtils.getSuccessJSONObject(info);	
//			}
			subject.login(new UsernamePasswordToken(account, password, true));
//			LoginInfo info = (LoginInfo)subject.getPrincipal();
			//拥有分派订单权限
			if(subject.isPermitted("order:assign")) {
				return "redirect:/play_order/assign/list";
			}
			if(subject.isPermitted("order:receive")) {
				return "redirect:/play_order/receive/list";
			}
			return "redirect:/sys/sys_new/list";
		} catch (AuthenticationException e) {
			e.printStackTrace();
			System.out.println("用户名密码错误");
			model.addAttribute("entity", this.getEntity(account));
			model.addAttribute("errMessage", "用户名密码错误");
			return "login/login";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("特殊错误");
			model.addAttribute("entity", this.getEntity(account));
			model.addAttribute("errMessage", "特殊错误");
			return "login/login";
		}
	}
	private SysUser getEntity(String account) {
		SysUser user = new SysUser();
		user.setAccount(account);
		user.setPassword("");
		return user;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout() {
		SecurityUtils.getSubject().logout();
		return "redirect:/login";
	}
}
