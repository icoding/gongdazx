package com.easou.news.crawl.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	/**
	 * 获得当前时间毫秒数
	 * @return
	 */
	public static long currentDateMilliseconds(){
		return new Date().getTime();
	}
	
	/**
	 * 获得指定的时间毫秒数
	 * @param dateStr
	 * @param datePattern
	 * @return
	 */
	public static long fixedDateMilliseconds(String dateStr, String datePattern){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			return sdf.parse(dateStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
}
