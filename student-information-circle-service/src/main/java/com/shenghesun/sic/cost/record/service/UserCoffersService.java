package com.shenghesun.sic.cost.record.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shenghesun.sic.cost.record.dao.UserCoffersDao;
import com.shenghesun.sic.cost.record.entity.UserCoffers;

@Transactional(readOnly = true)
@Service
public class UserCoffersService {

	@Autowired
	private UserCoffersDao userCoffersDao;

	/**
	 * @Title: getMaxSeqNumByUserId 
	 * @Description: 获取用户可提取金额  
	 * @param userId
	 * @return  BigDecimal 
	 * @author yangzp
	 * @date 2018年11月20日上午10:45:41
	 **/ 
	public BigDecimal getMaxSeqNumByUserId(Long userId) {
		return userCoffersDao.getAmountByUserId(userId);
	}
	
	@Transactional(readOnly = false)
	public UserCoffers save(UserCoffers entity) {
		return userCoffersDao.save(entity);
	}
	
	public UserCoffers findByUserID(Long userId) {
		return 	userCoffersDao.findByUserId(userId);
	}
}
