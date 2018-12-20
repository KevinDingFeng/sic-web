package com.shenghesun.sic.cost.record.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.cost.record.entity.ExtractionRecord;

@Repository
public interface ExtractionRecordDao extends JpaRepository<ExtractionRecord, Long>, JpaSpecificationExecutor<ExtractionRecord> {
	/**
	 * 查看提现记录
	 * @Title: findByUserId 
	 * @Description: TODO 
	 * @param userId
	 * @return  List<ExtractionRecord> 
	 * @author yangzp
	 * @date 2018年11月14日上午9:52:49
	 **/ 
	Page<ExtractionRecord> findByUserId(Long userId, Pageable pageable);
}
