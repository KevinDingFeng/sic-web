package com.shenghesun.sic.ad.record.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.ad.record.entity.AdClickRecord;

@Repository
public interface AdClickRecordDao extends JpaRepository<AdClickRecord, Long>, JpaSpecificationExecutor<AdClickRecord> {

}
