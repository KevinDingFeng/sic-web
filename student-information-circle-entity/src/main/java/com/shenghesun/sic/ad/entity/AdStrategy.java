package com.shenghesun.sic.ad.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shenghesun.sic.ad.model.LimitType;
import com.shenghesun.sic.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 广告策略
 * 	用于控制投放
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { "ads" })
@EqualsAndHashCode(callSuper = true, exclude = { "ads" })
public class AdStrategy extends BaseEntity {
	
	/**
	 * 点击的次数阀值
	 */
	private int clickLimitNum = 9999;
	
	/**
	 * 统计阀值的时间范围
	 */
	@Column(nullable = false, length = 64)
	@Enumerated(EnumType.STRING)
	private LimitType limitType = LimitType.No;
	
	/**
	 * 着陆地址
	 */
	@Column(nullable = false, length = 256)
	private String landingPageUrl;

	/**
	 * 是否已开启
	 */
	private boolean active = true;

	/**
	 * 是否已删除
	 */
	private boolean removed = false;
	
	/**
	 * 投放状态
	 */
	@Column(nullable = false, length = 64)
	@Enumerated(EnumType.STRING)
	private Status status;
	
	public enum Status{
		Open("投放中"), Stop("停止");
		
		private String text;
		
		public String getText() {
			return this.text;
		}
		private Status(String text) {
			this.text = text;
		}
	}
	
	/**
	 * 投放状态说明
	 */
	@Column(nullable = true, length = 125)
	private String statusReason;
	
	/**
	 * 投放策略对应的广告
	 */
	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "strategy_ad_rel", inverseJoinColumns = { @JoinColumn(name = "ad_id") }, joinColumns = {
			@JoinColumn(name = "strategy_id") })
	private Set<Ad> ads;
}
