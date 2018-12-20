package com.shenghesun.sic.cost.record.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.cost.record.entity.CostRecord;
import com.shenghesun.sic.cost.record.entity.CostRecord.OperationType;

@Repository
public interface CostRecordDao extends JpaRepository<CostRecord, Long>, JpaSpecificationExecutor<CostRecord> {

	List<CostRecord> findByUserIdAndSeqNumAndOperationType(Long userId, Long seqNum, OperationType op);

	@Query("select max(cr.seqNum) from CostRecord cr where cr.userId=?1")
	Long getMaxSeqNumByUserId(Long userId);

	Integer countByUserIdAndInformationIdAndOperationType(Long userId, Long informationId, OperationType type);

}
