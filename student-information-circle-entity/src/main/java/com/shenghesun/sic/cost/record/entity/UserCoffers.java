package com.shenghesun.sic.cost.record.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.shenghesun.sic.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
 

/**
  * @ClassName: UserCoffers 
  * @Description: 用户金库
  * @author: yangzp
  * @date: 2018年11月19日 下午6:09:36  
  */
@Entity
@Table
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserCoffers extends BaseEntity {
	
	/**
	 * 用户 id
	 */
	@Column(nullable = false)
	private Long userId;
	
	/**
	 * 可提取金额
	 */
	@Column(nullable = false)
	private BigDecimal amount;
	
	/**
	 * 已提取金额
	 */
	@Column(nullable = true)
	private BigDecimal withdrAwnamount = new BigDecimal(0.00);

}
