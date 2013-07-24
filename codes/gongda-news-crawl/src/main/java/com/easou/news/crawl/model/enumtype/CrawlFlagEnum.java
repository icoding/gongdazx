// Copyright(c) 2011 easou.com
package com.easou.news.crawl.model.enumtype;

/**
 * @author yunchat
 *
 */
public enum CrawlFlagEnum {
	
	NORMAL(0, "normal"),
	OUTER(1, "outer");
	
	private int value;

	private String name;

	private CrawlFlagEnum(int value, String name) {
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
	
	public static CrawlFlagEnum valueOfName(String name) {
		for (CrawlFlagEnum e : CrawlFlagEnum.values()) {
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return NORMAL;
	}
}
