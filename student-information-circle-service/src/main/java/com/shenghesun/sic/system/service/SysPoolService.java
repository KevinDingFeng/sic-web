package com.shenghesun.sic.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shenghesun.sic.system.dao.SysPoolDao;
import com.shenghesun.sic.system.entity.SysPool;

@Service
public class SysPoolService {

	@Autowired
	private SysPoolDao sysPoolDao;
	
//	
//	public List<SysPool> findAll(){
//		return sysPoolDao.findAll();
//	}


	public SysPool findById(Long id) {
		return sysPoolDao.getOne(id);
	}

}
