package com.easou.news.crawl.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
import com.easou.news.crawl.service.impl.TransformServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class TransformServiceTest {
    @Autowired
    private IHttpClientService httpClientService;
    
	@Autowired
	private ITransformService transformService;

//    @Test
    public void extractCatalogTest() throws IOException {
        String body;
        byte[] contentBytes = null;
        try {
            // String entryWay = "http://blog.ifeng.com/article/20012179.html";
            String entryWay = "http://sports.sohu.com/20121022/n355380032.shtml";
            // String entryWay = "http://ent.163.com/12/1108/12/8FPN87JM00031GVS.html";
            // String entryWay = "http://money.163.com/12/1105/10/8FHQ92J400252V0H.html";
            CrawlUrl crawlUrl = new CrawlUrl();
            crawlUrl.setEntryWay(entryWay);
            crawlUrl.setUri(new URI(entryWay));
            crawlUrl.setCatalog_level(0);
            body = httpClientService.getResponseBodyByGet(crawlUrl.getUri().toString(), "gb2312");
            contentBytes = body.getBytes("gbk");
            System.out.println(body);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // System.exit(0);

        // send webpage body to transform service ...
//        TransformServiceImpl transformService = new TransformServiceImpl();
        String transformedContent = transformService.transform(contentBytes);

        System.out.println(transformedContent);
        
        Document document = Jsoup.parse(transformedContent);
        System.out.println(document.getElementById("Main").attr("title"));
        System.out.println(document.getElementsByTag("title_tag").text());
        System.out.println(document.getElementsByTag("time_source_tag").text());
        System.out.println(document.getElementsByTag("content_tag").text());
        System.out.println(document.getElementsByTag("image_tag"));
        
        Elements elements =  document.getElementsByTag("img");

        for (Element element : elements) {
        	String alt = element.attr("alt");
        	String url = element.attr("src");
        	url = url.substring(url.lastIndexOf("url=") + 4);
        	System.out.println("alt:" + alt);
        	System.out.println("url:" + url);
        	System.out.println("parent:" + element.parent());        	
        }

//        File f = new File("utf8output.txt");
//        BufferedWriter output = new BufferedWriter(new FileWriter(f));
//        output.write(transformedContent);
//        output.close();
    }
    
    @Test
    public void jsoupDocumentTest() {
    	String ss = "<xml><Main id='Main'>dd<img src='sss'/>dddddddd<img src='bbb'/></Main></xml>";
    	Document d = Jsoup.parse(ss);
        System.out.println("1" + d.getElementsByTag("content_tag").text());
        System.out.println("2:" + d.getElementById("Main").attr("title"));
        Elements e = d.getElementsByTag("img");
        for (Element ee : e) {
            System.out.println(ee.attr("src"));
        }
        System.out.println("---end---");
    }
}
