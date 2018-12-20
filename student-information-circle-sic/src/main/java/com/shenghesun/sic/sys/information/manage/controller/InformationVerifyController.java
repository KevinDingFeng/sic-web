package com.shenghesun.sic.sys.information.manage.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.shenghesun.sic.core.constant.Authorized;
import com.shenghesun.sic.core.constant.Presentation;
import com.shenghesun.sic.core.constant.UnauthorizedMessage;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.entity.InformationVerify;
import com.shenghesun.sic.information.entity.InformationVerify.VerifyResult;
import com.shenghesun.sic.information.service.InformationService;
import com.shenghesun.sic.information.service.InformationVerifyService;
import com.shenghesun.sic.sso.model.LoginInfo;
import com.shenghesun.sic.stream.support.InformationStreamPoolService;

/**
  * @ClassName: InformationController 
  * @Description: 信息 即 新闻等文章或图文审核
  * @author: yangzp
  * @date: 2018年11月6日 下午3:02:00  
  */
//@Slf4j
@Controller
@RequestMapping("/sys/new_verify")
public class InformationVerifyController {
	
	@Autowired
	private InformationVerifyService informationVerifyService;
	
	@Autowired
	private InformationService informationService;
	
	@Autowired
	private InformationStreamPoolService informationStreamPoolService;

	/**
	 * @Title: form 
	 * @Description: 进入审核页面 
	 * @param model
	 * @param id
	 * @return  String 
	 * @author yangzp
	 * @date 2018年11月9日下午2:56:42
	 **/ 
	@RequestMapping(value = "form", method = RequestMethod.GET)
	public String form(Model model, 
			@RequestParam(value = Presentation.KEY_ID, required = true) Long id) {
		
		Subject sub = SecurityUtils.getSubject();
    	if (!sub.isPermitted(Authorized.INFORMATION_VERIFY)) {
    		throw new UnauthorizedException(UnauthorizedMessage.INFORMATION_VERIFY);
    	}
		
		Information information = informationService.findOne(id);
			
		model.addAttribute(Presentation.KEY_ENTITY, information);
		
		return "news_verify/form";
	}
	
	/**
	 * @Title: verify 
	 * @Description: 审核 
	 * @param informationVerify
	 * @param result
	 * @return  String 
	 * @author yangzp
	 * @date 2018年11月9日下午2:59:23
	 **/ 
	@RequestMapping(value = "verify", method = RequestMethod.POST)
	public String verify(@Validated InformationVerify informationVerify, BindingResult result) {
		
		Subject subject = SecurityUtils.getSubject();
    	if (!subject.isPermitted(Authorized.INFORMATION_VERIFY)) {
    		throw new UnauthorizedException(UnauthorizedMessage.INFORMATION_VERIFY);
    	}
		LoginInfo info = (LoginInfo)subject.getPrincipal();
		
		Information information = informationService.findOne(informationVerify.getInformationId());
		//审核通过
		if(VerifyResult.Pass.equals(informationVerify.getVerifyResult())) {
			information.setVerified(true);
			informationStreamPoolService.newInformationNotify(informationVerify.getInformationId());
		}
		
		informationVerify.setInformation(information);
		informationVerify.setSysUserId(info.getId());
		//序列码+1
		Integer seqNum = informationVerifyService.findMaxByInformationId(informationVerify.getInformationId());
		if(seqNum != null) {
			informationVerify.setSeqNum(seqNum + 1);
		}
		informationVerifyService.save(informationVerify);
		
		return "redirect:/sys/sys_new/list";
	}
}
