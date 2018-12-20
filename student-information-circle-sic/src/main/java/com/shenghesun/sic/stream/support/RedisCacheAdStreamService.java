package com.shenghesun.sic.stream.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.utils.RedisUtil;

/**
 * 广告流缓存助手
 * @author kevin
 *
 */
@Service
public class RedisCacheAdStreamService {
	
	@Autowired
	private RedisUtil redisUtil;
	
	// redis 中存储 广告流数据的 key 前缀
	private static final String AD_KEY_PREFIX = "ad_";
	// redis 中存储 广告流数据 key 的 分隔符
	private static final String AD_KEY_SPLIT = "_";
	// 设置 redis 中 信息流数据组的缓存时间，单位 秒,300 天
	private static final Long AD_EXPIRE_TIME = 259_200_00L;
	// 存放 广告流数据的 key 集合的 key
	private static final String AD_ARR_KEY = "ad_arr_key";

	/**
	 * 获取 存储广告流数据的 key
	 * 	ad_SID_UUID
	 * @param sid 策略id 
	 * @param uuid 广告唯一标识
	 * @return
	 */
	public String getKey(Long sid, String uuid) {
		return AD_KEY_PREFIX + sid + AD_KEY_SPLIT + uuid;
	}
	/**
	 * 存储广告数据，同时设置 key 进入 投放序列
	 * @param key
	 * @param value
	 */
	public boolean push(String key , JSONObject value) {
		this.pushKeysArr(key);
		return redisUtil.set(key, value, AD_EXPIRE_TIME);
	}
	
	public JSONObject get(String key) {
		return JSONObject.parseObject(redisUtil.get(key));
	}
	/**
	 * 获取 广告流数据的 key 的集合，用于投放
	 * @return
	 */
	public JSONArray getKeysArr() {
		String arr = redisUtil.get(AD_ARR_KEY);
		return arr == null ? new JSONArray() : JSONArray.parseArray(arr);
	}
	/**
	 * 把 key push 到 广告流数据的 key 集合中
	 * 投放的时候使用
	 * @param key
	 * @return
	 */
	public boolean pushKeysArr(String key) {
		JSONArray arr = this.getKeysArr();
		arr.add(key);
		return redisUtil.set(AD_ARR_KEY, arr, AD_EXPIRE_TIME);
	}

}
