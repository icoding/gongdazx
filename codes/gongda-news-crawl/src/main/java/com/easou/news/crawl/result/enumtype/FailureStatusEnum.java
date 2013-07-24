// Copyright(c) 2011 easou.com
package com.easou.news.crawl.result.enumtype;


/**
 * @author yunchat
 *
 */
public enum FailureStatusEnum {
	
	DEFAULT(0, "default"),
	FETCH_TEMPLATE_FAILURE(1, "template not exist"),
	FETCH_EMPTY_BODY_FAILURE(2, "Empty page!"),
	EXTRACT_NO_CRAWL_URL_WARN(3, "not find new page url"),
	EXTRACT_PAGE_FAILURE(4, "Extract page failure"),
	FILTER_IMAGE_FAILURE(5, "filterByImageInfo"),
	FILTER_DATE_FAILURE(6, "dateFormatError"),
	FILTER_CONTENT_FAILURE(7, "filterByContent"),
	WRITE_IMAGE_FAILURE(8, "write image failure"),
	WRITE_INFO_FAILURE(9, "write info failure");
	
	private int id;

	private String name;

	private FailureStatusEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	public static FailureStatusEnum valueOfId(int id) {
		for (FailureStatusEnum e : FailureStatusEnum.values()) {
			if (e.getId() == id) {
				return e;
			}
		}
		return DEFAULT;
	}
	
}
