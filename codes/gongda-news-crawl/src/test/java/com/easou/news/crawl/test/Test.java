package com.easou.news.crawl.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class Test {
	public static void main(String[] args) throws IOException {
//		String str = FileUtils.readFileToString(new File("f:\\1.html"));
////		String str = "dskajdks endText moxiaoming endText hello div";
////		System.out.println(str);
//		String regex = "<div id=\"endText\">((?![\\s\\S]*?<div id=\"endText\">)[\\s\\S]*?)<!-- 分页 -->";
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(str);
////		System.out.println(matcher.find());
//		if(matcher.find()){
//			System.out.println(matcher.group(1));
//		}
		
	    HttpClient httpClient = new DefaultHttpClient();  
	    try {  
	          
	        //创建HttpGet  
	        HttpGet httpGet = new HttpGet("http://news.yesky.com/46/34452046.shtml");  
	        System.err.println("executing request " + httpGet.getURI());  
	        HttpResponse response = httpClient.execute(httpGet);
	        HttpEntity entity = response.getEntity();
	        byte[] bytes = EntityUtils.toByteArray(entity);
	        String str = new String (bytes, "gbk");
//	        StringBuffer sb = new StringBuffer();
//	        if (entity != null) {
//	        	InputStream is = entity.getContent();
//	        	
//	        	
//	        	byte[] bytes = new byte[4096];
//	        	int size = 0;
//	        	while ((size = is.read(bytes)) > 0) {
//	        		String str = new String(bytes, 0, size, "gbk");
//	        		sb.append(str);
//	        		}
//	        	is.close();
//	        }
	        System.out.println(str);
	        System.err  
	                .println("==========================================================");  
	    } catch (Exception e) {  
	        // TODO: handle exception  
	    }finally{  
	        //关闭连接，释放资源  
	        httpClient.getConnectionManager().shutdown();  
	    }  		
	}
}
