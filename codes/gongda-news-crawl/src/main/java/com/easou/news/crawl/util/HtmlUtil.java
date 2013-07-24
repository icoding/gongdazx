package com.easou.news.crawl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * html的工具类
 * @author moxm
 *
 */
public class HtmlUtil {
	private static final String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }   
	private static final String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }   
	private static final String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式   
	private static final String regEx_html1 = "<[^>]+"; 
	
	private static final Pattern FILTER_TAG_ATTR_P = Pattern.compile("(<p [^/\t\n\r\f]*?>)", Pattern.CASE_INSENSITIVE);
	private static final Pattern FILTER_TAG_ATTR_BR = Pattern.compile("(<br [^/\t\n\r\f]*?>)", Pattern.CASE_INSENSITIVE);
	private static final Pattern FILTER_TAG_ATTR_STRONG = Pattern.compile("(<strong [^/\t\n\r\f]*?>)", Pattern.CASE_INSENSITIVE);

	
	
	
	private static Map<String, String> map = new HashMap<String, String>();
	
	static {
		map.put("&nbsp;", " ");
		map.put("&gt;", ">");
		map.put("&lt;", "<");
		map.put("&quot;", "\"");
	}
	
	public static String removeScriptTags(String htmlStr){
		Pattern pattern = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);   
        Matcher matcher = pattern.matcher(htmlStr);   
        htmlStr = matcher.replaceAll(""); //过滤script标签   

        pattern = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);   
        matcher = pattern.matcher(htmlStr);   
        htmlStr = matcher.replaceAll(""); //过滤style标签   
        return htmlStr;
	}
	
	/**
	 * 替换指定标记
	 * @param str
	 * @return
	 */
	public static String replaceSign(String str){
		Set<Entry<String, String>> set = map.entrySet();
		for(Entry<String, String> entry : set){
			str = str.replaceAll(entry.getKey(), entry.getValue());
		}
		
		return str;
	}
	
	/**
	 * 去掉文本中空行
	 * @return
	 */
	public static String removeSpaceLine(String str){
		try {
			StringBuffer sb = new StringBuffer();
			Reader reader = new StringReader(str);
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			while((line = br.readLine()) != null){
				if(!"".equals(line.trim())){
					sb.append(line).append("\n");
				}
			}
			br.close();
			reader.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 去掉所有的html标签
	 * @param htmlStr
	 * @return
	 */
	public static String removeAllHtml(String htmlStr){
		if(htmlStr == null || "".equals(htmlStr.trim()))
			return null;
		
        Pattern pattern = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);   
        Matcher matcher = pattern.matcher(htmlStr);   
        htmlStr = matcher.replaceAll(""); //过滤html标签   
         
        pattern = Pattern.compile(regEx_html1,Pattern.CASE_INSENSITIVE);   
        matcher = pattern.matcher(htmlStr);   
        htmlStr = matcher.replaceAll(""); //过滤html标签   
        
        return htmlStr.trim();
	}
	
	public static String removeTagAttr(String htmlStr) {
		Matcher matcher = FILTER_TAG_ATTR_P.matcher(htmlStr);
		htmlStr = matcher.replaceAll("<p>");
		matcher = FILTER_TAG_ATTR_BR.matcher(htmlStr);
		htmlStr = matcher.replaceAll("<br>");
		matcher = FILTER_TAG_ATTR_STRONG.matcher(htmlStr);
		htmlStr = matcher.replaceAll("<strong>");
		return htmlStr;
	}

	public static String getAssign(String regex, String str){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		if(matcher.find())
			return matcher.group(1);
		
		return null;
	}
	
	/**
	 * 保留指定的标签
	 * @param str
	 * @param holdTags
	 * @return
	 */
	public static String keepFixedHtml(String str, String holdTags){
		if(holdTags == null || "".equals(holdTags.trim())) {
			return removeAllHtml(str);
		} else {
			String regex = createRegex(holdTags);
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(str);
			String keepHtml =  matcher.replaceAll("").trim();
			return removeTagAttr(keepHtml);
		}
	}
	
	private static String createRegex(String holdTags) {
		StringBuffer sb = new StringBuffer("(\\<!--(.+)--\\>)|</?(?!");
		String[] tagArray = holdTags.split("\\|");
		for(int i=0;i<tagArray.length;i++){
			if(i != 0)
				sb.append("|");
			sb.append("([/]?"+tagArray[i]+")");
		}

		sb.append(")[^><]*>");
		System.out.println("hold:" + sb.toString());
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
//		String content = FileUtils.readFileToString(new File("f:\\1.txt"));
		String content = " ddd<!--nihao-->ddd  <!--<![endif]-->ddaf<p ddd>nihao</p>";
		
		System.out.println(content);
		String holdTags = "p|strong|br";
//		String regex = "</?[^/?p|strong|br][^><]*>";
//		System.out.println(dupFixedHtml(content, regex));
		System.out.println(keepFixedHtml(content, holdTags));
		
		String removeAttr = "dddd<p style=\"text-align: center;\"> ssss</p>ddd<p >d<p />d";
		System.out.println(removeTagAttr(removeAttr));
	
	}
}