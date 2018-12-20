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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shenghesun.sic.entity.base.BaseEntity;
import com.shenghesun.sic.wx.entity.WxUserInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 积分记录
 * 	通过计费记录，匹配组合得出该记录信息
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { "user" , "moneyRecord" })
@EqualsAndHashCode(callSuper = true, exclude = { "user", "moneyRecord"})
public class IntegralRecord extends BaseEntity {

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
	 * 积分金额
	 * 	正整数
	 */
	@Column(nullable = false)
	private BigDecimal amount;
	
	/**
	 * 日期
	 */
	private Date date;
	
	/**
	 * 是否转化为货币
	 * 	为 true 时，说明该记录转化为货币记录
	 */
	private boolean settled = false;
			
	/**
	 * 是否有效，比如超过最大额度，会被确定为无效
	 * 	为 true 时表示该记录通过了 有效 校验（校验方法待定）
	 * 		校验条件之一，是否超过了每日每用户的限定额度
	 */
	private boolean effective = false;
	
	/**
	 * 积分的来源
	 */
	@Column(nullable = false, length = 64)
	@Enumerated(EnumType.STRING)
	private Source source;
	
	@Transient
	private String sourceText;
	/**
	 * 重写get方法 返回text
	 * @Title: getSource 
	 * @Description: TODO 
	 * @return  String 
	 * @author yangzp
	 * @date 2018年11月14日下午5:06:15
	 **/ 
	public String getSourceText() {
		return source.getText();
	}
	
	public enum Source{
		Sign("签到"), Cost("计费兑换"), Reward("奖励");
		
		private String text;
		
		public String getText() {
			return this.text;
		}
		private Source(String text) {
			this.text = text;
		}
	}
	
	@JsonIgnore
//	@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
	@ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "money_record_id", nullable = true)
	private MoneyRecord moneyRecord;
	
	/**
	 * 兑换生成的对应记录,该条 积分记录 转化后，才会拥有自己的货币记录值，即通过该属性，可以明白货币记录的生成依据
	 * 
	 */
	@Column(name = "money_record_id", insertable = false, updatable = false, nullable = true)
	private Long moneyRecordId;
	
	@Column(length=10)
	private String rate;
}
