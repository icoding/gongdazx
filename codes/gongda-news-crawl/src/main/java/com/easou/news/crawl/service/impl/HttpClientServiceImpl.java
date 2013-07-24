package com.easou.news.crawl.service.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easou.news.crawl.factory.HttpClientFactory;
import com.easou.news.crawl.service.IHttpClientService;

@Service
public class HttpClientServiceImpl implements IHttpClientService {
	private static Logger logger = LoggerFactory.getLogger(HttpClientServiceImpl.class);
	
	@Autowired
	private HttpClientFactory httpClientFactory;
	
	@Override
	public String getResponseBodyByGet(String url, String configCharset) {
		StopWatch stopWatch = new Log4JStopWatch();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent", httpClientFactory.getRequestUA());
		String body = null;
		try {
			HttpResponse httpResponse = httpClientFactory.getInstance().execute(httpGet);
			StatusLine statusLine = httpResponse.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				HttpEntity httpEntity = httpResponse.getEntity();
				
				if(httpEntity != null){
					//编码获得策略
					String defaultCharset = "gbk";
					boolean hasCharSet = false;
					if(StringUtils.isBlank(configCharset)){
						Charset charset = ContentType.getOrDefault(httpEntity).getCharset();
						if(charset != null) {
							defaultCharset = charset.name();
							hasCharSet = true;
						}
						byte[] bytes = EntityUtils.toByteArray(httpEntity);
						body = new String(bytes, defaultCharset);
			            if (!hasCharSet) {
//			                String regEx="(?=<meta).*?(?<=charset=[\\'|\\\"]?)([[a-z]|[A-Z]|[0-9]|-]*)";
			                String regEx="<meta.*charset=[\\'|\\\"]?([a-zA-Z0-9-]+)";
			                Pattern p=Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
			                Matcher m=p.matcher(body);
			                String charSet = "";
			                if (m.find()) {
			                	charSet = m.group(1);			                	
			                }
			                if (!charSet.equalsIgnoreCase(defaultCharset) && !charSet.equalsIgnoreCase("gb2312")) {
			                	body = new String(bytes, charSet);
			                }
			            }
			    		stopWatch.stop("body response");
						return body;
					} else {
						defaultCharset = configCharset;
					}
					byte[] bytes = EntityUtils.toByteArray(httpEntity);
					
					body = new String(bytes, defaultCharset);
				}
			}
		} catch (ClientProtocolException e) {
			logger.error("get response body error! url[{}]", url, e);
		} catch (IOException e) {
			logger.error("get response body error! url[{}]", url, e);
		} finally {
			httpGet.abort();//终止本次请求
		}
		
		stopWatch.stop("body response");
		return body;
	}

	@Override
	public String getResponseBodyByPost(String url) {
		return null;
	}

	@Override
	public byte[] getImageByGet(String url,String refferer) {
		StopWatch stopWatch = new Log4JStopWatch();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent", httpClientFactory.getRequestUA());
		if (refferer != null) {
			httpGet.setHeader("Referer", refferer);	
		}
		byte[] data = null;
		try {
			HttpResponse httpResponse = httpClientFactory.getInstance().execute(httpGet);
			StatusLine statusLine = httpResponse.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				HttpEntity httpEntity = httpResponse.getEntity();
				if(httpEntity != null){
					data = EntityUtils.toByteArray(httpEntity);	
				}
			}
		} catch (ClientProtocolException e) {
			logger.error("get image error! url[{}]", url, e);
		} catch (IOException e) {
			logger.error("get image error! url[{}]", url, e);
		} finally {
			httpGet.abort();//终止本次请求
		}
		stopWatch.stop("image response");
		return data;
	}
}
