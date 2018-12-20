package com.shenghesun.sic.cost.record.entity;

import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * 提现记录
 * 	用户申请提现后，生成该记录，当提现操作结束后，修改该记录的值
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { "user"})
@EqualsAndHashCode(callSuper = true, exclude = { "user" })
public class ExtractionRecord extends BaseEntity {

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
	 * 提现金额
	 */
	@Column(nullable = false)
	private BigDecimal amount;
	
	/**
	 * 日期
	 */
	private Date date;
	
	
	/**
	 * 提现状态
	 */
	@Column(nullable = false, length = 64)
	@Enumerated(EnumType.STRING)
	private ExtractionState extractionState;
	
	public enum ExtractionState{
		Processing("处理中"), Reject("驳回"), Complete("已完成");
		
		private String text;
		
		public String getText() {
			return this.text;
		}
		private ExtractionState(String text) {
			this.text = text;
		}
	}

}
