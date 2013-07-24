// Copyright(c) 2011 easou.com
package com.easou.news.crawl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yunchat
 *
 */
public class ContentFilterUtil {

	private static Map<String, String> contentFilterMap = new HashMap<String, String>();
	
	private static Set<String> sourceKeys = new HashMap<String, String>().keySet();
	
	static {
		contentFilterMap.put("新华", "<span id=\"content\" class=\"bai13\">|<span class=\"bai13\" id=\"content\">");
		contentFilterMap.put("光明", "<div id=\"ArticleContent\">|<div id=\"flv_play\">");
		contentFilterMap.put("新浪", "<div class=\"eControl\">|<div id=\"dataSource_1\" thumb=1>");
		contentFilterMap.put("国际在线", "点击图片进入下一页");
		contentFilterMap.put("网易", "<div class=\"nph_photo_view\">");

		sourceKeys = contentFilterMap.keySet();
		
	}
	
	public static boolean filterContent(String source, String content) {
		
		for (String s : sourceKeys) {
			if (source.indexOf(s) > -1) {
				String[] values = contentFilterMap.get(s).split("\\|");
				for (String val : values) {
					if (content.indexOf(val) > -1) {
						return true;
					}					
				}	
			}			
		}
		return false;
	}

	
	public static void main(String[] args) {
//		System.out.println(ContentFilterUtil.filterContent(source, content));
	}
} 
