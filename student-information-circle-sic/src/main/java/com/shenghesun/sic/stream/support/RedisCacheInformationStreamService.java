package com.shenghesun.sic.stream.support;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenghesun.sic.utils.RedisUtil;

/**
 * redis 缓存助手
 * 	管理 set 、get redis 
 * 	管理 key 的规范
 * 
 * 所有 push 数据 或 插入数据 相关的操作，都是和 assemble 信息流备用有关，和 读取无关
 * 读取 信息流数据，默认位置指向当前天或前一天的起始坐标
 * @author kevin
 *
 */
@Service
public class RedisCacheInformationStreamService {

	@Autowired
	private RedisUtil redisUtil;

	// redis 中存储 当前 信息流数据组 的 push 位置坐标 的 key 的前缀，之后紧跟类型
	private static final String CURRENT_PUSH_INDEX_KEY_PREFIX = "infor_current_push_index_";
	// redis 中存储 信息流数据组 的坐标 前缀
	private static final String INFOR_ARR_INDEX_PREFIX = "ii_";
	// redis 中存储 信息流数据组 坐标的起始值
	private static final int DEFAULT_INDEX_BEGIN = 1;
	// 设置 redis 中 信息流数据组的缓存时间，单位 秒,300 天
	private static final Long INFOR_EXPIRE_TIME = 259_200_00L;
	// redis 中存储 当前天 信息流数据组 的起始 push 位置坐标 key 的前缀
	private static final String CURRENT_DAY_PUSH_INDEX_KEY_PREFIX = "infor_current_day_push_index_";
	// redis 中存储 信息流 uuid 对应的 current push index 的 key 的前缀
	private static final String UUID_PUSH_INDEX_KEY = "ui_";
	// redis 中存储 信息流数据 key 的 分隔符
	private static final String INFOR_KEY_SPLIT = "_";
	
	public String getInforArrIndexPrefix() {
		return INFOR_ARR_INDEX_PREFIX;
	}
	/*
	 * ui_UUID 坐标
	 * infor_current_push_index_TYPE type对应的当前坐标
	 * infor_current_day_push_index_TYPE type对应的当天起始坐标
	 * ii_TYPE_INDEX 信息流
	 * 
	 */
	// {日期字符串 ：当前天信息流起始位置}
	// {流位置 ： 信息流数据组（只包括信息流数据的 JSONArray）}
	// {信息uuid ： 流位置}
	/**
	 * 获取当前的 push 坐标 确定当前的 坐标（如果当前天不存在，则获取当前坐标作为当前天的起始坐标存入） 当前的坐标，获取到的就是 push
	 * 的位置，每次更新该值(更新方法不在本方法内执行，需要执行完 push 操作后，调用 update 方法)时，已经在原基础上做了递增
	 * 
	 * @return
	 */
	public String getCurrentIndex(String type) {
		String currentIndex = null;
		// 通过一个 current key 获取数据；存在返回 true
		if (redisUtil.exists(CURRENT_PUSH_INDEX_KEY_PREFIX + type)) {
			currentIndex = redisUtil.get(CURRENT_PUSH_INDEX_KEY_PREFIX + type);
		} else {
			// 不存在当前值时，设置默认值
//			currentIndex = INFOR_ARR_INDEX_PREFIX + DEFAULT_INDEX_BEGIN;
			//由 默认是坐标值 改为纯数字值，便于后续添加类型
			currentIndex = DEFAULT_INDEX_BEGIN + "";
			redisUtil.set(CURRENT_PUSH_INDEX_KEY_PREFIX + type, currentIndex);
		}
		return currentIndex;
	}

	/**
	 * 更新当前的 push 坐标
	 * 
	 * @param nextIndex
	 */
	public boolean updateCurrentIndex(String type, String nextIndex) {
		return StringUtils.isEmpty(nextIndex) ? false : redisUtil.set(CURRENT_PUSH_INDEX_KEY_PREFIX + type, nextIndex);
	}

//	/**
//	 * 获取下一个 push 坐标
//	 * 
//	 * @param index
//	 * @return
//	 */
//	public String getNextIndex(String type, String index) {
//		if (index.startsWith(INFOR_ARR_INDEX_PREFIX)) {
//			Long value = Long.parseLong(
//					index.substring(index.indexOf(INFOR_ARR_INDEX_PREFIX) + INFOR_ARR_INDEX_PREFIX.length()));
//			
//			return INFOR_ARR_INDEX_PREFIX + (++ value);
//		}
//		return null;
//	}

