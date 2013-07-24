package com.easou.news.crawl.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.model.CrawlBasicInfo;
import com.easou.news.crawl.result.Result;
import com.easou.news.crawl.result.enumtype.CommandEnum;
import com.easou.news.crawl.result.enumtype.FailureStatusEnum;
import com.easou.news.crawl.util.HtmlUtil;

/**
 * 过滤
 * @author moxm
 *
 */
@Component("filterCommand")
public class FilterCommand implements Command {
	private static Logger logger = LoggerFactory.getLogger(FilterCommand.class);
	
	@Override
	public boolean execute(Context context) throws Exception {
		CrawlBasicInfo basicInfo = (CrawlBasicInfo) context.get(CommandContant.CRAWL_NEWS_BASICINFO);
		if(filterByImage(basicInfo)){//根据图片过滤，如果实际有图但是图片信息为空则过滤
			logger.warn("filterByImageInfo: url[{}] entryWay[{}]", basicInfo.getPageUrl(), basicInfo.getEntryWay());
			Result result = (Result)context.get(CommandContant.COMMAND_RESULT);
			result.setSuccess(false);
			result.getModel().put("model", CommandEnum.FILTER.getName());
			result.getModel().put("status", FailureStatusEnum.EXTRACT_PAGE_FAILURE.getId());
			return true;
		}
		
		if(filterByPubTime(basicInfo)){//根据发布时间，发布时间为0则设置为当前时间
			logger.warn("dateFormatError: url[{}] entryWay[{}]", basicInfo.getPageUrl(), basicInfo.getEntryWay());
			basicInfo.setPublishTime(System.currentTimeMillis());
//			Result result = (Result)context.get(CommandContant.COMMAND_RESULT);
//			result.setSuccess(false);
//			result.getModel().put("model", CommandEnum.FILTER.getName());
//			result.getModel().put("status", FailureStatusEnum.EXTRACT_PAGE_FAILURE.getId());
//			return true;
		}
		
		if(filterByContent(basicInfo)){//根据内容过滤，内容为空则过滤
			logger.warn("filterByContent: url[{}] entryWay[{}]", basicInfo.getPageUrl(), basicInfo.getEntryWay());
			Result result = (Result)context.get(CommandContant.COMMAND_RESULT);
			result.setSuccess(false);
			result.getModel().put("model", CommandEnum.FILTER.getName());
			result.getModel().put("status", FailureStatusEnum.EXTRACT_PAGE_FAILURE.getId());
			return true;
		}
		
		return false;
	}

	private boolean filterByPubTime(CrawlBasicInfo basicInfo) {
		return basicInfo.getPublishTime()==null || basicInfo.getPublishTime()==0;
	}

	private boolean filterByContent(CrawlBasicInfo basicInfo) {
		if (basicInfo.getContent() == null) {
			return true;
		}
		String content = HtmlUtil.removeScriptTags(basicInfo.getContent());
		content = HtmlUtil.removeAllHtml(content);
		return StringUtils.isBlank(content);
	}

	private boolean filterByImage(CrawlBasicInfo basicInfo) {
		return basicInfo.isHasImage() && basicInfo.getCrawlImages().isEmpty();
	}
}