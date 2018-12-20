package com.shenghesun.sic.cost.record.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.core.constant.Presentation;
import com.shenghesun.sic.cost.record.entity.IntegralRecord;
import com.shenghesun.sic.cost.record.entity.UserCoffers;
import com.shenghesun.sic.cost.record.service.IntegralRecordService;
import com.shenghesun.sic.cost.record.service.UserCoffersService;
import com.shenghesun.sic.utils.HttpHeaderUtil;
import com.shenghesun.sic.utils.JsonUtil;
import com.shenghesun.sic.utils.TokenUtil;

 /**
  * @ClassName: IntegralRecordController 
  * @Description: 积分记录
  * @author: yangzp
  * @date: 2018年11月13日 下午5:38:38  
  */
@RestController
@RequestMapping(value = "/m/integral_record")
public class IntegralRecordController {
	@Autowired
	private IntegralRecordService integralRecordService;
	
	@Autowired
	UserCoffersService userCoffersService;
	
	/**
	 * @Title: findTotalIntegral 
	 * @Description: 获取总积分 
	 * @param req
	 * @return  JSONObject 
	 * @author yangzp
	 * @date 2018年11月13日下午5:46:38
	 **/ 
	@RequestMapping(value = "/total_integral", method = RequestMethod.GET)
	public JSONObject findTotalIntegral(HttpServletRequest req) {
		// 获取用户信息 
		String token = HttpHeaderUtil.getToken((HttpServletRequest) req);
		Long loginUserId = TokenUtil.getLoginUserId(token);//登录用户的 id
		
		BigDecimal totalAmount = integralRecordService.findTotalIntegral(loginUserId);
		
		if(totalAmount == null) {
			totalAmount = new BigDecimal("0");
		}
		
		JSONObject json = new JSONObject();
		json.put("totalAmount", totalAmount);
		return JsonUtil.getSuccessJSONObject(json);
	}
	
	/**
	 * @Title: details 
	 * @Description: 获取积分明细 
	 * @param req
	 * @return  JSONObject 
	 * @author yangzp
	 * @date 2018年11月13日下午6:17:17
	 **/ 
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public JSONObject details(HttpServletRequest req,  @PageableDefault(page = 0, value = Presentation.DEFAULT_PAGE_SIZE, sort = {
			Presentation.DEFAULT_ORDER_FIELD }, direction = Direction.DESC) Pageable pageable) {
		// 获取用户信息 
		String token = HttpHeaderUtil.getToken((HttpServletRequest) req);
		Long loginUserId = TokenUtil.getLoginUserId(token);//登录用户的 id
		
		Page<IntegralRecord> integralRecords = integralRecordService.findByUserIdAndEffective(loginUserId, true, pageable);
		return JsonUtil.getSuccessJSONObject(integralRecords);
	}
	
	/**
	 * @Title: getExchangeMoney 
	 * @Description: 积分兑换，获取用户最大提现金额；目前按照1(元):1600 
	 * @param req
	 * @return  JSONObject 
	 * @author yangzp
	 * @date 2018年11月19日上午10:35:21
	 **/ 
	@RequestMapping(value = "/exchange_money", method = RequestMethod.GET)
	public JSONObject getExchangeMoney(HttpServletRequest req) {
		// 获取用户信息 
		String token = HttpHeaderUtil.getToken((HttpServletRequest) req);
		Long loginUserId = TokenUtil.getLoginUserId(token);//登录用户的 id
		UserCoffers userCoffers = userCoffersService.findByUserID(loginUserId);
		
		JSONObject json = new JSONObject();
		json.put("totalMoney", (userCoffers!=null)?userCoffers.getAmount() : new BigDecimal(0.00));
		return JsonUtil.getSuccessJSONObject(json);
	}
}
