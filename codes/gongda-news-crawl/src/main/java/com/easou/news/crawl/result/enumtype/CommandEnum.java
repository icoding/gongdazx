// Copyright(c) 2011 easou.com
package com.easou.news.crawl.result.enumtype;

/**
 * @author yunchat
 *
 */
public enum CommandEnum {

	FETCH("fetch"),
	EXTRACT("extrace"),
	POST_PROCESSOR("post processor"),
	FILTER("filter"),
	WRITE("write");

	private String name;

	private CommandEnum(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
}
