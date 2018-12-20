package com.shenghesun.sic.cost.record.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.cost.record.entity.IntegralRecord;
import com.shenghesun.sic.cost.record.entity.MoneyRecord;

@Repository
public interface IntegralRecordDao extends JpaRepository<IntegralRecord, Long>, JpaSpecificationExecutor<IntegralRecord> {

	@Query("select sum(ir.amount) from IntegralRecord ir where ir.userId=?1 and ir.date=?2")
	BigDecimal sumAmountByUserIdAndDate(Long userId, Date date);
	
	/**
	 * 获取总积分
	 * @Title: findTotalIntegral 
	 * @Description: TODO 
	 * @param userId
	 * @return  BigDecimal 
	 * @author yangzp
	 * @date 2018年11月13日下午5:34:33
	 **/ 
	@Query("select sum(ir.amount) from IntegralRecord ir where ir.userId=?1 and ir.settled=0 and ir.effective=1")
	BigDecimal findTotalIntegral(Long userId);
	
	/**
	 * 获取积分明细
	 * @Title: findByUserIdAndEffective 
	 * @Description: TODO 
	 * @param userId
	 * @param effective
	 * @return  List<IntegralRecord> 
	 * @author yangzp
	 * @date 2018年11月13日下午6:16:04
	 **/ 
	Page<IntegralRecord> findByUserIdAndEffective(Long userId, boolean effective, Pageable pageable);
	
	/**
	 * @Title: getAmount 
	 * @Description: 获取所有用户某一时间的有效积分 
	 * @return  List<MoneyRecord> 
	 * @author yangzp
	 * @date 2018年11月19日下午6:19:56
	 **/ 
	@Query("select new com.shenghesun.sic.cost.record.entity.MoneyRecord(ir.userId,sum(ir.amount))  from IntegralRecord ir where ir.settled=0 and ir.effective=1 and ir.date=?1 group by ir.userId")
	List<MoneyRecord> getAmount(Date date);
	
	/**
	 * @Title: updateSettled 
	 * @Description: 根据用户id修改 是否转化为货币标志
	 * @param rate 积分转换率
	 * @param userId  
	 * void 
	 * @author yangzp
	 * @date 2018年11月20日下午1:58:12
	 **/ 
	@Modifying
	@Query("UPDATE IntegralRecord ir SET ir.settled=true, ir.rate=?1 where ir.userId=?2 and ir.settled=false and ir.effective=true")
	void updateSettled(String rate, Long userId);
}
