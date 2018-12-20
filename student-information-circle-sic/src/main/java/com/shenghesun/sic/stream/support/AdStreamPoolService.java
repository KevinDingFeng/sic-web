package com.shenghesun.sic.stream.support;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.shenghesun.sic.ad.entity.AdStrategy;
import com.shenghesun.sic.ad.entity.AdStrategy.Status;
import com.shenghesun.sic.ad.service.AdStrategyService;

import lombok.extern.slf4j.Slf4j;
/**
 * 广告流数据池模块
 * 	负责通知广告流数据组装模块，并为其提供数据
 * @author kevin
 *
 */
@Service
@Slf4j
public class AdStreamPoolService {
	
	@Autowired
	private AdStrategyService adStrategyService;

	public List<AdStrategy> getAdStrategies() {
		List<AdStrategy> list = adStrategyService.findBySpecification(this.getOpeningSpecification());
		return list;
	}
	
	/**
	 * 根据特殊的需求 整理组合查询条件
	 * 	已开启 未删除 且正在投放中的策略
	 * @return
	 */
	private Specification<AdStrategy> getOpeningSpecification() {
		return new Specification<AdStrategy>() {
			@Override
			public Predicate toPredicate(Root<AdStrategy> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				list.add(cb.equal(root.get("removed"), false));
				list.add(cb.equal(root.get("active"), true));
				list.add(cb.equal(root.get("status").as(Status.class), Status.Open));
				
				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));
			}
		};
	}
	/**
	 * 策略的点击次数增加一次
	 * @param sid
	 */
	public void addClick(Long sid) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				//TODO 修改点击测试统计，涉及到广告策略投放
				log.info("notify ad click " + sid);
			}
		}.start();
	}

}
