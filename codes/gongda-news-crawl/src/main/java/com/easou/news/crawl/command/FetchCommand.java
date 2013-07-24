package com.easou.news.crawl.command;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.factory.TemplateFactory;
import com.easou.news.crawl.model.CrawlUrl;
import com.easou.news.crawl.model.Template;
import com.easou.news.crawl.model.enumtype.CrawlFlagEnum;
import com.easou.news.crawl.result.Result;
import com.easou.news.crawl.result.enumtype.CommandEnum;
import com.easou.news.crawl.result.enumtype.FailureStatusEnum;
import com.easou.news.crawl.service.IHttpClientService;
import com.easou.news.crawl.service.impl.ExtractServiceImpl;
import com.easou.news.crawl.util.UrlUtil;

@Component("fetchCommand")
public class FetchCommand implements Command {
	private static Logger logger = LoggerFactory.getLogger(FetchCommand.class);

	private static final Pattern REDIRECT_PATTERN = Pattern.compile("CONTENT=\"0;[ ]*URL=([\\s\\S]*?)\"", Pattern.CASE_INSENSITIVE);
	
	@Autowired
	private IHttpClientService httpClientService;
	@Resource(name="templateFactory")
	private TemplateFactory templateFactory;
	
	@Override@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		CrawlUrl crawlUrl = (CrawlUrl) context.get(CommandContant.PREPARE_CRAWL_URL);
		Template template = templateFactory.getTemplate(crawlUrl.getEntryWay());
	    logger.info("begin to fetch url: " + crawlUrl.toString() );
		if(template == null){
			logger.error("template not exist! entryway[{}]", crawlUrl.getEntryWay());
			Result result = (Result)context.get(CommandContant.COMMAND_RESULT);
			result.setSuccess(false);
			result.getModel().put("model", CommandEnum.FETCH.getName());
			result.getModel().put("status", FailureStatusEnum.FETCH_TEMPLATE_FAILURE.getId());
			return true;
		}

		String url = crawlUrl.getUri().toString();
		if(crawlUrl.getWeight() > 1) {
		    url = url.substring(0, url.length()-CommandContant.KEYNEWSTAG.length());
		    logger.info("Using original url to fetch ! " + crawlUrl.getUri().toString() + "  " + url);
		}
		String body = httpClientService.getResponseBodyByGet(url, template.getCharset());
		if(body != null){
			Matcher matcher = REDIRECT_PATTERN.matcher(body);
			if (matcher.find()) {
				url =  matcher.group(1);
				if (url != null && url.length() != 0 && !url.equalsIgnoreCase("/ie6")) {
					url = UrlUtil.joinUrl(crawlUrl.getUri().toString(), url);
					crawlUrl.setUri(URI.create(url));
					body = httpClientService.getResponseBodyByGet(crawlUrl.getUri().toString(), template.getCharset());
					if (body == null) {
						Result result = (Result)context.get(CommandContant.COMMAND_RESULT);
						result.setSuccess(false);
						result.getModel().put("model", CommandEnum.FETCH.getName());
						result.getModel().put("status", FailureStatusEnum.FETCH_EMPTY_BODY_FAILURE.getId());
						return true;
					}
				}
			}
			crawlUrl.setBody(body);
			if (crawlUrl.getFlag() == CrawlFlagEnum.NORMAL.getValue()) {
				crawlUrl.setContentFrom(template.getContentFrom());
				crawlUrl.setUsedBy(template.getUsedBy());
			}
		    logger.info("add body: " + crawlUrl.getUri().toString());
			context.put(CommandContant.CRAWL_URL, crawlUrl);
			return false;
		} else {
		    logger.info("Empty page!" + crawlUrl.getUri().toString() + "  " + url);
			Result result = (Result)context.get(CommandContant.COMMAND_RESULT);
			result.setSuccess(false);
			result.getModel().put("model", CommandEnum.FETCH.getName());
			result.getModel().put("status", FailureStatusEnum.FETCH_EMPTY_BODY_FAILURE.getId());
		}
		
		return true;
	}
	
	
}
