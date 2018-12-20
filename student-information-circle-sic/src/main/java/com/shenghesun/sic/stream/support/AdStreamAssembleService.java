package com.shenghesun.sic.stream.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.ad.entity.Ad;
import com.shenghesun.sic.ad.entity.AdStrategy;
import com.shenghesun.sic.ad.model.AdModel;

/**
 * 广告流组装模块
 * 	负责从 pool 中获取可用的 广告策略和广告信息，组装放置到 redis 中
 * 
 * 	通过 广告投放策略，获取正在投放中的策略和广告信息，组装成 广告信息流数据，放到 redis 中
 * 	记录 redis 中的总条数
 * @author kevin
 *
 */
@Service
public class AdStreamAssembleService {

	@Autowired
	private AdStreamPoolService adStreamPoolService;
	
	@Autowired
	private RedisCacheAdStreamService redisCacheAdStreamService;
	
	public JSONObject assemble() {
		JSONObject json = new JSONObject();
		// 通过 pool 组件获取广告策略信息，投放条件是否允许，交给 pool 判断，这里只负责获取
		List<AdStrategy> strategyList = adStreamPoolService.getAdStrategies();
		if(strategyList != null) {
			// 组装广告流数据
			json = this.assembleAdStrategyList(strategyList);
		}
		return json;
	}

	//处理整个 策略集合
	private JSONObject assembleAdStrategyList(List<AdStrategy> list) {
		JSONArray adSArr = new JSONArray();
		for(AdStrategy ads : list) {
			adSArr.add(this.assembleAdStrategy(ads));
		}
		JSONObject json = new JSONObject();
		json.put("adSs", adSArr);
		return json;
	}
	//处理单个策略，根据 策略为批次，存入 redis ,返回 策略和广告的标识
	private JSONObject assembleAdStrategy(AdStrategy ads) {
		JSONObject json = new JSONObject();
		Set<Ad> adSet = ads.getAds();
		if(adSet != null && adSet.size() > 0) {
			List<AdModel> adms = this.assembleAdSet(adSet);
			if(adms != null) {
				json.put("adSId", ads.getId());
				JSONArray adUUidArr = new JSONArray();
				for(AdModel adm : adms) {
					adm.setSid(ads.getId());
					adm.setLandingPage(ads.getLandingPageUrl());
					//存入 redis 
					String key = redisCacheAdStreamService.getKey(adm.getSid(), adm.getUuid());
					redisCacheAdStreamService.push(key, (JSONObject) JSONObject.toJSON(adm));
					adUUidArr.add(adm.getUuid());
				}
				json.put("adUUids", adUUidArr);
			}
		}
		return json;
	}
	//处理整个广告集合
	private List<AdModel> assembleAdSet(Set<Ad> set) {
		List<AdModel> list = null;
		Iterator<Ad> its = set.iterator();
		while(its.hasNext()) {
			if(list == null) {
				list = new ArrayList<>();
			}
			Ad ad = its.next();
			AdModel adModel = this.assembleAd(ad);
			list.add(adModel);
		}
		return list;
	}
	//处理单个广告
	private AdModel assembleAd(Ad ad) {
		AdModel adModel = new AdModel();
		adModel.setUuid(ad.getUuid());
		adModel.setTitle(ad.getTitle());
		adModel.setImgs(ad.getImgs());
		return adModel;
	}
	
}
