package com.shenghesun.sic.stream.support;

import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.ad.entity.Ad;
import com.shenghesun.sic.ad.entity.AdStrategy;
import com.shenghesun.sic.ad.model.AdModel;
import com.shenghesun.sic.utils.JsonUtil;

/**
 * 软文流数据组装模块 通过软文池获取需要组装的数据
 * 
 * @author kevin
 *
 */
@Service
public class FlexibleStreamAssembleService {

	@Autowired
	private FlexibleStreamPoolService flexibleStreamPoolService;
	@Autowired
	private RedisCacheFlexibleStreamService redisCacheFlexibleStreamService;

	public JSONObject assemble(long id) {
		Ad ad = flexibleStreamPoolService.getFlexibleById(id);
		if (ad != null && ad.getAdStrategies() != null) {
			// 组装
			return this.assembleFlexible(ad);
		}
		return JsonUtil.getFailJSONObject();
	}

	/**
	 * 以软文未主体驱动
	 * @param ad
	 * @return
	 */
	private JSONObject assembleFlexible(Ad ad) {
		Set<AdStrategy> set = ad.getAdStrategies();
		Iterator<AdStrategy> it = set.iterator();
		while (it.hasNext()) {
			AdStrategy ads = it.next();
			this.assembleAdStrategy(ads, ad);
		}
		return null;
	}

	// 处理单个策略，根据 策略为批次，存入 redis ,返回 策略和广告的标识
	private JSONObject assembleAdStrategy(AdStrategy ads, Ad ad) {
		JSONObject json = new JSONObject();
		Set<Ad> adSet = ads.getAds();
		if (adSet != null && adSet.size() > 0) {
			AdModel adm = this.assembleAd(ad);
			json.put("adSId", ads.getId());
			
			adm.setSid(ads.getId());
			adm.setLandingPage(ads.getLandingPageUrl());
			// 存入 redis
			String key = redisCacheFlexibleStreamService.getKey(adm.getSid(), adm.getUuid());
			redisCacheFlexibleStreamService.push(key, (JSONObject) JSONObject.toJSON(adm));
			
			json.put("adUUid", adm.getUuid());
		}
		return json;
	}

	// 处理单个广告
	private AdModel assembleAd(Ad ad) {
		AdModel adModel = new AdModel();
		adModel.setUuid(ad.getUuid());
		adModel.setTitle(ad.getTitle());
		adModel.setImgs(ad.getImgs());
		return adModel;
	}

}
