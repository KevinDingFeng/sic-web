package com.shenghesun.sic.cost.record.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shenghesun.sic.cost.record.dao.IntegralRecordDao;
import com.shenghesun.sic.cost.record.entity.IntegralRecord;
import com.shenghesun.sic.cost.record.entity.MoneyRecord;

@Service
@Transactional(readOnly = true)
public class IntegralRecordService {
	
	@Autowired
	private IntegralRecordDao integralRecordDao;
	
	// 默认值，单个用户每天的 积分总量上限
	private static final BigDecimal AMOUNT_LIMIT = new BigDecimal(6000);
//	 单次 积分兑换的数量，该值需要随时修改变化，TODO
//	private static final BigDecimal AMOUNT_DEFAULT = new BigDecimal(10);
	

//	public BigDecimal getDefaultAmount() {
//		return AMOUNT_DEFAULT;
//	}
	/**
	 * 校验该次请求是否有效，
	 * 	方法暂定为 当前用户当前天的积分上限不超过规定值
	 * 	返回 true 表示有效，可以继续操作
	 * @param userId
	 * @param req
	 * @return
	 */
	public boolean effective(Long userId, HttpServletRequest req) {
		BigDecimal totalIntegral = this.getSumToday(userId);
		return totalIntegral.compareTo(AMOUNT_LIMIT) < 0;
//		return true;
	}

	public BigDecimal getSumToday(Long userId) {
		return this.sumAmountByUserIdAndDate(userId, new Date(System.currentTimeMillis()));
	}
	
	public BigDecimal sumAmountByUserIdAndDate(Long userId, Date date) {
		BigDecimal sum = integralRecordDao.sumAmountByUserIdAndDate(userId, date);
		return sum == null ? BigDecimal.ZERO : sum;
	}

	@Transactional(readOnly = false)
	public IntegralRecord save(IntegralRecord entity) {
		return integralRecordDao.save(entity);
	}

	/**
	 * 获取总积分
	 * @Title: findTotalIntegral 
	 * @Description: TODO 
	 * @param userId 用户id
	 * @return  BigDecimal 
	 * @author yangzp
	 * @date 2018年11月13日下午5:37:03
	 **/ 
	public BigDecimal findTotalIntegral(Long userId) {
		return integralRecordDao.findTotalIntegral(userId);
	}
	
	/**
	 * 获取积分明细
	 * @Title: findByUserIdAndEffective 
	 * @Description: TODO 
	 * @param userId
	 * @param effective
	 * @return  List<IntegralRecord> 
	 * @author yangzp
	 * @date 2018年11月13日下午6:18:41
	 **/ 
	public Page<IntegralRecord> findByUserIdAndEffective(Long userId, boolean effective, Pageable pageable){
		return integralRecordDao.findByUserIdAndEffective(userId, effective, pageable);
	}
	
	
	/**
	 * @Title: getAmount 
	 * @Description: 获取所有用户某一时间的有效积分  
	 * @param date
	 * @return  List<MoneyRecord> 
	 * @author yangzp
	 * @date 2018年11月20日上午10:32:28
	 **/ 
	public List<MoneyRecord> getAmount(Date date){
		return integralRecordDao.getAmount(date);
	}
	
	/**
	 * @Title: updateSettled 
	 * @Description: 根据用户id修改 是否转化为货币标志  
	 * @param rate 积分转换率
	 * @param userId  void 
	 * @author yangzp
	 * @date 2018年11月20日下午2:00:35
	 **/ 
	@Transactional(readOnly = false)
	public void updateSettled(String rate, Long userId) {
		integralRecordDao.updateSettled(rate, userId);
	}
}
