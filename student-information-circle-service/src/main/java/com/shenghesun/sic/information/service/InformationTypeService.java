package com.shenghesun.sic.information.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shenghesun.sic.information.dao.InformationTypeDao;
import com.shenghesun.sic.information.entity.InformationType;

@Service
public class InformationTypeService {

	@Autowired
	private InformationTypeDao informationTypeDao;

	/**
	 * 根据 removed 属性获取类型列表
	 * 	可以用于前端组装新闻流和列表展示使用 bool = false
	 * @param bool
	 * @return
	 */
	public List<InformationType> findByRemoved(boolean bool) {
		return informationTypeDao.findByRemoved(bool);
	}

	/**
	 * 根据 active 和 removed 两个属性获取类型列表
	 * 	可以用于编辑新闻时，选择新闻所属类型 active=true,removed=false
	 * @param active
	 * @param removed
	 * @return
	 */
	public List<InformationType> findByActiveAndRemoved(boolean active, boolean removed){
		return informationTypeDao.findByActiveAndRemoved(active, removed);
	}
	/**
	 * 获取默认的信息类型，推荐
	 * @return
	 */
	public String getDefaultType() {
		return "Recommend";
	}

	public InformationType findById(long id) {
		return informationTypeDao.findOne(id);
	}

	public InformationType save(InformationType entity) {
		return informationTypeDao.save(entity);
	}
}
