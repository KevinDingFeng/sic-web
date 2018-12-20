package com.shenghesun.sic.cost.record.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.cost.record.entity.MoneyRecord;

@Repository
public interface MoneyRecordDao extends JpaRepository<MoneyRecord, Long>, JpaSpecificationExecutor<MoneyRecord> {

}
