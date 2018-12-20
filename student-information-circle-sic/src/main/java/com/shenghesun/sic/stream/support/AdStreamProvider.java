package com.shenghesun.sic.stream.support;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * 广告流提供者
 * @author kevin
 *
 */
@Service
public class AdStreamProvider {
	
	@Autowired
	private RedisCacheAdStreamService redisCacheAdStreamService;

	/**
	 * 根据 req 获取投放需要的广告流数据
	 * 	目前使用随机的方式
	 * @param req
	 * @return
	 */
	public JSONObject getByRequest(HttpServletRequest req) {
		JSONObject json = this.getByRandom(req);
		return json;
	}
	/**
	 * 实现简易的随机方式 返回 广告流数据
	 * @param req
	 * @return
	 */
	private JSONObject getByRandom(HttpServletRequest req) {
		JSONArray arr = redisCacheAdStreamService.getKeysArr();
		int size = arr.size();
		if(size < 1) {
			return null;
		}
		Random r = new Random();
		int i = r.nextInt(size);
		return redisCacheAdStreamService.get(arr.getString(i));
	}

	
}
