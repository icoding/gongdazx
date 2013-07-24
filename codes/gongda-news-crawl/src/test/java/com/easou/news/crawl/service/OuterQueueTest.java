// Copyright(c) 2011 easou.com
package com.easou.news.crawl.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.easou.news.crawl.frontier.OuterQueue;
import com.easou.news.crawl.model.CrawlOuterUrl;

/**
 * @author yunchat
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
public class OuterQueueTest {
	
	@Autowired
	private OuterQueue outerQueue;

	@Test
	public void testPush() {
		CrawlOuterUrl url = new CrawlOuterUrl();
		url.setUrl("http://finance.sina.com.cn/leadership/mroll/20130316/150014855009.shtml");
		url.setTemplateId(42);
		url.setUsedBy("common");
		url.setContentFrom(1);
		url.setType(1);
		boolean res = outerQueue.pushOuterCrawl(url);
		System.out.println(res);
	}
	
	@Test
	public void testPush1() {
		CrawlOuterUrl url = new CrawlOuterUrl();
		url.setUrl("http://news.sina.com.cn/c/2013-03-18/140826565656.shtml");
		url.setTemplateId(42);
		url.setUsedBy("common");
		url.setContentFrom(0);
		url.setType(1);
		boolean res = outerQueue.pushOuterCrawl(url);
		System.out.println(res);
	}
	
	
	
//	@Test
	public void testPop() {
		CrawlOuterUrl url = outerQueue.popOuterCrawl();
		System.out.println(url);
	}

}
