package com.shenghesun.sic.cost.record.dao;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.cost.record.entity.UserCoffers;

@Repository
public interface UserCoffersDao extends JpaRepository<UserCoffers, Long>, JpaSpecificationExecutor<UserCoffers> {


	/**
	 * @Title: getMaxSeqNumByUserId 
	 * @Description: 获取用户可提取金额 
	 * @param userId
	 * @return  BigDecimal 
	 * @author yangzp
	 * @date 2018年11月20日上午10:43:04
	 **/ 
	@Query("select uc.amount from UserCoffers uc where uc.userId=?1")
	BigDecimal getAmountByUserId(Long userId);
	
	UserCoffers findByUserId(Long userId);
}
