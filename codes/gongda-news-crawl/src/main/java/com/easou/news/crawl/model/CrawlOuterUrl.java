// Copyright(c) 2011 easou.com
package com.easou.news.crawl.model;

import java.net.URI;

/**
 * @author yunchat
 *
 */
public class CrawlOuterUrl {
    
    private int templateId;
    
    private String url;
    
    private int type;
    
    private int contentFrom;
    
    private String usedBy;

	/**
	 * @return the templateId
	 */
	public int getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the contentFrom
	 */
	public int getContentFrom() {
		return contentFrom;
	}

	/**
	 * @param contentFrom the contentFrom to set
	 */
	public void setContentFrom(int contentFrom) {
		this.contentFrom = contentFrom;
	}

	/**
	 * @return the usedBy
	 */
	public String getUsedBy() {
		return usedBy;
	}

	/**
	 * @param usedBy the usedBy to set
	 */
	public void setUsedBy(String usedBy) {
		this.usedBy = usedBy;
	}

    @Override
    public String toString() {
        return "{url:" + url + " \ntype:" + type + " \ntemplateId:" + templateId
                + "\ncontentFrom" + contentFrom + "}";
    }
}
