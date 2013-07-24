package com.easou.news.crawl.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.mapper.TemplateMapper;
import com.easou.news.crawl.model.Template;

//@Component("templateFactory")
public class TemplateFactory {
	
    private static Logger logger = LoggerFactory.getLogger(TemplateFactory.class);

    @Autowired
    private TemplateMapper templateMapper;
    private Map<String, Template> map;

//    @PostConstruct
    public void init() {
        logger.info("begin to init template!");
        map = new HashMap<String, Template>();
        List<Template> templates = templateMapper.selectAll();
        for (Template template : templates) {
        	String[] webNames = template.getWebName().split(",");
        	for (String webName : webNames) {
        		if (StringUtils.isBlank(webName)) {
        			continue;
        		}
        		map.put(webName, template);
        	}
        }
        logger.info("template size : " + map.size());
    }

    public void reload() {
        logger.info("begin to reload template!");
        Map<String, Template> tmpMap = new HashMap<String, Template>();

        List<Template> templates = templateMapper.selectAll();
        for (Template template : templates) {
        	String[] webNames = template.getWebName().split(",");
        	for (String webName : webNames) {
        		if (StringUtils.isBlank(webName)) {
        			continue;
        		}
            	tmpMap.put(webName, template);
        	}
        }

        if (tmpMap.size() != 0) {
        	map = tmpMap;
        }
        
        logger.info("template size : " + map.size());
    }

    public Template getTemplate(String webname) {
        return map.get(webname);
    }

    public void putTemplate(String webName, Template template) {
        map.put(webName, template);
    }

    public Template loadTemplateById(int id) {
        if (map == null) {
            map = new HashMap<String, Template>();
        }
        Template template = this.templateMapper.findById(id);
        
        if (template == null || StringUtils.isBlank(template.getWebName())) {
        	return  null;
        }
        
    	String[] webNames = template.getWebName().split(",");
    	for (String webName : webNames) {
    		if (StringUtils.isBlank(webName)) {
    			continue;
    		}
    		System.out.println("webName:" + webName);
        	map.put(webName, template);
    	}
        return template;
    }
    
    public Template getTemplateById(int id, String key) {
        if (map == null) {
            map = new HashMap<String, Template>();
        }
    	Template template = map.get(key);
    	if (template == null) {
    		template  = this.loadTemplateById(id);
    		if (template != null) {
    			map.put(key, template);
    		}
    	}
    	return template;
    }
}
