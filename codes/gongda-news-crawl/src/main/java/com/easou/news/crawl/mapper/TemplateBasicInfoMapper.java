package com.easou.news.crawl.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.easou.news.crawl.model.CrawlBasicInfo;

@Repository("templateBasicInfoMapper")
public interface TemplateBasicInfoMapper {
	List<CrawlBasicInfo> selectAllByFid(int fid);
}