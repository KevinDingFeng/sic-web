package com.shenghesun.sic.cost.record.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.cost.record.entity.ExchangeRate;

@Repository
public interface ExchangeRateDao extends JpaRepository<ExchangeRate, Long>, JpaSpecificationExecutor<ExchangeRate> {

	/**
	 * @Title: getValidRate 
	 * @Description: 获取 seqNum 最大的记录作为兑换标准 
	 * @return  ExchangeRate 
	 * @author yangzp
	 * @date 2018年11月23日下午6:48:30
	 **/ 
	@Query("select er from ExchangeRate er where er.seqNum=(select max(seqNum) from ExchangeRate)")
	ExchangeRate getValidRate();

}
