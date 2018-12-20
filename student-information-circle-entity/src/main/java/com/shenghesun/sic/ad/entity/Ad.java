package com.shenghesun.sic.ad.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shenghesun.sic.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 广告
 * @author kevin
 *
 */

@Entity
@Table
@Data
@ToString(callSuper = true, exclude = { "adStrategies" })
@EqualsAndHashCode(callSuper = true, exclude = { "adStrategies" })
public class Ad extends BaseEntity {

	/**
	 * 广告 唯一 标识，代替之前使用的 id 值
	 * 	为了防止 用户通过 域名拼接直接攻破接口
	 */
	@Column(nullable = false, length = 125)
	private String uuid;
	
	/**
	 * 标题
	 */
	@Column(nullable = true, length = 256)
	private String title;
	
	/**
	 * 列表展示使用的图片路径，一条或者三条按照
	 * 	只存储相对路径，使用 "," 分隔
	 */
	@Column(nullable = false, length = 256)
	private String imgs;
	

	/**
	 * 广告隶属
	 * 	软文
	 * 	普通
	 */
	@Column(nullable = false, length = 64)
	@Enumerated(EnumType.STRING)
	private Subject subject = Subject.Common;
	
	public enum Subject{
		Common("普通"), Flexible("软文");
		
		private String text;
		
		public String getText() {
			return this.text;
		}
		private Subject(String text) {
			this.text = text;
		}
	}

	/**
	 * 是否已删除
	 */
	private boolean removed = false;
	
	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "ads")
	private Set<AdStrategy> adStrategies;
	
}
