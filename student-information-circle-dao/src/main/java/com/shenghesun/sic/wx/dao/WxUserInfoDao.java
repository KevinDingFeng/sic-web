package com.shenghesun.sic.wx.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.shenghesun.sic.wx.entity.WxUserInfo;


@Repository
public interface WxUserInfoDao extends JpaRepository<WxUserInfo, Long>, JpaSpecificationExecutor<WxUserInfo> {

	WxUserInfo findByOpenId(String openId);

	WxUserInfo findByIdAndOpenId(Long id, String openId);
	
	/**
	 * @Title: findByRecommendCodeAndRemoved 
	 * @Description: 根据推荐码查询用户 
	 * @param recommendCode
	 * @param removed
	 * @return  WxUserInfo 
	 * @author yangzp
	 * @date 2018年11月26日上午11:16:22
	 **/ 
	WxUserInfo findByRecommendCodeAndRemoved(String recommendCode, Boolean removed);

}
