package com.easou.news.crawl.service;

import java.net.URI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.easou.news.crawl.model.CrawlUrl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
public class ExtractRankTest {

	@Autowired
	private IHttpClientService httpClientService;

	@Test
	public void extractRank(){
		String entryUrl = "http://news.163.com/rank/";
		String body = httpClientService.getResponseBodyByGet(entryUrl, null);
		CrawlUrl crawlUrl = new CrawlUrl();
		crawlUrl.setUri(URI.create(entryUrl));
		crawlUrl.setEntryWay(entryUrl);
		crawlUrl.setBody(body);
		
		Document document = Jsoup.parse(crawlUrl.getBody(),crawlUrl.getBaseUri());
		Element whole = document.getElementById("whole");
		Element leftArea = whole.nextElementSibling();
		Element tabBox = leftArea.child(0);
		Elements elements = tabBox.getElementsByClass("active");
		
//		Elements elements = document.select("#whole ~ #news div.left div.tabBox div.action a");
		for(Element element : elements){
			Elements elems = element.getElementsByTag("a");
			for(Element e : elems){
				String url = e.attr("href");
				System.out.println(url);
			}
		}
		
		
	}
}
