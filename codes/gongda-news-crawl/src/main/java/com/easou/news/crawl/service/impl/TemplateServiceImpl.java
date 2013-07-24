package com.easou.news.crawl.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.easou.news.crawl.mapper.TemplateMapper;
import com.easou.news.crawl.model.Template;
import com.easou.news.crawl.service.ITemplateService;

@Service
public class TemplateServiceImpl implements ITemplateService {
	@Resource(name="templateMapper")
	private TemplateMapper templateMapper;
	
	@Override
	public List<Template> getAll() {
		return templateMapper.selectAll();
	}
}
