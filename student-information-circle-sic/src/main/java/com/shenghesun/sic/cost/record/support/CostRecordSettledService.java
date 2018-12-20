package com.shenghesun.sic.cost.record.support;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shenghesun.sic.cost.record.entity.CostRecord;
import com.shenghesun.sic.cost.record.entity.IntegralRecord;
import com.shenghesun.sic.cost.record.entity.CostRecord.OperationType;
import com.shenghesun.sic.cost.record.entity.IntegralRecord.Source;
import com.shenghesun.sic.cost.record.service.CostRecordService;
import com.shenghesun.sic.cost.record.service.IntegralRecordService;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.service.InformationService;
import com.shenghesun.sic.wx.service.WxUserInfoService;

@Service
public class CostRecordSettledService {

	@Autowired
	private CostRecordService costRecordService;
	@Autowired
	private IntegralRecordService integralRecordService;
	@Autowired
	private WxUserInfoService wxUserInfoService;
	@Autowired
	private InformationService informationService;
//	@Autowired
//	private SysUserService sysUserService;

	public BigDecimal settledCostRecord(Long userId, String uuid) {
		// 读取 cost record
		List<CostRecord> list = costRecordService
				.findBySpecification(costRecordService.getNoSettledSpecification(userId));
		Long time = costRecordService.calculationTime(list);
		if (time > costRecordService.getDefaultUnitTime()) {
			// 满足组合条件
			List<CostRecord> used = this.getUsedCostRecord(list);
			// 整理出 integral record
			IntegralRecord integralRecord = this.getDefaultIntegralRecord(userId, uuid);
			// 保持 integral
			integralRecord = integralRecordService.save(integralRecord);
			// 修改 cost
			this.updateCostRecordIntegral(used, integralRecord);
			return integralRecord.getAmount();
		}
		return BigDecimal.ZERO;
	}

	private void updateCostRecordIntegral(List<CostRecord> used, IntegralRecord integralRecord) {
		for(CostRecord cr : used) {
			cr.setIntegralRecord(integralRecord);
			cr.setIntegralRecordId(integralRecord.getId());
			cr.setSettled(true);//标记 已处理
			cr.setEffective(true);// 标记 有效
			cr.setMatched(true); // 标记 存在匹配项
			//TODO 是否有效和是否存在匹配项，不应该在这里处理，目前还没有精力给他们找合适的位置，暂时这样一步处理完成
			costRecordService.save(cr);
		}
	}

	/**
	 * 确认生成积分记录，需要的 计费记录
	 * @param list
	 * @return
	 */
	public List<CostRecord> getUsedCostRecord(List<CostRecord> list) {
		List<CostRecord> used = new ArrayList<>(); 
		// 暂时保存 开始和技术记录的集合，key = seqNum , value = cr
		Map<String, Map<Long, CostRecord>> map = costRecordService.getStartAndStopCostRecord(list);
		Map<Long, CostRecord> startMap = map.get("start");
		Map<Long, CostRecord> stopMap = map.get("stop");
		Long time = 0L;
		Iterator<Long> its = stopMap.keySet().iterator();
		while (its.hasNext()) {
			Long key = its.next();
			CostRecord startCr = null;// 结束记录对应的开始记录
			if ((startCr = startMap.get(key)) != null) {//存在开始记录
				// 存在，结束记录的时间戳-开始记录的时间戳=间隔的时间，该时间值叠加，等于最后的返回值
				time += stopMap.get(key).getTime() - startCr.getTime();
				CostRecord stopCr = stopMap.get(key);
				used.add(startCr);
				used.add(stopCr);
//				if(time >= UNIT_TIME) {//因为顺序需要通过 seqNum控制，暂时没有添加该逻辑 TODO
//					break;
//				}
			}
		}
		return used;
	}

	private IntegralRecord getDefaultIntegralRecord(Long userId, String uuid) {
		IntegralRecord ir = new IntegralRecord();
		ir.setUserId(userId);
		ir.setUser(wxUserInfoService.findById(userId));
//		ir.setAmount(integralRecordService.getDefaultAmount());
		ir.setAmount(informationService.findByUuid(uuid).getAmount());
		ir.setDate(new Date(System.currentTimeMillis()));
		ir.setSettled(false);
		ir.setEffective(true);// TODO 有效性校验，在调用 处理方法之前，已经通过了校验，暂时没有再次校验的需求
		ir.setSource(Source.Cost);
		return ir;
	}

	/**
	 * 判断当前信息是否可以为当前用户进行计费操作
	 * @param userId
	 * @param uuid
	 * @return
	 */
	public boolean canStart(Long userId, String uuid) {
		//判断信息自身是否可以计费
		if(StringUtils.isBlank(uuid)) {
			return false;
		}
		Information infor = informationService.findByUuid(uuid);
		if(infor == null || !infor.isCost()) {
			return false;
		}
		//判断计费记录的数量是否小于信息设定的数量 直接使用结束请求判断
		//单个用户计费次数
		Integer count = costRecordService.countByUserIdAndInformationIdAndOperationType(userId, infor.getId(), OperationType.Stop);
		return count != null && count < infor.getCostTimes();
	}

}
