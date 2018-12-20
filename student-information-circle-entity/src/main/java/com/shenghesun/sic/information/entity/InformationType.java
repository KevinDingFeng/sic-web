package com.shenghesun.sic.information.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shenghesun.sic.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 信息类型
 * 	推荐 Recommend active=false 不对外开放
 * 	头条	Headlines
 * 	教育	Education
 *	影视 Movies
 * 	电竞	ESports
 * 	星座	Constellation
 * @author kevin
 *
 */
@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { "informations" })
@EqualsAndHashCode(callSuper = true, exclude = { "informations" })
public class InformationType extends BaseEntity {
	/**
	 * 类型编码
	 */
	@Column(nullable = false, length = 32)
	private String code;
	
	/**
	 * 类型名称 
	 */
	@Column(nullable = false, length = 32)
	private String name;
	
	/**
	 * 是否对外开放
	 * 	可用状态为 true
	 * 在编辑新闻选择类型时，取该值为 true 的记录
	 */
	private boolean active = true;
	
	/**
	 * 是否已删除
	 * 	可用状态为 false
	 * 前端展示类型时，取该值为 false 的记录
	 */
	private boolean removed = false;
	
	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER, mappedBy = "types")
	private Set<Information> informations;

}
