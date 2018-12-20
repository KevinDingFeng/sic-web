package com.shenghesun.sic.information.model;

import java.sql.Date;

import lombok.Data;

 /**
  * 信息查询条件
  * @ClassName: InformationCondition 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年11月7日 下午5:08:27  
  */
@Data
public class InformationCondition {
	
	/**
	 * 信息id
	 */
	private String newsId;
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 是否为计费消息
	 * 	为 true 时，页面打开后，才可以给用户计费
	 */
	private Boolean cost;
	
	/**
	 * 是否已发布
	 * 	为 true 时，表示该信息已经加入到 流模块中，不会参与下一次整理信息流
	 */
	private Boolean used;
	
	/**
	 * 是否通过了审核
	 * 	通过审核后，该值该为 true，之后 used 属性才生效
	 */
	private Boolean verified;
	
	/**
	 * 是否已删除
	 */
	private Boolean removed = false;
	/**
	 * 提交者，该条信息由那个用户提交
	 */
	private String userName;
	
	/**
	 * 提交者id，该条信息由那个用户提交
	 */
	private Long sysUserId;
	
	/**
	 * 开始时间
	 */
	private Date beginDate;
	/**
	 * 结束时间
	 */
	private Date endDate;
	
}
