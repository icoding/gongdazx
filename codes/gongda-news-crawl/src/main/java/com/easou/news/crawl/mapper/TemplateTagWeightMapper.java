package com.easou.news.crawl.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.easou.news.crawl.model.TemplateTagWeight;

@Repository("templateTagWeightMapper")
public interface TemplateTagWeightMapper {
    List<TemplateTagWeight> selectByFid(int fid);
}