package com.easou.news.crawl.task;

import javax.annotation.Resource;

import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.frontier.WorkQueueFrontier;
import com.easou.news.crawl.model.CrawlUrl;
import com.easou.news.crawl.result.Result;
import com.easou.news.crawl.result.ResultImpl;
import com.easou.news.crawl.result.enumtype.FailureStatusEnum;

/**
 * 抓取任务主流程
 * @author moxm
 *
 */
public class CrawlTask implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(CrawlTask.class);
	
	@Autowired
	private WorkQueueFrontier workQueueFrontier;
	@Resource(name="catalogChainBase")
	private ChainBase catalogChainBase;
	@Resource(name="contentChainBase")
	private ChainBase contentChainBase;
	
	@Override
	public void run() {
		while(true){
			CrawlUrl crawlUrl = null;
			Result result = new ResultImpl(true);
			try {
				crawlUrl = workQueueFrontier.next();
				logger.info("fromQueue: url[{}] type[{}]", crawlUrl.getUri().toString(), crawlUrl.getType());
				ContextBase contextBase = new ContextBase();
				contextBase.put(CommandContant.COMMAND_RESULT, result);
				if(crawlUrl.getType() == 0){
					StopWatch stopWatch = new Log4JStopWatch();
					contextBase.put(CommandContant.PREPARE_CRAWL_URL, crawlUrl);
					catalogChainBase.execute(contextBase);
					stopWatch.stop("catalog whole");
				} else if(crawlUrl.getType() == 1){
					StopWatch stopWatch = new Log4JStopWatch();
					contextBase.put(CommandContant.PREPARE_CRAWL_URL, crawlUrl);
					contentChainBase.execute(contextBase);
					stopWatch.stop("content whole");
				}
			} catch (Exception e) {
				logger.error("url crawl error:" + crawlUrl.getUri().toString() , e);
				logger.error("crawlUrl.getUri().toString():" + ExceptionUtils.getMessage(e));
			} finally {
				if(crawlUrl != null){
					if (!result.isSuccess()) {
						Integer status = (Integer)(result.getModel().get("status"));
						logger.error(crawlUrl.getUri() + " false:" + result.getModel().get("model")
								+ " status:" + FailureStatusEnum.valueOfId(status).getName() + " " + crawlUrl.getEntryWay());
					} else {
						logger.info(crawlUrl.getUri() + " true:" + result.getModel().get("model")
								+ " msg:" + result.getModel().get("msg"));
					}					
					logger.info("recoverQueue: url[{}] type[{}]", crawlUrl.getUri().toString(), crawlUrl.getType());
					if(crawlUrl.getType() == 0 && crawlUrl.getCatalog_level() == 0)
						workQueueFrontier.finished(crawlUrl, 2*60*1000);		
					else
						workQueueFrontier.finished(crawlUrl, 30);
				} else {
					logger.error("crawlUrl is null");
				}
			}
		}
	}
}
