package com.easou.news.crawl.util;

import java.io.IOException;

import com.easou.news.crawl.model.CrawlBasicInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * json字符串工具
 * @author Administrator
 *
 */
public class JsonUtil {
	private static Gson gson = new Gson();
	
	public static String json(Object obj){
		return gson.toJson(obj);
	} 
	
	public static Object toObject(String str){
		return gson.fromJson(str, CrawlBasicInfo.class);
	}
	
	public static Object toObject(String str, Class classes){
		return gson.fromJson(str, classes);
	}
	
	public static void main(String[] args) throws IOException {
//		String format = FileUtils.readFileToString(new File("f:\\1.txt"));
		String json = "[{\"action\":\"attr\",\"key\":\"id\",\"value\":\"channel-nav\"},{\"action\":\"attr\",\"key\":\"class\",value:\"navigation\"}]";
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(json);
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		for(int i=0;i<jsonArray.size();i++){
			JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
			System.out.println(jsonObject.get("value"));
		}
	}
}
