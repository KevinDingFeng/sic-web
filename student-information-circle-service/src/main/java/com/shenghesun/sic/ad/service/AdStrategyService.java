package com.shenghesun.sic.ad.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.shenghesun.sic.ad.dao.AdStrategyDao;
import com.shenghesun.sic.ad.entity.AdStrategy;

@Service
public class AdStrategyService {
	
	@Autowired
	private AdStrategyDao adStrategyDao;

	public List<AdStrategy> findBySpecification(Specification<AdStrategy> spec) {
		return adStrategyDao.findAll(spec);
	}

	public AdStrategy save(AdStrategy entity) {
		return adStrategyDao.save(entity);
	}

}
