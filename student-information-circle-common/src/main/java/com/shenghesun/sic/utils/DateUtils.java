package com.shenghesun.sic.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	/**
	 * @Title: getFrontDay 
	 * @Description: 获取前一天 
	 * @param date
	 * @return  Date 
	 * @author yangzp
	 * @date 2018年11月20日上午10:24:36
	 **/ 
	public static Date getFrontDay(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, -1);  
        date = calendar.getTime();  
        return date;  
    }
}
