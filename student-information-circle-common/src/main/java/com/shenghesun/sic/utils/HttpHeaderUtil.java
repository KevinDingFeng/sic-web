package com.shenghesun.sic.utils;

import javax.servlet.http.HttpServletRequest;

public class HttpHeaderUtil {

	public static final String CUSTOM_TOKEN_NAME = "treasure_token";

	public static String getToken(HttpServletRequest req) {
		
//		print(req);
		
		return req.getHeader(CUSTOM_TOKEN_NAME);
//		return req.getParameter(CUSTOM_TOKEN_NAME);
	}
	/*private static void print(HttpServletRequest req) {
		Enumeration<String> es = req.getHeaderNames();
		while(es.hasMoreElements()) {
			String name = es.nextElement();
			System.out.println("name = " + name + " ; value = " + req.getHeader(name));
		}
//		es = req.getParameterNames();
//		System.out.println("-------------");
//		while(es.hasMoreElements()) {
//			String name = es.nextElement();
//			System.out.println("name = " + name + " ; value = " + req.getParameter(name));
//		}
		
		
	}*/
}
