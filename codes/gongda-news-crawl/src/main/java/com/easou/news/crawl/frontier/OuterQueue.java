// Copyright(c) 2011 easou.com
package com.easou.news.crawl.frontier;

import java.util.List;

import javax.annotation.Resource;

import org.jsoup.helper.StringUtil;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.model.CrawlOuterUrl;
import com.easou.news.crawl.util.JsonUtil;

/**
 * @author yunchat
 *
 */
@Service
public class OuterQueue {

	
	@Resource(name="frontierRedisTemplate")
	private StringRedisTemplate queueRedisTemplate;
	
	
	public CrawlOuterUrl popOuterCrawl() {
		BoundListOperations<String, String> operations =  queueRedisTemplate.boundListOps(CommandContant.CRAWL_OUTER_CHANNEL_LIST);
		String crawlinfo = operations.rightPop();
		if (crawlinfo != null) {
			return (CrawlOuterUrl) JsonUtil.toObject(crawlinfo, CrawlOuterUrl.class);
		}
		return null;
	}
	
	public boolean pushOuterCrawl(CrawlOuterUrl url) {
		BoundListOperations<String, String> operations = queueRedisTemplate.boundListOps(CommandContant.CRAWL_OUTER_CHANNEL_LIST);
		long len = operations.leftPush(JsonUtil.json(url));
		return len > 0 ? true :false;
	}

}
