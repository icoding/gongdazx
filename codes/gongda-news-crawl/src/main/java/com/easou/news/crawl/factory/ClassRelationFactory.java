// Copyright(c) 2011 easou.com
package com.easou.news.crawl.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.easou.news.crawl.mapper.ClassRelationMapper;
import com.easou.news.crawl.model.ClassRelation;

/**
 * @author yunchat
 *
 */
public class ClassRelationFactory {
	
    private static Logger logger = LoggerFactory.getLogger(ClassRelationFactory.class);
	
	private Map<String,String> map = new HashMap<String,String>();

	@Autowired
	private ClassRelationMapper classRelationMapper;
	
	public void init() {
		logger.info("begin to init class relation");
		List<ClassRelation> relationList = classRelationMapper.selectAll();
		if (relationList == null || relationList.size() == 0) {
			return;
		}
		for (ClassRelation rel : relationList) {
			map.put(rel.getClass2(), rel.getClass1());
		}
		logger.info("class relation size:" + map.size());
	}
	
	public void reload() {
		List<ClassRelation> relationList = classRelationMapper.selectAll();
		if (relationList == null || relationList.size() == 0) {
			return;
		}
		Map<String,String> tmpMap = new HashMap<String,String>();
		for (ClassRelation rel : relationList) {
			tmpMap.put(rel.getClass2(), rel.getClass1());
		}
		map = tmpMap;
	}
	
	public String getClass1ByClass(String cls) {
		return map.get(cls);
	}

}
