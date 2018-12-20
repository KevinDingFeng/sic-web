package com.shenghesun.sic.cost.record.entity;

import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shenghesun.sic.entity.base.BaseEntity;
import com.shenghesun.sic.wx.entity.WxUserInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
/**
 * 货币记录
 * 	通过用户的积分记录，通过定时机制转出为货币记录，该记录可作为提现操作的依据
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { "user" , "extractionRecord" })
@EqualsAndHashCode(callSuper = true, exclude = { "user", "extractionRecord" })
public class MoneyRecord extends BaseEntity {

	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private WxUserInfo user;
	
	/**
	 * 用户 id
	 */
	@Column(name = "user_id", insertable = false, updatable = false, nullable = false)
	private Long userId;
	
	/**
	 * 金额
	 */
	@Column(nullable = false)
	private BigDecimal amount;
	
	/**
	 * 日期
	 */
	private Date date;
	
	/**
	 * 是否已提现
	 * 	为 true 时，说明该记录转化为提现记录
	 */
	private boolean settled = false;
	
	
	@JsonIgnore
//	@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
	@ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "extraction_record_id", nullable = true)
	private ExtractionRecord extractionRecord;
	
	/**
	 * 兑换生成的对应记录,该条 积分记录 转化后，才会拥有自己的货币记录值，即通过该属性，可以明白货币记录的生成依据
	 * 
	 */
	@Column(name = "extraction_record_id", insertable = false, updatable = false, nullable = true)
	private Long extractionRecordId;
	
	/**
	 * 转换率：1600 1(分):1600(积分)
	 */
	private String rate;
	
	public MoneyRecord() {}

	public MoneyRecord(Long userId, BigDecimal amount) {
		super();
		this.userId = userId;
		this.amount = amount;
	}
	
}
