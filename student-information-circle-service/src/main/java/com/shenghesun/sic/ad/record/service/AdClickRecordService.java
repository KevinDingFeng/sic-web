package com.shenghesun.sic.ad.record.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shenghesun.sic.ad.record.dao.AdClickRecordDao;
import com.shenghesun.sic.ad.record.entity.AdClickRecord;

@Service
public class AdClickRecordService {
	
	@Autowired
	private AdClickRecordDao adClickRecordDao;

	public AdClickRecord save(AdClickRecord entity) {
		return adClickRecordDao.save(entity);
	}

}
