package com.shenghesun.sic.information.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shenghesun.sic.information.dao.InformationVerifyDao;
import com.shenghesun.sic.information.entity.InformationVerify;

@Service
@Transactional(readOnly = true)
public class InformationVerifyService {
	
	@Autowired
	private InformationVerifyDao informationVerifyDao;
	
	@Transactional(readOnly = false)
	public InformationVerify save (InformationVerify informationVerify) {
		return informationVerifyDao.save(informationVerify);
	}

	/**
	 * 获取新闻审核的最后一次序列值
	 * @Title: findMaxByInformationId 
	 * @Description: TODO 
	 * @param informationId
	 * @return  Integer 
	 * @author yangzp
	 * @date 2018年11月12日下午6:12:25
	 **/ 
	public Integer findMaxByInformationId(Long informationId) {
		return informationVerifyDao.findMaxByInformationId(informationId);
	}
}
