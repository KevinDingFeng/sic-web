package com.shenghesun.sic.cost.record.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.shenghesun.sic.cost.record.dao.CostRecordDao;
import com.shenghesun.sic.cost.record.entity.CostRecord;
import com.shenghesun.sic.cost.record.entity.CostRecord.OperationType;

@Service
public class CostRecordService {
	
	// 设置默认的计算积分的单位时长，即对用户来说，多少 秒 增加一次积分
	private static final Long UNIT_TIME = 30_000L;
	
	public Long getDefaultUnitTime() {
		return UNIT_TIME;
	}

	@Autowired
	private CostRecordDao costRecordDao;

	public CostRecord save(CostRecord entity) {
		return costRecordDao.save(entity);
	}

	public Long getStartTime(Long userId) {
		// 获取 当前用户 未处理的 记录信息
		List<CostRecord> costs = this.findBySpecification(this.getNoSettledSpecification(userId));
		if (costs != null && costs.size() > 0) {
			// 根据记录计算时长
			Long time = this.calculationTime(costs);
			//如果计算得到的开始时间非法，返回 0L
			return time > UNIT_TIME ? 0L : time;
		}
		return 0L;
	}

	// 区分开 记录中的开始和结束记录
	// 使用结束记录中的 seqNum 值匹配 开始记录，计算时长
	public Long calculationTime(List<CostRecord> costs) {
		// 暂时保存 开始和技术记录的集合，key = seqNum , value = cr
		Map<String, Map<Long, CostRecord>> map = this.getStartAndStopCostRecord(costs);
		Map<Long, CostRecord> startMap = map.get("start");
		Map<Long, CostRecord> stopMap = map.get("stop");
		Long time = 0L;
		Iterator<Long> its = stopMap.keySet().iterator();
		while (its.hasNext()) {
			Long key = its.next();
			CostRecord startCr = null;// 结束记录对应的开始记录
			if ((startCr = startMap.get(key)) != null) {
				// 存在，结束记录的时间戳-开始记录的时间戳=间隔的时间，该时间值叠加，等于最后的返回值
				time += stopMap.get(key).getTime() - startCr.getTime();
			}
		}
		return time;
	}

	public Map<String, Map<Long, CostRecord>> getStartAndStopCostRecord(List<CostRecord> costs) {
		// 暂时保存 开始和技术记录的集合，key = seqNum , value = cr
		Map<Long, CostRecord> startMap = new HashMap<>();
		Map<Long, CostRecord> stopMap = new HashMap<>();
		for (CostRecord cr : costs) {
			if (OperationType.Start.name().equals(cr.getOperationType().name())) {
				// 开始记录
				startMap.put(cr.getSeqNum(), cr);
			} else if (OperationType.Stop.name().equals(cr.getOperationType().name())) {
				// 结束记录
				stopMap.put(cr.getSeqNum(), cr);
			}
		}
		Map<String, Map<Long, CostRecord>> map = new HashMap<>();
		map.put("start", startMap);
		map.put("stop", stopMap);
		return map;
	}

	public List<CostRecord> findBySpecification(Specification<CostRecord> spec) {
		return costRecordDao.findAll(spec);
	}

	/**
	 * 根据特殊的需求 整理组合查询条件
	 * 
	 * @return
	 */
	public Specification<CostRecord> getNoSettledSpecification(Long userId) {
		return new Specification<CostRecord>() {
			@Override
			public Predicate toPredicate(Root<CostRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				list.add(cb.equal(root.get("matched"), false));
				list.add(cb.equal(root.get("settled"), false));
				list.add(cb.equal(root.get("effective"), true));
				list.add(cb.equal(root.get("userId"), userId));

				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));
			}
		};
	}

	public List<CostRecord> findByUserIdAndSeqNumAndOperationType(Long userId, Long seqNum, OperationType op) {
		return costRecordDao.findByUserIdAndSeqNumAndOperationType(userId, seqNum, op);
	}
	
	public Integer countByUserIdAndInformationIdAndOperationType(Long userId, Long informationId, OperationType type) {
		return costRecordDao.countByUserIdAndInformationIdAndOperationType(userId, informationId, type);
	}

	/**
	 * 获取当前用户最大的 seqNum 值
	 * 
	 * @param userId
	 * @return
	 */
	public Long getMaxSeqNumByUserId(Long userId) {
		Long seqNum = costRecordDao.getMaxSeqNumByUserId(userId);
		if(seqNum == null || seqNum < 1L) {
			return 1l;
		}
		return seqNum;
	}

}
