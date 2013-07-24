// Copyright(c) 2011 easou.com
package com.easou.news.crawl.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easou.news.crawl.factory.TemplateFactory;
import com.easou.news.crawl.model.CrawlBasicInfo;
import com.easou.news.crawl.model.CrawlImage;
import com.easou.news.crawl.model.CrawlUrl;
import com.easou.news.crawl.model.Template;
import com.easou.news.crawl.model.TemplateBasicInfo;
import com.easou.news.crawl.model.enumtype.NewsTypeEnum;
import com.easou.news.crawl.service.IGroovyService;
import com.easou.news.crawl.service.IHttpClientService;
import com.easou.news.crawl.util.DateUtil;
import com.easou.news.crawl.util.HtmlUtil;
import com.easou.news.crawl.util.Md5Util;

/**
 * @author yunchat
 *
 */
@Service
public class GroovyServiceImpl implements IGroovyService {
	
	private static final Logger logger = LoggerFactory.getLogger(GroovyServiceImpl.class);

	@Resource(name="templateFactory")
	private TemplateFactory templateFactory;
	
	@Autowired
	private IHttpClientService httpClientService;
	
	/**
	 * 
	 */
	public GroovyServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.easou.news.crawl.service.IGroovyService#extractHtmlContent(com.easou.news.crawl.model.CrawlUrl)
	 */
	@Override
	public CrawlBasicInfo extractHtmlContent(CrawlUrl contentUrl) {
		if (StringUtil.isBlank(contentUrl.getBody())) {
			return new CrawlBasicInfo();
		}
		StopWatch stopWatch = new Log4JStopWatch();
		Template template = templateFactory.getTemplate(contentUrl.getEntryWay());
//		
		Document document = Jsoup.parse(contentUrl.getBody(), contentUrl.getBaseUri());
		
		CrawlBasicInfo crawlBasicInfo = packageBasicInfo(contentUrl, template, document);//创建CrawlBasicInfo对象并封装一些基础信息
		
		stopWatch.stop("content catalog");
		return crawlBasicInfo;
	}
	
	private CrawlBasicInfo packageBasicInfo(CrawlUrl contentUrl, Template template, Document document) {
		CrawlBasicInfo crawlBasicInfo = new CrawlBasicInfo();
		crawlBasicInfo.setEntryWay(contentUrl.getEntryWay());
		crawlBasicInfo.setPageUrl(contentUrl.getUri().toString());
		if (document != null) {
			crawlBasicInfo.setPageTitle(document.title());
		}
		crawlBasicInfo.setCatalogTitle(contentUrl.getCatalog_title());
		crawlBasicInfo.setWeight(contentUrl.getWeight());
		crawlBasicInfo.setSource(template.getSource());
		crawlBasicInfo.setClassify(template.getClassify());
		crawlBasicInfo.setDownloadTime(DateUtil.currentDateMilliseconds());
		crawlBasicInfo.setHost(contentUrl.getBaseUri());
		crawlBasicInfo.setType(NewsTypeEnum.PIC_GROUP.getValue());
		
		Element setInfoEl = document.getElementById("setInfo");
		String dateString =  setInfoEl.getElementsByAttributeValue("class", "nph_set_title").first()
								.getElementsByTag("span").last().html();
		long milltimes = DateUtil.fixedDateMilliseconds(dateString, "yyyy-MM-dd HH:mm");
		
		String title = setInfoEl.getElementsByTag("h1").html();	
		
		Element contentEl = setInfoEl.getElementsByTag("p").first();
		Elements span = contentEl.getElementsByTag("span");
		if (span != null) {
			span.remove();
		}
		String content = contentEl.html();

		Pattern pattern = Pattern.compile("<textarea class=\"hidden\" id=\"photoList\">([\\s\\S]*?)</textarea>");
		Matcher matcher = pattern.matcher(contentUrl.getBody());
		if (!matcher.find()) {
			return null;
		}
		String photoAreas = matcher.group(1);
		
		Document docum = Jsoup.parse(photoAreas);
		Elements photoLi = docum.getElementsByTag("li");
		
		List<CrawlImage> crawlImages = downloadContentImage(photoLi, crawlBasicInfo.getHost());
		if (crawlImages == null || crawlImages.size() == 0) {
			return null;
		}
		crawlBasicInfo.setHasImage(crawlImages.size()>0);
		crawlBasicInfo.setCrawlImages(crawlImages);
		
		crawlBasicInfo.setPublishTime(milltimes);
		crawlBasicInfo.setContentTitle(title);
		crawlBasicInfo.setContent(content);
		crawlBasicInfo.setDownloadTime(System.currentTimeMillis());
		logger.info("groovy:" + crawlBasicInfo);
		return crawlBasicInfo;
	}
	
	private List<CrawlImage> downloadContentImage(Elements elements, String entryWay) {
		List<CrawlImage> crawlImages = new ArrayList<CrawlImage>();
		for(Element imageElement : elements){
			Elements el = imageElement.getElementsByAttributeValue("title", "img");
			String url = el.html();
			String content = imageElement.getElementsByTag("p").html();
			String title = imageElement.getElementsByTag("h2").html();
					


			if (StringUtils.isBlank(url) || entryWay.equals(url))
				continue;
			
			String urlMd5 = Md5Util.generateMd5ByUrl(url);
			
			if(urlMd5 == null){
				logger.error("generationUrlMd5Fail: url[{}]", url);
				return null;
			}
			
			byte[] data = httpClientService.getImageByGet(url, null);
			if(data == null){
				logger.error("downloadImageEmpty: url[{}]", url);
				return null;
			}
			
			logger.info("findNewImage: url["+url+"] pageUrl["+entryWay+"] urlMd5["+urlMd5+"]");
			
			imageElement.replaceWith(new TextNode("(key:"+urlMd5+")",""));
			
			CrawlImage crawlImage = new CrawlImage();
			crawlImage.setData(data);
			crawlImage.setUrl(url);
			crawlImage.setMd5(urlMd5);
			crawlImage.setTitle(title);
			crawlImage.setContent(content);
			crawlImages.add(crawlImage);
			
		}
		
		return crawlImages;
	}



}
