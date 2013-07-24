package com.easou.news.crawl.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.easou.news.crawl.model.ClassRelation;

@Repository("classRelation")
public interface ClassRelationMapper {
	
	List<ClassRelation>selectAll();

	
}