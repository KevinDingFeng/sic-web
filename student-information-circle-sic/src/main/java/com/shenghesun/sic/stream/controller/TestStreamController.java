package com.shenghesun.sic.stream.controller;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shenghesun.sic.ad.entity.Ad;
import com.shenghesun.sic.ad.entity.Ad.Subject;
import com.shenghesun.sic.ad.entity.AdStrategy;
import com.shenghesun.sic.ad.entity.AdStrategy.Status;
import com.shenghesun.sic.ad.service.AdService;
import com.shenghesun.sic.ad.service.AdStrategyService;
import com.shenghesun.sic.cost.record.entity.CostRecord;
import com.shenghesun.sic.cost.record.entity.CostRecord.OperationType;
import com.shenghesun.sic.cost.record.service.CostRecordService;
import com.shenghesun.sic.cost.record.support.CostRecordSettledService;
import com.shenghesun.sic.information.entity.Information;
import com.shenghesun.sic.information.entity.InformationType;
import com.shenghesun.sic.information.service.InformationService;
import com.shenghesun.sic.information.service.InformationTypeService;
import com.shenghesun.sic.stream.support.AdStreamAssembleService;
import com.shenghesun.sic.stream.support.FlexibleStreamAssembleService;
import com.shenghesun.sic.stream.support.InformationStreamPoolService;
import com.shenghesun.sic.wx.service.WxUserInfoService;

@RestController
@RequestMapping(value = "/test")
public class TestStreamController {

	@Autowired
	private InformationService informationService;
	@Autowired
	private InformationTypeService informationTypeService;

	// 1 生成 信息数据记录
	@RequestMapping(value = "/ci", method = RequestMethod.GET)
	public String createInformation() {
		InformationType type = new InformationType();
		type.setActive(true);
		type.setRemoved(false);
		type.setName("推荐");
		type.setCode("Recommend");
		type = informationTypeService.save(type);
		Set<InformationType> types = new HashSet<>();
		types.add(type);
		for (int i = 1; i < 11; i++) {
			Information infor = new Information();
			infor.setUuid(UUID.randomUUID().toString().replace("-", ""));
			infor.setTitle("标题" + i);
			infor.setContext("信心内容" + i);
			infor.setImgs("2018/1.jpg,2018/2.jpg,2018/3.jpg");
			infor.setSysUserId(1L);
			infor.setUserName("Kevin");
			infor.setVerified(true);
			infor.setTypes(types);
			informationService.save(infor);
		}

		return "ok";
	}

	@Autowired
	private AdService adService;
	@Autowired
	private AdStrategyService adStrategyService;

	// 2 生成 广告数据记录
	@RequestMapping(value = "/ca", method = RequestMethod.GET)
	public String createAd() {
		Ad ad = new Ad();
		ad.setUuid(UUID.randomUUID().toString().replace("-", ""));
		ad.setTitle("标题1");
		ad.setImgs("2018/1.jpg,2018/2.jpg,2018/3.jpg");
		ad = adService.save(ad);
		AdStrategy as = new AdStrategy();
		as.setLandingPageUrl("http://www.baidu.com");
		as.setStatus(Status.Open);
		Set<Ad> ads = new HashSet<>();
		ads.add(ad);
		as.setAds(ads);
		adStrategyService.save(as);
		return "ok";
	}

	@Autowired
	// private SysUserService sysUserService;
	private WxUserInfoService wxUserInfoService;
	@Autowired
	private CostRecordService costRecordService;

	// 3 生成 计费数据记录
	@RequestMapping(value = "/ccr", method = RequestMethod.GET)
	public String createCostRecord() {
		CostRecord cr = new CostRecord();
		cr.setUserId(1L);
		cr.setUser(wxUserInfoService.findById(1L));
		cr.setDate(new Date(System.currentTimeMillis()));
		cr.setOperationType(OperationType.Start);
		cr.setSeqNum(1L);
		costRecordService.save(cr);
		cr = new CostRecord();
		cr.setUserId(1L);
		cr.setUser(wxUserInfoService.findById(1L));
		cr.setDate(new Date(System.currentTimeMillis()));
		cr.setOperationType(OperationType.Stop);
		cr.setSeqNum(1L);
		costRecordService.save(cr);
		return "ok";
	}

	@Autowired
	// private InformationStreamAssembleService informationStreamAssembleService;
	private InformationStreamPoolService informationStreamPoolService;

	// 4 整理 信息数据到 redis
	@RequestMapping(value = "/si", method = RequestMethod.GET)
	public String settledInformation(@RequestParam(value = "id") Long id) {
		// informationStreamAssembleService.tryAssemble();
		informationStreamPoolService.newInformationNotify(id);
		return "ok";
	}

	@Autowired
	private AdStreamAssembleService adStreamAssembleService;

	// 5 整理 广告数据到 redis
	@RequestMapping(value = "/sa", method = RequestMethod.GET)
	public String settledAd() {
		adStreamAssembleService.assemble();
		return "ok";
	}

	@Autowired
	private CostRecordSettledService costRecordSettledService;

	// 6 整合 计费集合为积分记录
	@RequestMapping(value = "/scr", method = RequestMethod.GET)
	public String settledCostRecord() {
		costRecordSettledService.settledCostRecord(1L, "");
		return "ok";
	}

	// 7 生成 软文数据记录
	@RequestMapping(value = "/cf", method = RequestMethod.GET)
	public String createFlexible() {
		Ad ad = new Ad();
		ad.setSubject(Subject.Flexible);
		ad.setUuid(UUID.randomUUID().toString().replace("-", ""));
		ad.setTitle("软文1");
		ad.setImgs("2018/1.jpg,2018/2.jpg,2018/3.jpg");
		ad = adService.save(ad);
		AdStrategy as = new AdStrategy();
		as.setLandingPageUrl("http://www.baidu.com");
		as.setStatus(Status.Open);
		Set<Ad> ads = new HashSet<>();
		ads.add(ad);
		as.setAds(ads);
		adStrategyService.save(as);
		return "ok";
	}

	@Autowired
	private FlexibleStreamAssembleService flexibleStreamAssembleService;
	// 8 整理 广告数据到 redis
	@RequestMapping(value = "/ss", method = RequestMethod.GET)
	public String settledFlexible(@RequestParam(value = "id") Long id) {
		flexibleStreamAssembleService.assemble(id);
		return "ok";
	}

}
