package com.easou.news.crawl.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * httpclient客户端工厂
 * @author moxm
 *
 */
@Component("httpClientFactory")
public class HttpClientFactory {
	private static Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);
	
	private final String HTTPCLIENT_PROP_FILE = "/httpclient.properties";
	private AbstractHttpClient httpClient;
	private String requestUA;
	
	@PostConstruct
	public void init(){
		int maxPerRoute = 0;
		int maxTotal = 0;
		int connectionTimeout = 0;
		int soTimeout = 0;
		int retryCount = 0;
		
		try {
			Properties prop = getProperties(HTTPCLIENT_PROP_FILE);
			maxPerRoute = Integer.parseInt(prop.getProperty("http.pool.maxPerRoute"));
			maxTotal = Integer.parseInt(prop.getProperty("http.pool.maxTotal"));
			connectionTimeout = Integer.parseInt(prop.getProperty("http.conn.timeout"));
			soTimeout = Integer.parseInt(prop.getProperty("http.conn.sotimeout"));
			retryCount = Integer.parseInt(prop.getProperty("http.conn.retry.count"));
			requestUA = prop.getProperty("http.request.header.ua");
		} catch (IOException e) {
			logger.error("init http properties error!", e);
			return;
		}

		PoolingClientConnectionManager manager = new PoolingClientConnectionManager();
		manager.setDefaultMaxPerRoute(maxPerRoute);
		manager.setMaxTotal(maxTotal);
		
		HttpParams params = new SyncBasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
		HttpConnectionParams.setSoTimeout(params, soTimeout);
		httpClient = new DefaultHttpClient(manager, params);
		httpClient.setHttpRequestRetryHandler(new StandardHttpRequestRetryHandler(retryCount, true));
		httpClient.addResponseInterceptor(new HttpResponseInterceptor(){
			@Override
			public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
				HttpEntity httpEntity = httpResponse.getEntity();
				Header header = httpEntity.getContentEncoding();
				if(header != null){
					for(HeaderElement element : header.getElements()){
						if("gzip".equalsIgnoreCase(element.getName())){
							httpResponse.setEntity(new GzipDecompressingEntity(httpResponse.getEntity()));
						}
					}
				}
			}
		});
	}

	private Properties getProperties(String propFile) throws IOException {
		InputStream is = this.getClass().getResourceAsStream(propFile);
		Properties pro = new Properties();
		pro.load(is);
		is.close();
		return pro;
	}

	public String getRequestUA() {
		return requestUA;
	}

	public HttpClient getInstance() {
		return httpClient;
	}
	
	@PreDestroy
	public void shutdown(){
		httpClient.getConnectionManager().shutdown();
	}
}
