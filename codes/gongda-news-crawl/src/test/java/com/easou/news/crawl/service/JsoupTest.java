// Copyright(c) 2011 easou.com
package com.easou.news.crawl.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author yunchat
 *
 */
public class JsoupTest {

	/**
	 * 
	 */
	public JsoupTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String ss = "dd<p>test1</p><p><strong>test2</strong></p>fdfdf";
		Document doc = Jsoup.parse(ss);
		
		Elements alEls =doc.children();
		for (Element el :alEls) {
			System.out.println("**" + el);
		}
		
		Elements els = doc.getElementsMatchingText("dddf");
		
		
		
		
		
		for (Element el :els) {
			System.out.println("--" + el);
		}
		
//		Element link = doc.select("a").first();
//		String relHref = link.attr("href"); // == "/"
//		String absHref = link.attr("abs:href"); 
//		System.out.println(relHref);
//		System.out.println(absHref);
//		ss = "<param name=\"allownetworking\" value=\"all\" /><param name=\"wmode\" value=\"opaque\" /><param name=\"flashvars\" value=\"sid=sect_0003&topicid=0003&hiddenR=true&vid=V8IKJDL8P&coverpic=" +
//				"http://vimg2.ws.126.net/image/snapshot/2012/12/8/Q/V8IKJDL8Q.jpg&pltype=10&autoplay=false\" /><!--[if !IE]>--><param name=\"ddd\" />";
//		doc = Jsoup.parse(ss, "http://www.elong.com/hh/cc/aa.html");
//		System.out.println(doc);
	}

}
