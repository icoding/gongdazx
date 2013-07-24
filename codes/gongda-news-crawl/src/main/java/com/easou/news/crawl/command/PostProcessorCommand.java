package com.easou.news.crawl.command;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.frontier.WorkQueueFrontier;
import com.easou.news.crawl.model.CrawlUrl;
import com.easou.news.crawl.result.Result;
import com.easou.news.crawl.result.enumtype.CommandEnum;


@Component("postProcessorCommand")
public class PostProcessorCommand implements Command {
	private static Logger logger = LoggerFactory.getLogger(PostProcessorCommand.class);
	
	@Autowired(required = false)
	private WorkQueueFrontier workQueueFrontier;
	
	@Override@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		List<CrawlUrl> catalogUrls = (List<CrawlUrl>) context.get(CommandContant.CATALOG_URL_LIST);
		int size = 0;
		if(catalogUrls != null){
			size = catalogUrls.size();
			for(CrawlUrl url : catalogUrls){
//				logger.info("putQueue: url[{}] type[{}]", url.getUri().toString(), url.getType());
				workQueueFrontier.schedule(url);
			}
		}
		Result result = (Result)context.get(CommandContant.COMMAND_RESULT);
		result.setSuccess(true);
		result.getModel().put("model", CommandEnum.POST_PROCESSOR.getName());
		result.getModel().put("msg", "add crawl url size:" + size);
		return false;
	}
}
