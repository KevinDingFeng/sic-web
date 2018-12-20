package com.shenghesun.sic.ad.record.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.shenghesun.sic.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 用户点击记录
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = {})
@EqualsAndHashCode(callSuper = true, exclude = {})
public class AdClickRecord extends BaseEntity {

	/**
	 * 投放策略的 id 
	 */
	private Long adStrategyId;
	
	/**
	 * 点击广告的唯一标识
	 */
	private String adUuid;
	
	/**
	 * 点击发生的用户 id 
	 * 	目前小程序使用微信用户的 id ，对应 wx_user_info 的 id 字段
	 */
	private Long userId;
	
//	/**
//	 * 点击发生的用户
//	 */
//	private Long sysUserId;
	
//	/**
//	 * 登录名 用户名
//	 * 点击发生的用户
//	 */
//	@Column(nullable = false, length = 64)
//	private String account;
	
}
