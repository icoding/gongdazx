package com.easou.news.crawl.frontier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.easou.news.crawl.mapper.TemplateMapper;
import com.easou.news.crawl.model.CrawlUrl;
import com.easou.news.crawl.model.Template;

public class SeedManager {

	private StringRedisTemplate queueRedisTemplate;
	
	private TemplateMapper templateMapper;

	private HostnameQueueAssignmentPolicy queueAssignmentPolicy = new HostnameQueueAssignmentPolicy();
	
	public void loadSeeds(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Set<String> keys = queueRedisTemplate.keys("sq:*");
		for(String sk : keys){
			String classkey = sk.substring(3);
			if(queueRedisTemplate.opsForList().size("wq:"+classkey) < queueRedisTemplate.opsForSet().size("sq:"+classkey)){
				Set<String> uris = queueRedisTemplate.opsForSet().members("sq:"+classkey);
				if(uris != null){
					for(String uri :uris){
						queueRedisTemplate.opsForList().leftPush("wq:"+classkey, uri);
					}
				}
			}else{
				System.out.println("["+sdf.format(date)+"] classkey ["+classkey+"] is full.");
			}
		}
	}
	
	public void reloadAllSeed(){
		removeDownSeed();
		System.out.println("begin to load all seed!");
		List<Template> templateList = templateMapper.selectAllSeed();
		
		for (Template template : templateList) {
			try {
				System.out.println(template.getWebName());
	        	String[] webNames = template.getWebName().split(",");
	        	for (String webName : webNames) {
	        		if (StringUtils.isBlank(webName)) {
	        			continue;
	        		}
					CrawlUrl uri = new CrawlUrl();
					uri.setUri(URI.create(webName));
					uri.setEntryWay(webName);
					String classkey = queueAssignmentPolicy.getClassKey(uri);
					String json = CrawlUrl.toJson(uri);
					queueRedisTemplate.opsForSet().add("sq:" + classkey, json);	        		
	        	}
			} catch (Exception e) {
				System.out.println("add error:" + template.getWebName());				
			}

		}
		System.out.println("End reload all seed with size:" + templateList.size());
	}
	
	public void loadSeedById(int id){
		System.out.println("begin to load seed id:" + id);
		Template template = templateMapper.findById(id);
		try {
			System.out.println(template.getWebName());
        	String[] webNames = template.getWebName().split(",");
        	for (String webName : webNames) {
        		if (StringUtils.isBlank(webName)) {
        			continue;
        		}
				CrawlUrl uri = new CrawlUrl();
				uri.setUri(URI.create(webName));
				uri.setEntryWay(webName);
				String classkey = queueAssignmentPolicy.getClassKey(uri);
				String json = CrawlUrl.toJson(uri);
				queueRedisTemplate.opsForSet().add("sq:" + classkey, json);	        		
        	}
		} catch (Exception e) {
			System.out.println("add error:" + template.getWebName());				
		}

		System.out.println("End load seed id" + id);
	}	
	
	private void removeDownSeed() {
		System.out.println("begin to remove down seed!");
		List<Template> templateList = templateMapper.selectDownSeed();
		for (Template template : templateList) {
			try {
	        	String[] webNames = template.getWebName().split(",");
	        	for (String webName : webNames) {
	        		if (StringUtils.isBlank(webName)) {
	        			continue;
	        		}
					CrawlUrl uri = new CrawlUrl();
					uri.setUri(URI.create(webName));
					uri.setEntryWay(webName);
					String classkey = queueAssignmentPolicy.getClassKey(uri);
					String json = CrawlUrl.toJson(uri);
					System.out.println("remove key:" + classkey + "\tjson:" + json);
					queueRedisTemplate.opsForSet().remove("sq:" + classkey, json);	        		
	        	}
			} catch (Exception e) {
				System.out.println("remove error:" + template.getWebName());
			}
		}
		System.out.println("End remove down seed with size:" + templateList.size());
	}
	
	public void addSeeds() throws IOException {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("seed-uri.conf");
		LineIterator it = IOUtils.lineIterator(input, "UTF-8");
		while (it.hasNext()) {
			String url = it.nextLine();
			System.out.println(url);
			CrawlUrl uri = new CrawlUrl();
			uri.setUri(URI.create(url));
			uri.setEntryWay(url);
			String classkey = queueAssignmentPolicy.getClassKey(uri);
			String json = CrawlUrl.toJson(uri);
			queueRedisTemplate.opsForSet().add("sq:" + classkey, json);
		}
		input.close();
	}

	public StringRedisTemplate getQueueRedisTemplate() {
		return queueRedisTemplate;
	}

	public void setQueueRedisTemplate(StringRedisTemplate queueRedisTemplate) {
		this.queueRedisTemplate = queueRedisTemplate;
	}
	
	/**
	 * @param templateMapper the templateMapper to set
	 */
	public void setTemplateMapper(TemplateMapper templateMapper) {
		this.templateMapper = templateMapper;
	}	
}
