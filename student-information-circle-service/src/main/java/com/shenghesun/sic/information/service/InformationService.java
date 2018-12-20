package com.shenghesun.sic.information.service;

import java.sql.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shenghesun.sic.information.dao.InformationDao;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.model.InformationCondition;

@Service
@Transactional(readOnly = true)
public class InformationService {
	
	@Autowired
	private InformationDao informationDao;
	
	public Information findByUuid(String uuid) {
		return informationDao.findByUuid(uuid);
	}
	
	public Information findOne(Long id) {
		return informationDao.findOne(id);
	}
	
	@Transactional(readOnly = false)
	public Information save (Information information) {
		return informationDao.save(information);
	}
	
	/**
	 * 按条件查询
	 * @Title: findByConditions 
	 * @Description: TODO 
	 * @param condition
	 * @param pageable
	 * @return  Page<Information> 
	 * @author yangzp
	 * @date 2018年11月7日下午5:19:52
	 **/ 
	public Page<Information> findByConditions(InformationCondition condition, Pageable pageable) {
		return informationDao.findAll(new Specification<Information>() {

			@Override
			public Predicate toPredicate(Root<Information> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = cb.conjunction();
				
				if (condition.getSysUserId() != null) {
					predicate.getExpressions().add(cb.equal(root.get("sysUserId"), condition.getSysUserId()));
				}
				if (StringUtils.isNotEmpty(condition.getNewsId())) {
					predicate.getExpressions().add(cb.like(root.get("id").as(String.class), "%" + condition.getNewsId() + "%"));
				}
				
				if (StringUtils.isNotEmpty(condition.getUserName())) {
					predicate.getExpressions().add(cb.like(root.get("userName"), "%" + condition.getUserName() + "%"));
				}
				
				if(condition.getVerified() != null) {
					if(condition.getVerified()) {
						predicate.getExpressions().add(cb.isTrue(root.<Boolean>get("verified")));
					}else {
						predicate.getExpressions().add(cb.isFalse(root.<Boolean>get("verified")));
					}
				}
				
				if(condition.getUsed() != null) {
					if(condition.getUsed()) {
						predicate.getExpressions().add(cb.isTrue(root.<Boolean>get("used")));
					}else {
						predicate.getExpressions().add(cb.isFalse(root.<Boolean>get("used")));
					}
				}
				
				if (condition.getBeginDate() != null) {
					predicate.getExpressions()
							.add(cb.greaterThanOrEqualTo(root.get("creation").as(Date.class), condition.getBeginDate()));
				}
				
				if (condition.getEndDate() != null) {
					predicate.getExpressions()
							.add(cb.lessThanOrEqualTo(root.get("creation").as(Date.class), condition.getEndDate()));
				}
				
				if (StringUtils.isNotEmpty(condition.getTitle())) {
					predicate.getExpressions().add(cb.like(root.get("title"), "%" + condition.getTitle() + "%"));
				}
				predicate.getExpressions().add(cb.isFalse(root.<Boolean>get("removed")));

				return predicate;
			}
		}, pageable);
	}
	/**
	 * 组合查询 统一的调用方法
	 * @param spec
	 * @param pageable
	 * @return
	 */
	public Page<Information> findBySpecification(Specification<Information> spec, Pageable pageable) {
		return informationDao.findAll(spec, pageable);
	}

	public Long countByVerifiedAndUsedAndRemoved(boolean verified, boolean used, boolean removed) {
		return informationDao.countByVerifiedAndUsedAndRemoved(verified, used, removed);
	}

	@Transactional(readOnly = false)
	public int updateUsedByUuidIn(String[] uuids) {
		return informationDao.updateUsedByUuidIn(uuids);
	}

	public Long countByVerifiedAndUsedAndRemovedAndIdIn(boolean verified, boolean used, boolean removed, List<Long> inforIds) {
		return informationDao.countByVerifiedAndUsedAndRemovedAndIdIn(verified, used, removed, inforIds);
	}
}
