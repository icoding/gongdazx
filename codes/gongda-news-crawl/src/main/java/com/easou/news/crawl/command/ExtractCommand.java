package com.easou.news.crawl.command;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.factory.ClassRelationFactory;
import com.easou.news.crawl.model.CrawlBasicInfo;
import com.easou.news.crawl.model.CrawlUrl;
import com.easou.news.crawl.model.enumtype.ContentFromEnum;
import com.easou.news.crawl.model.enumtype.UsedByEnum;
import com.easou.news.crawl.result.Result;
import com.easou.news.crawl.result.enumtype.CommandEnum;
import com.easou.news.crawl.result.enumtype.FailureStatusEnum;
import com.easou.news.crawl.service.IExtractService;
import com.easou.news.crawl.service.IGroovyService;

@Component("extractCommand")
public class ExtractCommand implements Command {
	private static Logger logger = LoggerFactory.getLogger(ExtractCommand.class);
	
	@Autowired
	private IExtractService extractService;
	
	@Autowired
	private IGroovyService groovyService;
	
	@Autowired(required = false)
	private ClassRelationFactory classRelationFactory;

	@Override@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		CrawlUrl crawlUrl = (CrawlUrl) context.get(CommandContant.CRAWL_URL);
		if(crawlUrl.getType() == 0){
			List<CrawlUrl> catalogUrls = extractService.extractHtmlCatalog(crawlUrl);
			if(catalogUrls.isEmpty()){
				logger.warn("not find new page url, parent[{}]", crawlUrl.getUri());
				Result result = (Result)context.get(CommandContant.COMMAND_RESULT);
				result.setSuccess(false);
				result.getModel().put("model", CommandEnum.EXTRACT.getName());
				result.getModel().put("status", FailureStatusEnum.EXTRACT_NO_CRAWL_URL_WARN.getId());
				return true;
			}
			context.put(CommandContant.CATALOG_URL_LIST, catalogUrls);
		} else if(crawlUrl.getType() == 1){
			CrawlBasicInfo crawlBasicInfo = null;
			logger.info("ExtractCommand content:" + crawlUrl.toString());
			if (crawlUrl.getContentFrom() == ContentFromEnum.CRAWL.getValue()) {
				crawlBasicInfo = extractService.extractHtmlContent(crawlUrl);
			} else if (crawlUrl.getContentFrom() == ContentFromEnum.TRANSFORM.getValue()) {
				crawlBasicInfo = extractService.transformHtmlContent(crawlUrl);
			} else if (crawlUrl.getContentFrom() == ContentFromEnum.GROOVY.getValue()) {
				crawlBasicInfo = groovyService.extractHtmlContent(crawlUrl);
			}
			if (crawlBasicInfo == null || crawlBasicInfo.getPageUrl() == null) {
				logger.warn("Extract page error:", crawlUrl.toString());
				Result result = (Result)context.get(CommandContant.COMMAND_RESULT);
				result.setSuccess(false);
				result.getModel().put("model", CommandEnum.EXTRACT.getName());
				result.getModel().put("status", FailureStatusEnum.EXTRACT_PAGE_FAILURE.getId());
				return true;
			}
			crawlBasicInfo.setClass1(classRelationFactory.getClass1ByClass(crawlBasicInfo.getClassify()));
			crawlBasicInfo.setContentFrom(crawlUrl.getContentFrom());
			crawlBasicInfo.setUsedBy(UsedByEnum.valueOfName(crawlUrl.getUsedBy()).getValue());
			context.put(CommandContant.CRAWL_NEWS_BASICINFO, crawlBasicInfo);
		}
		return false;
	}
}
