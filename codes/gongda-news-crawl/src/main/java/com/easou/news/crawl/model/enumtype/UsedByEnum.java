// Copyright(c) 2011 easou.com
package com.easou.news.crawl.model.enumtype;

/**
 * @author yunchat
 *
 */
public enum UsedByEnum {
	
	COMMON(0, "common"),
	ONLINE(1, "online"),
	APP(2, "app");
	
	private int value;

	private String name;

	private UsedByEnum(int value, String name) {
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
	
	public static UsedByEnum valueOfName(String name) {
		for (UsedByEnum e : UsedByEnum.values()) {
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return COMMON;
	}
}