	/**
	 * 获取当前天的日期字符串
	 * @return
	 */
	private String getCurrentDayString() {
		return this.getFormatDate(this.getDate(0));
	}
	/**
	 * 格式化日期
	 * @param n
	 * @return
	 */
	private String getFormatDate(Date date) {
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}
	/**
	 * 获取 距 当前天 n 天的时间
	 * 	传入的 n 为 Null 或 0 表示为当天时间
	 * @param n 天
	 * @return
	 */
	private Date getDate(Integer n) {
		if(n == null || n == 0) {
			return new Date(System.currentTimeMillis());
		}
		return new Date(System.currentTimeMillis() - n * (86_400_000L));
	}
	/**
	 * 判断是否存在当前天的起始插入位置
	 * 
	 * @return
	 */
	public boolean existsCurrentDayStartPushIndex(String type) {
		//获取当前天的日期字符串
		String currentDateStr = this.getCurrentDayString();
		return redisUtil.exists(CURRENT_DAY_PUSH_INDEX_KEY_PREFIX + type + INFOR_KEY_SPLIT + currentDateStr);
	}

	/**
	 * 插入当前天的 起始插入位置
	 * 
	 * @param index
	 */
	public boolean insertCurrentDayStartPushIndex(String type, String index) {
		//获取当前天的日期字符串
		String currentDateStr = this.getCurrentDayString();
		return redisUtil.set(CURRENT_DAY_PUSH_INDEX_KEY_PREFIX + type + INFOR_KEY_SPLIT + currentDateStr, index);
	}

	/**
	 * 获取当前天的 起始插入位置
	 * 
	 * @return
	 */
	public String getCurrentDayStartPushIndex(String type) {
		//获取当前天的日期字符串
		String currentDateStr = this.getCurrentDayString();
		return redisUtil.get(CURRENT_DAY_PUSH_INDEX_KEY_PREFIX + type + INFOR_KEY_SPLIT + currentDateStr);
	}
	/**
	 * 获取 昨天的起始 push 位置
	 * @return
	 */
	private String getYesterdayStartPushIndex(String type) {
		//获取昨天的日期字符串
		String yesterdayDateStr = this.getFormatDate(this.getDate(1));
		return redisUtil.get(CURRENT_DAY_PUSH_INDEX_KEY_PREFIX + type + INFOR_KEY_SPLIT + yesterdayDateStr);
	}

	/**
	 * 判断 昨天的起始 push 位置是否存在
	 * @return
	 */
	private boolean existsYesterdayStartPushIndex(String type) {
		//获取昨天的日期字符串
		String yesterdayDateStr = this.getFormatDate(this.getDate(1));
		return redisUtil.exists(CURRENT_DAY_PUSH_INDEX_KEY_PREFIX + type + INFOR_KEY_SPLIT + yesterdayDateStr);
	}

	/**
	 * 把 json arr 格式的数据缓存到指定的位置， 并返回 push 结果
	 * 	设置缓存时间为 3 天
	 * @param index
	 * @param inforArr
	 * @return
	 */
	public boolean push(String type, String index, JSONArray obj) {
		index = INFOR_ARR_INDEX_PREFIX + type + INFOR_KEY_SPLIT + index;//ii_news_1
		return redisUtil.set(index, obj, INFOR_EXPIRE_TIME);
	}
	/**
	 * 
	 * @param index
	 * @return
	 */
	public JSONArray get(String type, String index) {
		index = INFOR_ARR_INDEX_PREFIX + type + INFOR_KEY_SPLIT + index;//ii_news_1
		return JSONObject.parseArray(redisUtil.get(index));
	}

	/**
	 * 记录每个信息 对应的存储坐标
	 * 	设置缓存时间为 3 天
	 * @param list
	 * @param index
	 */
	public boolean saveUuidAndIndexMapping(String[] uuids, String type, String index) {
		if(uuids != null && uuids.length > 0) {
			for(String uuid : uuids) {
				String value = INFOR_ARR_INDEX_PREFIX + type + INFOR_KEY_SPLIT + index;//ii_news_1 
				redisUtil.set(UUID_PUSH_INDEX_KEY + uuid, value, INFOR_EXPIRE_TIME);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 获取信息流数据组默认的读取位置
	 * 	读取 信息流数据，默认位置指向当前天或前一天的起始坐标
	 * 	当前天的起始坐标不存在，获取前一天起始的坐标
	 * 	如果前一天的起始坐标也不存在，则获取当前 push 位置的坐标
	 * @return
	 */
	public String getDefaultIndex(String type) {
		if(this.existsCurrentDayStartPushIndex(type)) {
			return this.getCurrentDayStartPushIndex(type);
		}
		if(this.existsYesterdayStartPushIndex(type)) {
			return this.getYesterdayStartPushIndex(type);
		}
		return this.getCurrentIndex(type);
	}

}
