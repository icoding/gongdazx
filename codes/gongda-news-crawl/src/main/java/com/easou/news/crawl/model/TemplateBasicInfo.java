package com.easou.news.crawl.model;

/**
 * 新闻基础信息的模板类
 * @author moxm
 *
 */
public class TemplateBasicInfo {
	private int id;
	private String name;
	private String property;
	private String action;
	private String expression;
	private String datePattern;
	private String holdTags;
	private boolean hasImage;
	
	public boolean isHasImage() {
		return hasImage;
	}
	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}
	public String getDatePattern() {
		return datePattern;
	}
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}
	public String getHoldTags() {
		return holdTags;
	}
	public void setHoldTags(String holdTags) {
		this.holdTags = holdTags;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
}
