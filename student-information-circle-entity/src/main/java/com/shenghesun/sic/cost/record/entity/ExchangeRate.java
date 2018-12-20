package com.shenghesun.sic.cost.record.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.shenghesun.sic.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 兑换比例和推荐提成比例控制
 * 	每条记录可使用 update 或者 insert ，如果是 insert 则需要把 seqNum 值加1
 * 	读取当前的值时，获取 seqNum 最大的记录作为兑换标准。
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { })
@EqualsAndHashCode(callSuper = true, exclude = { })
public class ExchangeRate extends BaseEntity {

	/**
	 * 序列码
	 */
	private Long seqNum = 0L;
	/**
	 * 兑换比例
	 * 	积分和金额的兑换比例
	 */
	private BigDecimal rate = new BigDecimal(1000);
	/**
	 * 推荐人提成
	 * 	从兑换人的积分中扣除
	 */
	private BigDecimal RecommendRate = new BigDecimal(0.1);
}
