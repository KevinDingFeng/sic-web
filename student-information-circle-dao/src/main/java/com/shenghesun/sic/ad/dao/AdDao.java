package com.shenghesun.sic.ad.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.ad.entity.Ad;
import com.shenghesun.sic.ad.entity.Ad.Subject;

@Repository
public interface AdDao extends JpaRepository<Ad, Long>, JpaSpecificationExecutor<Ad> {

	Ad findByIdAndSubject(long id, Subject subject);
	
}
