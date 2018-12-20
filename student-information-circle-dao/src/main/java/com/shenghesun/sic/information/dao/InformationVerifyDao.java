package com.shenghesun.sic.information.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.information.entity.InformationVerify;

@Repository
public interface InformationVerifyDao extends JpaRepository<InformationVerify, Long>, JpaSpecificationExecutor<InformationVerify> {
	/**
	 * 获取新闻审核的最后一次序列值
	 * @Title: findMaxByInformationId 
	 * @Description: TODO 
	 * @param informationId
	 * @return  Long 
	 * @author yangzp
	 * @date 2018年11月12日下午6:08:51
	 **/ 
	@Query("SELECT max(seqNum) FROM InformationVerify o where o.informationId=?1")
	Integer findMaxByInformationId(Long informationId);
}
