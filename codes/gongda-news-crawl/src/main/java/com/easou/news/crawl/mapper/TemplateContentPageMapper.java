package com.easou.news.crawl.mapper;

import org.springframework.stereotype.Repository;

import com.easou.news.crawl.model.TemplateContentPage;

@Repository("templateContentPageMapper")
public interface TemplateContentPageMapper {
	TemplateContentPage selectByFid(int fid);
}