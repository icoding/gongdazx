package com.easou.news.crawl.service;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.model.CrawlBasicInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
public class RedisServiceTest {
	@Autowired
	private IRedisService redisService;
	
	@Test
	public void popNewsInfoTest(){
		CrawlBasicInfo basicInfo = redisService.popNewsInfo(CommandContant.NEW_BASICINFO_LIST);
		System.out.println(basicInfo);
	}
	
	@Test
	public void setImageInfo(){
		redisService.setImageInfos("test", "testdata".getBytes(), 1000, TimeUnit.SECONDS);
		System.out.println("-----------");
		
		byte[] val = redisService.getImageInfos("test");
		System.out.println("-----------" + new String(val));
	}
	
	@Test
	public void getImageInfo(){

		byte[] val = redisService.getImageInfos("test");
		System.out.println("-----------" + new String(val));
	}

}
