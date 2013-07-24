package com.easou.news.crawl.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class UrlUtil {
	/**
	 * 拼接url
	 * @param baseUri
	 * @param append
	 * @return
	 */
	public static String joinUrl(String baseUri, String append) {
		try {
			URL base = new URL(baseUri);
			if (append.startsWith("?"))
				append = base.getPath() + append;
			URL abs = new URL(base, append);
			return abs.toExternalForm();
		} catch (MalformedURLException e) {
			try {
				URL abs = new URL(append);
				return abs.toExternalForm();
			} catch (MalformedURLException e1) {}
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		String baseUri = "http://finance.sina.com.cn/china/20130318/135114867276.shtml";
		String append = "491595.html";
		URI base = URI.create(baseUri);
//		System.out.println(base.getHost());
//		System.out.println(base.getHost());
		System.out.println(joinUrl(baseUri, append));
	}
}
