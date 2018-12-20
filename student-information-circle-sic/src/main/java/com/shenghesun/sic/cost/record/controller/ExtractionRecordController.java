package com.shenghesun.sic.cost.record.controller;

import java.math.BigDecimal;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.core.constant.Presentation;
import com.shenghesun.sic.cost.record.entity.ExtractionRecord;
import com.shenghesun.sic.cost.record.entity.ExtractionRecord.ExtractionState;
import com.shenghesun.sic.cost.record.entity.UserCoffers;
import com.shenghesun.sic.cost.record.service.ExtractionRecordService;
import com.shenghesun.sic.cost.record.service.UserCoffersService;
import com.shenghesun.sic.utils.HttpHeaderUtil;
import com.shenghesun.sic.utils.JsonUtil;
import com.shenghesun.sic.utils.TokenUtil;
import com.shenghesun.sic.wx.entity.WxUserInfo;
import com.shenghesun.sic.wx.service.WxUserInfoService;

/**
  * @ClassName: ExtractionRecordController 
  * @Description: 提现记录
  * @author: yangzp
  * @date: 2018年11月14日 上午9:46:39  
  */
@RestController
@RequestMapping(value = "/m/extraction_record")
public class ExtractionRecordController {
	@Autowired
	private ExtractionRecordService extractionRecordService;
	
	@Autowired
	private WxUserInfoService wxUserService;
	
	@Autowired
	UserCoffersService userCoffersService;
	
	/**
	 * @Title: apply 
	 * @Description: 申请提现 
	 * @param req
	 * @param extractionRecord
	 * @param result
	 * @return  JSONObject 
	 * @author yangzp
	 * @date 2018年11月14日上午10:05:13
	 **/ 
	@RequestMapping(value = "/apply", method = RequestMethod.POST)
	public JSONObject apply(HttpServletRequest req, @Validated ExtractionRecord extractionRecord,
			BindingResult result) {
		if(result.hasErrors()) {
			return JsonUtil.getFailJSONObject("数据错误");
		}
		JSONObject json = new JSONObject();
		json.put("code", 1);
		json.put("message", "申请提现成功");
		// 获取用户信息 
		String token = HttpHeaderUtil.getToken(req);
		String openId = TokenUtil.getLoginUserAccount(token);//登录用户的 用户名, 这里使用的是 openId
		Long wxUserId = TokenUtil.getLoginUserId(token);//登录用户的 id, 这里使用的是 wxUserId
		
		//可提现总金额
		UserCoffers userCoffers = userCoffersService.findByUserID(wxUserId);
		BigDecimal totalIntegral = (userCoffers!=null)?userCoffers.getAmount() : new BigDecimal(0.00);
		if(totalIntegral.compareTo(extractionRecord.getAmount()) < 0) {//申请提现大于可提现金额
			json.put("code", -1);
			json.put("message", "提现金额大于可提现金额");
			return JsonUtil.getSuccessJSONObject(json);
		}
		
		WxUserInfo wxUser = wxUserService.findByIdAndOpenId(wxUserId, openId);
		extractionRecord.setDate(new Date(System.currentTimeMillis()));
		extractionRecord.setUser(wxUser);
		extractionRecord.setUserId(wxUserId);
		extractionRecord.setExtractionState(ExtractionState.Processing);
		extractionRecordService.save(extractionRecord);
		
		//从用户金库中减去提现金额，加上已提现金额
		userCoffers.setAmount(userCoffers.getAmount().subtract(extractionRecord.getAmount()));
		userCoffers.setWithdrAwnamount(userCoffers.getWithdrAwnamount().add(extractionRecord.getAmount()));
		userCoffersService.save(userCoffers);
		
		
		return JsonUtil.getSuccessJSONObject(extractionRecord);
	}
	
	/**
	 * @Title: details 
	 * @Description: 提现记录 
	 * @param req
	 * @param pageable
	 * @return  JSONObject 
	 * @author yangzp
	 * @date 2018年11月14日上午10:47:44
	 **/ 
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public JSONObject details(HttpServletRequest req, @PageableDefault(page = 0, value = Presentation.DEFAULT_PAGE_SIZE, sort = {
			Presentation.DEFAULT_ORDER_FIELD }, direction = Direction.DESC) Pageable pageable) {
		// 获取用户信息 
		String token = HttpHeaderUtil.getToken((HttpServletRequest) req);
		Long loginUserId = TokenUtil.getLoginUserId(token);//登录用户的 id
		Page<ExtractionRecord> result = extractionRecordService.findByUserId(loginUserId, pageable);
		
		return JsonUtil.getSuccessJSONObject(result);
	}
}
