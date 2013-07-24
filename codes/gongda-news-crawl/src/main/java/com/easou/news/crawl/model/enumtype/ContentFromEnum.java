// Copyright(c) 2011 easou.com
package com.easou.news.crawl.model.enumtype;

/**
 * @author yunchat
 *
 */
public enum ContentFromEnum {

	CRAWL(0, "crawl"),
	TRANSFORM(1, "transform"),
	GROOVY(2, "groovy");
	
	private int value;

	private String name;

	private ContentFromEnum(int value, String name) {
		this.value = value;
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	
}
