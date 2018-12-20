package com.shenghesun.sic.cost.record.controller;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.cost.record.entity.CostRecord;
import com.shenghesun.sic.cost.record.entity.CostRecord.OperationType;
import com.shenghesun.sic.cost.record.service.CostRecordService;
import com.shenghesun.sic.cost.record.service.IntegralRecordService;
import com.shenghesun.sic.cost.record.support.CostRecordSettledService;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.service.InformationService;
import com.shenghesun.sic.utils.HttpHeaderUtil;
import com.shenghesun.sic.utils.JsonUtil;
import com.shenghesun.sic.utils.TokenUtil;
import com.shenghesun.sic.wx.service.WxUserInfoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/m/cost_record")
@Slf4j
public class CostRecordController {

	@Autowired
	private CostRecordService costRecordService;
	
	@Autowired
	private IntegralRecordService integralRecordService;
	@Autowired
	private CostRecordSettledService costRecordSettledService;
	@Autowired
	private WxUserInfoService wxUserInfoService;
//	@Autowired
//	private SysUserService sysUserService;
	@Autowired
	private InformationService informationService;
	
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public JSONObject start(HttpServletRequest req, 
			@RequestParam(value = "uuid", required = false) String uuid, 
			@RequestParam(value = "seqNum", required = false) Long seqNum) {
		// 获取用户信息 
		String token = HttpHeaderUtil.getToken((HttpServletRequest) req);
//		String loginAccount = TokenUtil.getLoginUserAccount(token);//登录用户的 用户名
		Long loginUserId = TokenUtil.getLoginUserId(token);//登录用户的 id
		// 判断 当前的参数是否可用；判断本次请求是否有效 
		/*
		 * 	1 用户的信息是否完整
		 *  2 seqNum 是否可用
		 *  3 选中的 uuid 是否满足计费条件
		 *  4 用户当天的积分是否已经到达上线
		 */
	
//		if(wxUserInfoService.registered(wxUserInfoService.findById(loginUserId)) 
//				&& 
		if(this.canUse(loginUserId, seqNum, OperationType.Start) 
				&& costRecordSettledService.canStart(loginUserId, uuid)
				&& integralRecordService.effective(loginUserId, req)) {
			log.info("cost record start");
			// 获取用户之前 计费记录中没有处理的信息，获取该次计时开始的时间点
			Long time = costRecordService.getStartTime(loginUserId);
			
			// 当 seqNum 为 null 时，需要根据历史记录，确定当前的 seqNum 值为多少
			if(seqNum == null || seqNum < 1L) {
				seqNum = this.getDefaultSeqNum(loginUserId);
			}
			// 插入开始计费记录
			costRecordService.save(this.getStartRecord(loginUserId, uuid, seqNum));
			JSONObject json = new JSONObject();
			json.put("time", time);
			json.put("seqNum", seqNum);
			return JsonUtil.getSuccessJSONObject(json);
		}
		return JsonUtil.getFailJSONObject("Invalid req");//返回错误信息之后，前端只要不开始计费操作即可
	}
	private Long getDefaultSeqNum(Long userId) {
		//获取当前用户最大的 序列值
		Long maxSeqNum = costRecordService.getMaxSeqNumByUserId(userId);
		// 在上述值上加 1 ，作为下次输入的值
		return maxSeqNum + 1;
	}
	
