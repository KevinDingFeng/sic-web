package com.shenghesun.sic.utils;

import com.alibaba.fastjson.JSONObject;

public class JsonUtil {
	// 用来定义 code、message、data 等字面量，使用 static final 关键字修饰
	/**
	 * 对应返回的状态值， 200 表示操作成功，其他值表示操作失败， DATA_KEY 对应失败原因
	 */
	public static final String CODE_KEY = "code";
	/**
	 * 对应返回的状态说明，普通情况 success 表示成功， fail 表示失败
	 */
	public static final String MESSAGE_KEY = "message";
	/**
	 * 对应操作的返回值，返回的数据类型根据情况而定，普通情况为 String 或 JSONObject
	 */
	public static final String DATA_KEY = "data";
	
	public static final String CODE_SUCCESS_VALUE = "200";
	public static final String MESSAGE_SUCCESS_VALUE = "success";
	public static final String CODE_TOKEN_CHECK_FAIL_VALUE = "201";
	public static final String MESSAGE_TOKEN_CHECK_FAIL_VALUE = "invalid token";
	public static final String CODE_FAIL_VALUE = "400";
	public static final String MESSAGE_FAIL_VALUE = "fail";
	
	/**
	 * 获取一个成功的json对象
	 * @return
	 */
	public static JSONObject getSuccessJSONObject() {
		JSONObject json = new JSONObject();
		json.put(CODE_KEY, CODE_SUCCESS_VALUE);
		json.put(MESSAGE_KEY, MESSAGE_SUCCESS_VALUE);
		return json;
	}
	/**
	 * 获取一个成功的包括数据data的json对象
	 * @param data
	 * @return
	 */
	public static JSONObject getSuccessJSONObject(Object data) {
		JSONObject json = getSuccessJSONObject();
		json.put(DATA_KEY, data);
		return json;
	}
	
	/**
	 * 获取一个失败的json对象
	 * @return
	 */
	public static JSONObject getFailJSONObject() {
		JSONObject json = new JSONObject();
		json.put(CODE_KEY, CODE_FAIL_VALUE);
		json.put(MESSAGE_KEY, MESSAGE_FAIL_VALUE);
		return json;
	}
	
	/**
	 * 获取一个失败的json对象
	 * @return
	 */
	public static JSONObject getFailJSONObject(Object data) {
		JSONObject json = getFailJSONObject();
		json.put(DATA_KEY, data);
		return json;
	}
	/**
	 * 获取一个 token 校验失败的json对象
	 * @return
	 */
	public static JSONObject getTokenCheckFailJSONObject(Object data) {
		JSONObject json = new JSONObject();
		json.put(CODE_KEY, CODE_TOKEN_CHECK_FAIL_VALUE);
		json.put(MESSAGE_KEY, MESSAGE_TOKEN_CHECK_FAIL_VALUE);
		json.put(DATA_KEY, data);
		return json;
	}
	
}
