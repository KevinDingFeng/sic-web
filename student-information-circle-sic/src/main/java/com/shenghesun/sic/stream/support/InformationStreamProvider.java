package com.shenghesun.sic.stream.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;

/**
 * 信息流数据提供者
 * 	支持 StreamController 接口，读取缓存等信息
 * @author kevin
 *
 */
@Service
public class InformationStreamProvider {

	@Autowired
	private RedisCacheInformationStreamService redisCacheService;
	
	/**
	 * 获取信息流数据组默认的读取位置
	 * 	返回的是数值，不带有 前缀和类型
	 * @return
	 */
	public String getDefaultIndex(String type) {
		return redisCacheService.getDefaultIndex(type);
	}

	/**
	 * 根据 index 获取 信息流集合
	 * @param index
	 * @return
	 */
	public JSONArray getByIndexAndType(String type, String index) {
		return redisCacheService.get(type, index);
	}
	
//	public String getIndex(int indexValue) {
//		return redisCacheService.getInforArrIndexPrefix() + indexValue;
//	}

//	public int getIndexVal(String index) {
//		String pre = redisCacheService.getInforArrIndexPrefix();
//		return Integer.parseInt(index.substring(index.indexOf(pre) + pre.length()));
//	}

}
