package com.easou.news.crawl.service;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
public class HttpClientServiceTest {
	@Autowired
	private IHttpClientService httpClientService;
	
	@Test
	public void getResponseBodyByGetTest(){
		String url = "http://food.39.net/";
		System.out.println(httpClientService.getResponseBodyByGet(url, null));
	}
	
//	@Test
	public void getImageByGet() throws Exception {
		String url = "http://image.81un.net/thdb/2013-01-25/5fc090e4e5b701bb551bf7bd99b01967.jpeg";
		byte[] image = httpClientService.getImageByGet(url, "http://www.81un.net/");
		System.out.println("len:" + image.length);
		FileUtils.writeByteArrayToFile(new File(
				"/Users/yunchat/Documents/work/test/222.jpg"), image);

		
	}
}
