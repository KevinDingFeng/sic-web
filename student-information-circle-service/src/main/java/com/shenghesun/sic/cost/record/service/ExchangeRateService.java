package com.shenghesun.sic.cost.record.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shenghesun.sic.cost.record.dao.ExchangeRateDao;
import com.shenghesun.sic.cost.record.entity.ExchangeRate;

 /**
  * @ClassName: ExchangeRateService 
  * @Description: 兑换比例和推荐提成比例控制
  * @author: yangzp
  * @date: 2018年11月26日 上午9:59:05  
  */
@Transactional(readOnly = true)
@Service
public class ExchangeRateService {

	@Autowired
	private ExchangeRateDao exchangeRateDao;
	
	/**
	 * @Title: getValidRate 
	 * @Description: 获取 seqNum 最大的记录作为兑换标准  
	 * @return  ExchangeRate 
	 * @author yangzp
	 * @date 2018年11月26日上午10:00:48
	 **/ 
	public ExchangeRate getValidRate() {
		return exchangeRateDao.getValidRate();
	}

}
