package com.easou.news.crawl.command;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.model.CrawlBasicInfo;
import com.easou.news.crawl.model.CrawlImage;
import com.easou.news.crawl.result.Result;
import com.easou.news.crawl.result.enumtype.CommandEnum;
import com.easou.news.crawl.result.enumtype.FailureStatusEnum;
import com.easou.news.crawl.service.ICassandraService;
import com.easou.news.crawl.service.IRedisService;

@Component("writeCommand")
public class WriteCommand implements Command {
	private static Logger logger = LoggerFactory.getLogger(WriteCommand.class);
	
	@Autowired
	private ICassandraService cassandraService;
	@Autowired
	private IRedisService redisService;
	
	@Override
	public boolean execute(Context context) throws Exception {
		CrawlBasicInfo basicInfo = (CrawlBasicInfo) context.get(CommandContant.CRAWL_NEWS_BASICINFO);
		Result result = (Result)context.get(CommandContant.COMMAND_RESULT);
		if(basicInfo.isHasImage()){
			List<CrawlImage> crawlImages = basicInfo.getCrawlImages();
			for(CrawlImage crawlImage : crawlImages){
				if(!cassandraService.saveImage(crawlImage.getMd5(), crawlImage.getData())){
					logger.error("imageSaveError: key[{}] url[{}]", crawlImage.getMd5(), crawlImage.getUrl());
					result.setSuccess(false);
					result.getModel().put("model", CommandEnum.WRITE.getName());
					result.getModel().put("status", FailureStatusEnum.WRITE_IMAGE_FAILURE.getId());
					return true;
				}
			}
		}
		logger.info("Write redis, url[{}] entryWay[{}]", basicInfo.getPageUrl(), basicInfo.getEntryWay());
		if(!redisService.pushNewsInfo(CommandContant.NEW_BASICINFO_LIST, basicInfo)){
			logger.error("newsInfoSaveError: url[{}] entryWay[{}]", basicInfo.getPageUrl(), basicInfo.getEntryWay());
			result.setSuccess(false);
			result.getModel().put("model", CommandEnum.WRITE.getName());
			result.getModel().put("status", FailureStatusEnum.WRITE_INFO_FAILURE.getId());
			return true;
		}
		result.setSuccess(true);
		result.getModel().put("model", CommandEnum.WRITE.getName());
		result.getModel().put("msg", "Write redis:" + basicInfo.getPageUrl());		
		return false;
	}
}