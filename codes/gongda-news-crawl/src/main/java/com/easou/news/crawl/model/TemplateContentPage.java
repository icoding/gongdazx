package com.easou.news.crawl.model;

public class TemplateContentPage {
	private int id;
	private String action;
	private String expression;
	private String urlRegex;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrlRegex() {
		return urlRegex;
	}
	public void setUrlRegex(String urlRegex) {
		this.urlRegex = urlRegex;
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
