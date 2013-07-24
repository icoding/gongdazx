// Copyright(c) 2011 easou.com
package com.easou.news.crawl.task;

import java.net.URI;

import javax.annotation.Resource;

import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jsoup.helper.StringUtil;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.factory.TemplateFactory;
import com.easou.news.crawl.frontier.OuterQueue;
import com.easou.news.crawl.model.CrawlOuterUrl;
import com.easou.news.crawl.model.CrawlUrl;
import com.easou.news.crawl.model.Template;
import com.easou.news.crawl.result.Result;
import com.easou.news.crawl.result.ResultImpl;
import com.easou.news.crawl.result.enumtype.FailureStatusEnum;
import com.easou.news.crawl.util.JsonUtil;

/**
 * @author yunchat
 * 
 */
public class CrawlOuterTask {
	
	private static Logger logger = LoggerFactory.getLogger(CrawlOuterTask.class);

	@Resource(name = "contentChainBase")
	private ChainBase contentChainBase;

	@Resource(name = "templateFactory")
	private TemplateFactory templateFactory;

	@Autowired
	private OuterQueue outerQueue;

	public void process() {
		while (true) {
			CrawlUrl crawlUrl = null;
			CrawlOuterUrl outerUrl = null;
			Result result = new ResultImpl(true);
			try {
				outerUrl = outerQueue.popOuterCrawl();
				if (outerUrl == null) {
					return;
				}
				logger.info("Crawl Outer:" + outerUrl.toString());
				crawlUrl = outerUrlToCrawl(outerUrl);
				System.out.println("content from:" + crawlUrl.getContentFrom());
				ContextBase contextBase = new ContextBase();
				contextBase.put(CommandContant.COMMAND_RESULT, result);
				if (crawlUrl.getType() == 0) {
				} else if (crawlUrl.getType() == 1) {
					StopWatch stopWatch = new Log4JStopWatch();
					contextBase.put(CommandContant.PREPARE_CRAWL_URL, crawlUrl);
					contentChainBase.execute(contextBase);
					stopWatch.stop("content whole");
				}
			} catch (Exception e) {
				logger.error("url crawl error:" + outerUrl.toString(),  ExceptionUtils.getMessage(e));
			} finally {
				if (crawlUrl != null) {
					if (!result.isSuccess()) {
						Integer status = (Integer) (result.getModel().get("status"));
						logger.error(crawlUrl.getUri() + " false:"
								+ result.getModel().get("model") + " status:"
								+ FailureStatusEnum.valueOfId(status).getName()
								+ " " + crawlUrl.getEntryWay());
					} else {
						logger.info("Crawl Outer:" + crawlUrl.getUri() + " true :"
								+ result.getModel().get("model") + " msg:"
								+ result.getModel().get("msg"));
					}
				} else {
					logger.error("crawlUrl is null");
				}
			}
		}

	}

	private CrawlUrl outerUrlToCrawl(CrawlOuterUrl crawlOuterUrl) {
		CrawlUrl url = new CrawlUrl();
		url.setUri(URI.create(crawlOuterUrl.getUrl()));
		url.setUsedBy(crawlOuterUrl.getUsedBy());
		url.setType(crawlOuterUrl.getType());
		url.setContentFrom(crawlOuterUrl.getContentFrom());
		String key = CommandContant.ENTRY_COMMON_PREFIX
				+ crawlOuterUrl.getTemplateId();
		Template template = null;
		if (crawlOuterUrl.getTemplateId() != 0) {
			template = templateFactory.getTemplateById(
					crawlOuterUrl.getTemplateId(), key);
		}
		if (template == null) {
			templateFactory.putTemplate(key, new Template());
		}
		url.setEntryWay(key);
		return url;

	}

}
