package com.shenghesun.sic.ad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shenghesun.sic.ad.dao.AdDao;
import com.shenghesun.sic.ad.entity.Ad;
import com.shenghesun.sic.ad.entity.Ad.Subject;

@Service
public class AdService {
	
	@Autowired
	private AdDao adDao;

	public Ad save(Ad entity) {
		return adDao.save(entity);
	}

	public Ad findByIdAndSubject(long id, Subject subject) {
		return adDao.findByIdAndSubject(id, subject);
	}
}
