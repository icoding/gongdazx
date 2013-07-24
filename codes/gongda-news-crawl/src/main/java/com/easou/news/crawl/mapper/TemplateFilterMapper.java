package com.easou.news.crawl.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository("templateFilterMapper")
public interface TemplateFilterMapper {
	List<String> selectRegexByFid(int fid);
}
