package com.easou.news.crawl.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.factory.TemplateFactory;
import com.easou.news.crawl.frontier.WorkQueueFrontier;
import com.easou.news.crawl.model.CrawlBasicInfo;
import com.easou.news.crawl.model.CrawlUrl;
import com.easou.news.crawl.model.Template;
import com.easou.news.crawl.model.enumtype.UsedByEnum;
import com.easou.news.crawl.util.ContentFilterUtil;
import com.easou.news.crawl.util.HtmlUtil;
import com.easou.news.crawl.util.Md5Util;
import com.easou.news.crawl.util.UrlUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
public class ExtractServiceTest {
	
	private static final Pattern REDIRECT_PATTERN = Pattern.compile("CONTENT=\"0;[ ]*URL=([\\s\\S]*?)\"", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern FILTER_PATTERN_BR = Pattern.compile("(([\\s]*<br />)|([\\s]*<br/>)){2,}", Pattern.CASE_INSENSITIVE);

	
	@Autowired
	private IExtractService extractService;
	@Autowired
	private IHttpClientService httpClientService;
	
	@Resource(name="templateFactory")
	private TemplateFactory templateFactory;
	
//    @Resource(name = "uniRedisTemplate")
    private StringRedisTemplate uniRedisTemplate;

//	@Test
	public void extractCatalogTest(){
		try {
			templateFactory.loadTemplateById(866);
			String entryWay = "http://www.36kr.com/";
			String charset = null;
			CrawlUrl crawlUrl = new CrawlUrl();
			crawlUrl.setEntryWay(entryWay);
			crawlUrl.setUri(new URI("http://www.36kr.com/"));
			crawlUrl.setCatalog_level(2);
			String body = httpClientService.getResponseBodyByGet("http://www.baimen.cn/newsview/5/16030", charset);
			crawlUrl.setBody(body);
			
		System.out.println("body:" + body);
			List<CrawlUrl> crawlUrls = extractService.extractHtmlCatalog(crawlUrl);
			for(CrawlUrl u : crawlUrls){
				if (u.getUri().toString().indexOf("index_desktop_top10") < 0) {
					String ss = Md5Util.generateMd5ByUrl(u.getUri().toString());
					System.out.println(u.getRankOrder() + ", " +u.getUri() + "\t" + ss);
					uniRedisTemplate.delete(ss);
					
				}
			}
			System.out.println(crawlUrls.size());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void extractHtmlContentTest(){
		String entryWay = "http://www.yixieshi.com/it/";
		String url = "http://www.yixieshi.com/it/13391.html";
		Template tt = templateFactory.loadTemplateById(880);
		System.out.println("usedBy :" + tt.getUsedBy() + "--");
		System.out.println("usedBy :" + UsedByEnum.valueOfName(tt.getUsedBy()).getValue() + "--");
//		String entryWay = "http://sports.sohu.com/guojizuqiu.shtml";
//		String url = "http://sports.sohu.com/20121116/n357788781.shtml";
		String charset = "gbk";
		CrawlUrl contentUrl = new CrawlUrl();
		contentUrl.setUri(URI.create(url));
		contentUrl.setEntryWay(entryWay);
		contentUrl.setType(1);
		String body = httpClientService.getResponseBodyByGet(contentUrl.getUri().toString(), charset);
		contentUrl.setBody(body);
		System.out.println("-----------------------------------------");
		int len = body.length();
		
		
		System.out.println(ContentFilterUtil.filterContent("新浪网", body));
		
		System.out.println(body);
		System.out.println("-------------------------------------------");
		CrawlBasicInfo crawlBasicInfo = extractService.extractHtmlContent(contentUrl);

	//	CrawlBasicInfo crawlBasicInfo = extractService.transformHtmlContent(contentUrl);
//		Matcher matcher = FILTER_PATTERN_BR.matcher(crawlBasicInfo.getContent());
//		crawlBasicInfo.setContent(matcher.replaceAll("<br />"));
		System.out.println(crawlBasicInfo);
		
		String content = HtmlUtil.removeScriptTags(crawlBasicInfo.getContent());
		
	}
	
//	@Test
	public void extractSkipUrl(){
		try {
			templateFactory.loadTemplateById(1177);
			String entryWay = "http://www.shedunews.com/shiping.html";
			String charset = null;
			CrawlUrl crawlUrl = new CrawlUrl();
			crawlUrl.setEntryWay(entryWay);
			crawlUrl.setUri(new URI("http://www.shedunews.com/shiping/shipingtuijian/2013/03/04/491596.html"));
			crawlUrl.setCatalog_level(0);
			Template template = templateFactory.getTemplate(entryWay);
			String body = httpClientService.getResponseBodyByGet(crawlUrl.getUri().toString(), charset);
			crawlUrl.setBody(body);
			System.out.println("-----"+ body);	
			if(body != null){
				Matcher matcher = REDIRECT_PATTERN.matcher(body);
				if (matcher.find()) {
					String url =  matcher.group(1);
					if (url != null && url.length() != 0) {
						url = UrlUtil.joinUrl(crawlUrl.getUri().toString(), url);
						crawlUrl.setUri(URI.create(url));
						System.out.println(url);
						crawlUrl.setUri(URI.create(url));
						body = httpClientService.getResponseBodyByGet(url, charset);
						crawlUrl.setBody(body);
					}
				}
			}
//			System.out.println("-----"+ body);	
			
			CrawlBasicInfo crawlBasicInfo = extractService.transformHtmlContent(crawlUrl);
			System.out.println("-----------------------------------------------------------");
			System.out.println(crawlBasicInfo);
			
//			List<CrawlUrl> crawlUrls = extractService.extractHtmlCatalog(crawlUrl);
//			for(CrawlUrl u : crawlUrls){
//				System.out.println(u);
//			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void transformHtmlContentTest(){
		String entryWay = "http://www.smweekly.com/style/";
		String url = "http://www.smweekly.com/style/hotwind/201211/31678.aspx";
		String charset = null;
		CrawlUrl contentUrl = new CrawlUrl();
		contentUrl.setUri(URI.create(url));
		contentUrl.setEntryWay(entryWay);
		contentUrl.setType(1);
		String body = httpClientService.getResponseBodyByGet(contentUrl.getUri().toString(), charset);
		contentUrl.setBody(body);
		System.out.println(body);
		CrawlBasicInfo crawlBasicInfo = extractService.transformHtmlContent(contentUrl);
		System.out.println("-----------------------------------------------------------");
		System.out.println(crawlBasicInfo);
	}
	
}
