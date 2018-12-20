package com.shenghesun.sic.cost.record.entity;

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
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.wx.entity.WxUserInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 计费记录
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { "user" , "information", "integralRecord" })
@EqualsAndHashCode(callSuper = true, exclude = { "user", "information", "integralRecord" })
public class CostRecord extends BaseEntity {

	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private WxUserInfo user;
	
	/**
	 * 用户 id
	 */
	@Column(name = "user_id", insertable = false, updatable = false, nullable = false)
	private Long userId;
	
	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "information_id", nullable = true)
	private Information information;
	
	/**
	 * 信息 id
	 */
	@Column(name = "information_id", insertable = false, updatable = false, nullable = true)
	private Long informationId;
	
	/**
	 * 时间戳
	 * 	long 型，默认为系统时间
	 */
	private Long time = System.currentTimeMillis();
	
	/**
	 * 日期
	 * 	格式是： yyyy-MM-dd
	 */
	private Date date;
	/**
	 * 序列码
	 */
	private Long seqNum = 0L;
	
	/**
	 * 操作类型
	 */
	@Column(nullable = false, length = 64)
	@Enumerated(EnumType.STRING)
	private OperationType operationType;
	
	public enum OperationType{
		Start("开始"),Stop("结束");
		
		private String text;
		
		public String getText() {
			return this.text;
		}
		private OperationType(String text) {
			this.text = text;
		}
	}
	 			
	/**
	 * 是否拥有匹配项
	 * 	当发生的是 结束计费 操作时，如果可以匹配到 开始计费 记录，则修改为 true
	 */
	private boolean matched = false;

	/**
	 * 是否有效
	 * 	为 true 时表示该记录通过了 有效 校验（校验方法待定）
	 * 		校验条件之一，是否超过了每日每用户的限定额度
	 */
	private boolean effective = false;
	
	/**
	 * 是否已统计为积分
	 * 	为 true 时，说明该记录已经匹配完成，并转化为了积分
	 */
	private boolean settled = false;
	
	@JsonIgnore
//	@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
	@ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "integral_record_id", nullable = true)
	private IntegralRecord integralRecord;
	
	/**
	 * 该条 计费记录 转化后，才会拥有自己的积分记录值，即通过该属性，可以明白积分记录的生成依据
	 */
	@Column(name = "integral_record_id", insertable = false, updatable = false, nullable = true)
	private Long integralRecordId;
}
