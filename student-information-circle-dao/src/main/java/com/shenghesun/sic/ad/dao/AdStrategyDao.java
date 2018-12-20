package com.shenghesun.sic.ad.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.ad.entity.AdStrategy;

@Repository
public interface AdStrategyDao extends JpaRepository<AdStrategy, Long>, JpaSpecificationExecutor<AdStrategy> {

}
