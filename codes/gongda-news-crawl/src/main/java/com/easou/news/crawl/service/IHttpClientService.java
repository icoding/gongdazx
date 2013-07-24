package com.easou.news.crawl.service;

public interface IHttpClientService {
	String getResponseBodyByGet(String url, String configCharset);
	String getResponseBodyByPost(String url);
	byte[] getImageByGet(String url, String refferer);
}
