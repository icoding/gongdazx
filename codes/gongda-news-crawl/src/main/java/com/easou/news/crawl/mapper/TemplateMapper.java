package com.easou.news.crawl.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.easou.news.crawl.model.Template;

@Repository("templateMapper")
public interface TemplateMapper {
	List<Template> selectAll();
	
	Template findById(int id);
	
	List<Template> selectAllSeed();
	
	List<Template> selectDownSeed();
	
}