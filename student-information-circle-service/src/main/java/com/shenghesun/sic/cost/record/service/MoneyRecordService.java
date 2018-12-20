package com.shenghesun.sic.cost.record.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shenghesun.sic.cost.record.dao.MoneyRecordDao;
import com.shenghesun.sic.cost.record.entity.MoneyRecord;

@Service
@Transactional(readOnly = true)
public class MoneyRecordService {
	
	@Autowired
	private MoneyRecordDao moneyRecordDao;
	
	public MoneyRecord save (MoneyRecord entity) {
		return moneyRecordDao.save(entity);
	}
	
}
