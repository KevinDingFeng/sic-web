package com.shenghesun.sic.timing.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shenghesun.sic.cost.record.entity.ExchangeRate;
import com.shenghesun.sic.cost.record.entity.MoneyRecord;
import com.shenghesun.sic.cost.record.entity.UserCoffers;
import com.shenghesun.sic.cost.record.service.ExchangeRateService;
import com.shenghesun.sic.cost.record.service.IntegralRecordService;
import com.shenghesun.sic.cost.record.service.UserCoffersService;
import com.shenghesun.sic.utils.DateUtils;
import com.shenghesun.sic.wx.entity.WxUserInfo;
import com.shenghesun.sic.wx.service.WxUserInfoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TimingService {
	
	
	@Autowired
	private IntegralRecordService integralRecordService;
	
	@Autowired
	private UserCoffersService userCoffersService;
	
	
	@Autowired
	private ExchangeRateService exchangeRateService;
	
	@Autowired
	private WxUserInfoService wxUserInfoService;
	
	/**
	 * @Title: integral2Money 
	 * @Description: 积分转换货币,定时执行
	 *  每日的凌晨零点十分
	 * void 
	 * @author yangzp
	 * @date 2018年11月19日下午5:19:45
	 **/ 
	@Scheduled(cron = "0 10 0 * * ?")
	//@Scheduled(cron = "0 0/5 * * * ?")
	public void integral2Money() {
		log.info("积分转换货币开始："+ new Date());
		long start = System.currentTimeMillis();
		Date date = DateUtils.getFrontDay(new Date());
		List<MoneyRecord> moneys = integralRecordService.getAmount(new java.sql.Date(date.getTime()));
		if(CollectionUtils.isNotEmpty(moneys)) {
			log.info("转换记录数为："+moneys.size());
			for(MoneyRecord mr : moneys) {
				Long userId = mr.getUserId();
				BigDecimal amount = mr.getAmount();
				if(amount != null) {//有可转换的金额
					//从数据库中获取积分转化率
					ExchangeRate exchangeRate = exchangeRateService.getValidRate();
					//获取可转化金额
					BigDecimal result = amount.divide(exchangeRate.getRate(), 2 ,BigDecimal.ROUND_HALF_DOWN);
					//判断积分是否大于0，大于0时转换
					if(result.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					
					//是否有推荐码
					WxUserInfo wxUser = wxUserInfoService.findById(userId);
					if(StringUtils.isNotEmpty(wxUser.getRecommendCode())) {//有推荐码
						//获取推荐用户
						WxUserInfo recommendUser = wxUserInfoService.findByRecommendCodeAndRemoved(wxUser.getRecommendCode(), false);
						if(recommendUser != null) {
							//计算推荐金
							BigDecimal recommendMoney = result.divide(exchangeRate.getRecommendRate(),2,BigDecimal.ROUND_HALF_DOWN);
							//推荐金额大于一分
							if(recommendMoney.compareTo(BigDecimal.ZERO) > 0) {
								//给推荐用户增加推荐金
								addMoney(recommendUser.getId(), recommendMoney);
								//扣除推荐金
								result = result.subtract(recommendMoney);
							}
						}
					}
					
					//转换自己的金额
					addMoney(userId, result);
					
					//修改积分记录的settled为true
					integralRecordService.updateSettled(exchangeRate.getRate()+"",userId);
				}
			}
		}
		long end = System.currentTimeMillis(); 
		log.info("积分转换货币结束===运行时间:"+(end - start)+"毫秒");
	}
	
	/**
	 * @Title: addMoney 
	 * @Description: 给用户转换金额 
	 * @param userId 用户id
	 * @param result 待转换金额
	 * void 
	 * @author yangzp
	 * @date 2018年11月26日上午11:24:49
	 **/ 
	private void addMoney(Long userId, BigDecimal result) {
		UserCoffers userCoffers = userCoffersService.findByUserID(userId);
		if(userCoffers != null) {//已有记录
			userCoffers.setAmount(userCoffers.getAmount().add(result));
		}else {//第一次
			userCoffers = new UserCoffers();
			userCoffers.setAmount(result);
			userCoffers.setUserId(userId);
		}
		userCoffersService.save(userCoffers);
	}
}
