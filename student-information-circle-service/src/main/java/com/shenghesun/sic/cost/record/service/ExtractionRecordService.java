package com.shenghesun.sic.cost.record.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shenghesun.sic.cost.record.dao.ExtractionRecordDao;
import com.shenghesun.sic.cost.record.entity.ExtractionRecord;

@Service
@Transactional(readOnly = true)
public class ExtractionRecordService {
	@Autowired
	private ExtractionRecordDao extractionRecordDao;
	
	@Transactional(readOnly = false)
	public ExtractionRecord save (ExtractionRecord extractionRecord) {
		return extractionRecordDao.save(extractionRecord);
	}
	
	/**
	 * 查看提现记录
	 * @Title: findByUserId 
	 * @Description: TODO 
	 * @param userId
	 * @param pageable
	 * @return  List<ExtractionRecord> 
	 * @author yangzp
	 * @date 2018年11月14日上午10:22:51
	 **/ 
	public Page<ExtractionRecord> findByUserId(Long userId, Pageable pageable){
		return extractionRecordDao.findByUserId(userId, pageable);
	}
	
}
