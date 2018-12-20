package com.shenghesun.sic.information.entity;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotBlank;

import com.shenghesun.sic.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 信息
 * 	即 新闻等文章或图文内容
 * 
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { "types" })
@EqualsAndHashCode(callSuper = true, exclude = { "types" })
public class Information extends BaseEntity {
	
	/**
	 * 信息 唯一 标识，代替之前使用的 id 值
	 * 	为了防止 用户通过 域名拼接直接攻破接口
	 */
	@Column(nullable = false, length = 125)
	private String uuid;

	/**
	 * 标题
	 */
	@NotBlank(message = "标题必填")
	@Column(nullable = false, length = 256)
	private String title;
	
	/**
	 * 内容
	 */
	@NotBlank(message = "内容必填")
	@Column(nullable = false,columnDefinition="MEDIUMTEXT")
	private String context;
	
	/**
	 * 列表展示使用的图片路径，一条或者三条按照
	 * 	只存储相对路径，使用 "," 分隔
	 */
	@Column(length = 256)
	private String imgs;
	
	/**
	 * 是否为计费消息
	 * 	为 true 时，页面打开后，才可以给用户计费
	 */
	private boolean cost = true;
	
	/**
	 * 计费时积分的额度
	 * 	即前端过一个周期，得到的积分数量，预期是逐次递减
	 */
	private BigDecimal amount = new BigDecimal(10);
	
	/**
	 * 计费的次数
	 * 	默认是 3 ，即每条信息单个用户会有三次计费的机会
	 */
	private int costTimes = 3;
	
	/**
	 * 是否已发布
	 * 	为 true 时，表示该信息已经加入到 流模块中，不会参与下一次整理信息流
	 * 	在和类型是多对多的情况下，一条信息只能被其中一个类型收录
	 */
	private boolean used = false;
	
	/**
	 * 是否通过了审核
	 * 	通过审核后，该值该为 true，之后 used 属性才生效
	 */
	private boolean verified = false;
	
	/**
	 * 是否已删除
	 */
	private boolean removed = false;
	
//	@JsonIgnore
////	@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
//	@ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
//	@JoinColumn(name = "sys_user_id", nullable = false)
//	private SysUser sysUser;
	
	/**
	 * 提交者id，该条信息由那个用户提交
	 */
//	@Column(name = "sys_user_id", insertable = false, updatable = false, nullable = false)
	@Column(nullable = false)
	private Long sysUserId;
	
	/**
	 * 提交者name
	 */
	@Column(nullable = false, length = 50)
	private String userName;
	
	
//	@Column(nullable = false, length = 32)
//	private String code;
	/**
	 * 类型 多对多 
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(name = "information_type_rel", inverseJoinColumns = { @JoinColumn(name = "type_id") }, joinColumns = {
			@JoinColumn(name = "information_id") })
	private Set<InformationType> types;
	
	/**
	 * 图片删除标志：1 删除 空不删除
	 */
	@Transient
	private String deleteImageFlag1;
	@Transient
	private String deleteImageFlag2;
	@Transient
	private String deleteImageFlag3;
}
