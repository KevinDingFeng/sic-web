package com.shenghesun.sic.wx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.shenghesun.sic.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 普通用户
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = {  })
@EqualsAndHashCode(callSuper = true, exclude = { })
public class WxUserInfo extends BaseEntity {

//	@JsonIgnore
//	@ManyToOne(cascade = { javax.persistence.CascadeType.REFRESH }, fetch = javax.persistence.FetchType.LAZY)
//	@JoinColumn(name = "sys_user_id", nullable = false)
//	private SysUser sysUser;
//	
//	/**
//	 * 用户 id
//	 */
//	@Column(name = "sys_user_id", insertable = false, updatable = false, nullable = false)
//	private Long sysUserId;
	
	/**
	 * 微信用户唯一的 open id
	 */
	@Column(nullable = false, length = 255)
	private String openId;
	
	@Column(nullable = true, length = 64)
	private String country;
	@Column(nullable = true, length = 64)
	private String province;
	@Column(nullable = true, length = 64)
	private String city;
	private int sex = 1;//1 标识男性
	@Column(nullable = true, length = 255)
	private String nickName;
	@Column(nullable = true, length = 255)
	private String headImgUrl;
	@Column(nullable = true, length = 64)
	private String language;
	@Column(nullable = true, length = 255)
	private String privilege;
	/**
	 * 联系方式，用于判断用户是否补全了信息
	 * 	同 bankAccount ，两者都存在，则为 已经补全，否则视为信息不完整
	 */
	@Column(nullable = true, length = 32)
	private String cellphone;
	/**
	 * 联系方式是否通过了校验
	 */
	private boolean cellphoneVerified = false;
	/**
	 * 收款账户，用于判断用户是否补全了信息
	 * 	同 cellphone ，两者都存在，则为 已经补全，否则视为信息不完整
	 */
	@Column(nullable = true, length = 32)
	private String bankAccount;
	/**
	 * 是否已删除
	 */
	private boolean removed = false;
	
	/**
	 * 编码，数据库唯一存在
	 * 	属于用户自己，作为别人的推荐码使用
	 */
	@Column(nullable = true, length = 64)
	private String code;
	/**
	 * 推荐码，与自己的推荐人的 code 匹配
	 */
	@Column(nullable = true, length = 64)
	private String recommendCode;
}
