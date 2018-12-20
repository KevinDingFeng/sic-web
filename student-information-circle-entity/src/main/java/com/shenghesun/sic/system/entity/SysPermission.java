package com.shenghesun.sic.system.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shenghesun.sic.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 权限
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { "roles" })
@EqualsAndHashCode(callSuper = true, exclude = { "roles" })
public class SysPermission extends BaseEntity {
	

	/**
	 * 权限的具体值，用于功能权限判断
	 */
	@Column(length = 255, nullable = false, unique = true)
	private String perm;
	/**
	 * 权限名称，对应每个功能
	 */
	@Column(length = 64, nullable = false, unique = true)
	private String name;
	/**
	 * 权限分类，一版按照模块划分
	 */
	@Column(length = 64, nullable = false)
	private String category;

	/**
	 * 备注
	 */
	@Column(length = 255, nullable = true)
	private String remark;
	/**
	 * 序列号，暂时没使用
	 */
	@Column(nullable = false)
	private int seqNum;

	/**
	 * 权限所属的角色
	 */
	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "permissions")
	private Set<SysRole> roles;

}
