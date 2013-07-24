package com.easou.news.crawl.frontier;

import java.net.URI;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.easou.news.crawl.frontier.WorkQueueFrontier;
import com.easou.news.crawl.model.CrawlUrl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext-frontier-test.xml"})
public class WorkQueueFrontierTest {
	@Autowired
	private WorkQueueFrontier workQueueFrontier;
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void next() {
		try {
			for (int i = 0; i < 100; i++) {
				CrawlUrl uri = workQueueFrontier.next();
				System.out.println(ReflectionToStringBuilder.reflectionToString(uri));
				workQueueFrontier.finished(uri, 30);
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void schedule() {
			CrawlUrl uri = new CrawlUrl();
			uri.setUri(URI.create("http://test/test/"));
			workQueueFrontier.schedule(uri);
	}

}