	private CostRecord getStartRecord(Long userId, String uuid, Long seqNum) {
		CostRecord cr = this.getDefaultRecord(userId, uuid, seqNum);
		cr.setOperationType(OperationType.Start);//设置类型为 开始
		return cr;
	}
	private CostRecord getDefaultRecord(Long userId, String uuid, Long seqNum) {
		CostRecord cr = new CostRecord();
		cr.setUserId(userId);
		cr.setUser(wxUserInfoService.findById(userId));
		if(StringUtils.isNotBlank(uuid)) {
			Information infor = informationService.findByUuid(uuid);
			cr.setInformationId(infor.getId());
			cr.setInformation(infor);
		}
		cr.setDate(new Date(System.currentTimeMillis()));
//		cr.setOperationType(OperationType.Stop);//设置类型为 结束
		cr.setMatched(false);
		cr.setEffective(true);
		cr.setSettled(false);
		cr.setSeqNum(seqNum);
		return cr;
	}
	//
	//	
	/**
	 * 判断当前的 seqNum 值，是否可用，
	 * 	目前根据 seqNum 值在开始和结束记录中，是否已存在为条件，如果存在则不可用
	 * 	seqNum 在 开始记录请求中，可以为null；但在 结束记录请求中，必须存在
	 * @param seqNum
	 * @return
	 */
	private boolean canUse(Long userId, Long seqNum, OperationType op) {
		if(OperationType.Start.name().equals(op.name())) {
			// 开始记录
			if(seqNum == null || seqNum < 1L) {
				return true;
			}
//		}else if(OperationType.Stop.name().equals(op.name())) {
//			// 结束记录
		}
		List<CostRecord> list = costRecordService.findByUserIdAndSeqNumAndOperationType(userId, seqNum, op);
		return list == null || list.size() < 1;
	}

	@RequestMapping(value = "/stop", method = RequestMethod.GET)
	public JSONObject stop(HttpServletRequest req, 
			@RequestParam(value = "uuid", required = false) String uuid, 
			@RequestParam(value = "seqNum") Long seqNum) {
		// 获取用户信息 
		String token = HttpHeaderUtil.getToken((HttpServletRequest) req);
//		String loginAccount = TokenUtil.getLoginUserAccount(token);//登录用户的 用户名
		Long loginUserId = TokenUtil.getLoginUserId(token);//登录用户的 id
		// 判断 当前的参数是否可用；判断本次请求是否有效
		/*
		 * 	1 seqNum 非空
		 *  2 用户的信息是否完整
		 *  3 seqNum 是否可用
		 *  4 用户当天的积分是否已经到达上线
		 */
		if(seqNum != null 
//				&& wxUserInfoService.registered(wxUserInfoService.findById(loginUserId)) 
				&& this.canUse(loginUserId, seqNum, OperationType.Stop)
				&& integralRecordService.effective(loginUserId, req)) {
			log.info("cost record stop");
			// 插入结束计费记录
			costRecordService.save(this.getEndRecord(loginUserId, uuid, seqNum));
			// 启动一个线程，或者在一个单独的组件中启动一个线程，总之一定需要进行异步操作，不需要等该整合操作完，就可以给页面送出返回值
			// 这个方法不能异步，只能同步，因为涉及到 下一个计时的开始时间
			int amount = this.settledCostRecord(loginUserId, uuid).intValue();
			
			// 获取用户之前 计费记录中没有处理的信息，获取该次计时开始的时间点
			Long time = costRecordService.getStartTime(loginUserId);
			JSONObject json = new JSONObject();
			json.put("time", time);
			json.put("seqNum", seqNum);
			json.put("amount", amount);//添加返回的积分值
			return JsonUtil.getSuccessJSONObject(json);
		}
		return JsonUtil.getFailJSONObject("Invalid req");
	}

	private CostRecord getEndRecord(Long userId, String uuid, Long seqNum) {
		CostRecord cr = this.getDefaultRecord(userId, uuid, seqNum);
		cr.setOperationType(OperationType.Stop);//设置类型为 结束
		return cr;
	}
	
	/**
	 * 启用一个线程完成 处理 计费记录
	 * @param userId
	 */
	private BigDecimal settledCostRecord(Long userId, String uuid) {
//		new Thread() {
//			public void run() {
//				costRecordSettledService.settledCostRecord(userId);
//			};
//		}.start();
		
		return costRecordSettledService.settledCostRecord(userId, uuid);
	}
}
