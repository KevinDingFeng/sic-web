package com.shenghesun.sic.information.entity;

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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 信息审核
 * 	一条信息对应多条信息审核记录，每次审核 Fail 后，重新提交审核都新插入记录。
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { "information" })
@EqualsAndHashCode(callSuper = true, exclude = { "information" })
public class InformationVerify extends BaseEntity {

	@JsonIgnore
//	@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
	@ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "information_id", nullable = false)
	private Information information;
	
	/**
	 * 对应的信息数据
	 */
	@Column(name = "information_id", insertable = false, updatable = false, nullable = false)
	private Long informationId;
	
	/**
	 * 当前审核结果
	 */
	@Column(nullable = false, length = 64)
	@Enumerated(EnumType.STRING)
	private VerifyResult verifyResult;
	
	public enum VerifyResult{
		Fail("未通过"), Pass("通过");
		
		private String text;
		
		public String getText() {
			return this.text;
		}
		private VerifyResult(String text) {
			this.text = text;
		}
	}
	
	/**
	 * 当前审核结果说明
	 * 	一般情况下只有 未通过 时才存在该信息
	 */
	@Column(nullable = true, length = 125)
	private String resultRemark;
	
	/**
	 * 序列码
	 * 	单个信息的审核记录从 0 开始叠加，以确认审核记录的生成顺序
	 * 	每次插入新的审核记录时，需要找到同一条 信息 的前一次审核记录的 seqNum 值，并 +1 作为新的值，插入
	 */
	private int seqNum = 0;
	
	/**
	 * 审核人id
	 */
	@Column(nullable = false)
	private Long sysUserId;
	
}
