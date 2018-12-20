package com.shenghesun.sic.ad.record.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.ad.record.entity.AdClickRecord;
import com.shenghesun.sic.ad.record.service.AdClickRecordService;
import com.shenghesun.sic.stream.support.AdStreamPoolService;
import com.shenghesun.sic.utils.JsonUtil;
import com.shenghesun.sic.utils.TokenUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 点击广告统计点击记录的方法
 * 	需要添加点击次数修改，同步到 广告投放策略和广告池管理
 * @author kevin
 *
 */
@RestController
@RequestMapping(value = "/click")
@Slf4j
public class AdClickStatisController {
	
	@Autowired
	private AdClickRecordService adClickRecordService;
	@Autowired
	private AdStreamPoolService adStreamPoolService;

	@RequestMapping(method = RequestMethod.POST)
	public JSONObject click(HttpServletRequest request, 
			@RequestParam(value = "uuid") String uuid, 
			@RequestParam(value = "sid") Long sid, 
			@RequestParam(value = "token", required = false) String token 
			) {
		AdClickRecord acr = new AdClickRecord();
		if(StringUtils.isNotBlank(token)) {
			try {
				Long loginUserId = TokenUtil.getLoginUserId(token);//登录用户的 id
				log.info("click ad sid " + sid + " uuid " + uuid + " userId " + loginUserId);
				acr.setUserId(loginUserId);
			} catch (Exception e) {
				e.printStackTrace();
				//return JsonUtil.getFailJSONObject("token 解析错误");
			}
		}
		acr.setAdUuid(uuid);
		acr.setAdStrategyId(sid);
		adClickRecordService.save(acr);
		// 添加了一次点击数据，需要告诉广告池该策略的点击次数添加，需要控制阀值
		adStreamPoolService.addClick(sid);
		return JsonUtil.getSuccessJSONObject();
	}
}
