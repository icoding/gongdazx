// Copyright(c) 2011 easou.com
package com.easou.news.crawl.model.enumtype;

/**
 * @author yunchat
 *
 */
public enum NewsTypeEnum {
	
	NORMAL(0, "normal"),
	PIC_GROUP(1, "pic group");
	
	private int value;

	private String name;

	private NewsTypeEnum(int value, String name) {
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
	
	public static NewsTypeEnum valueOfName(String name) {
		for (NewsTypeEnum e : NewsTypeEnum.values()) {
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return NORMAL;
	}
}
