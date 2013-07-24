// Copyright(c) 2011 easou.com
package com.easou.news.crawl.service;

import java.net.URI;

/**
 * @author yunchat
 *
 */
public class TestUri {

	/**
	 * 
	 */
	public TestUri() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		URI uri = URI.create("http://www.chinanews.com/sportchannel/");
		System.out.println(uri.getHost());
		System.out.println(uri.getScheme());
		System.out.println(uri.getUserInfo());		

	}

}
