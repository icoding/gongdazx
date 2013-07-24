package com.easou.news.crawl.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtil {
	
	/**
	 * 判断是否匹配
	 * @param str
	 * @param regex
	 * @return
	 */
	public static boolean isMatch(String str, List<String> regexs){
		if(regexs==null || regexs.isEmpty())
			return false;
		
		for(String regex : regexs){
			if("".equals(regex.trim()))
				continue;
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			if(matcher.find())
				return true;
		}
		
		return false;
	}
}
