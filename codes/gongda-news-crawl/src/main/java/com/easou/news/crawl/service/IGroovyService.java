// Copyright(c) 2011 easou.com
package com.easou.news.crawl.service;

import com.easou.news.crawl.model.CrawlBasicInfo;
import com.easou.news.crawl.model.CrawlUrl;

/**
 * @author yunchat
 *
 */
public interface IGroovyService {


	CrawlBasicInfo extractHtmlContent(CrawlUrl crawlUrl);

}
