package com.easou.news.crawl.model;

import java.net.URI;

import com.google.gson.Gson;

/**
 * URL信息类
 * @author moxm
 * 
 */
public class CrawlUrl {
    private static transient Gson gson = new Gson();

    private String entryWay;// 入口url
    private URI uri;
    private transient String body;
    private int type; // 0：目录页， 1：内容页
    private int catalog_level;// 0: 一级目录页 1：二级目录页
    private String catalog_title;// 目录页标题
    private int weight;
    private int contentFrom; // 0：内容走转换，1:内容走爬虫
    private String usedBy; // online,app

    private boolean force = false;
    private transient Object holder;

    private String rankInfo;
    private int rankOrder;
    
    private int flag;

	public CrawlUrl() {
    }

    public CrawlUrl(String entryWay, URI uri, int type, int catalog_level) {
        this.entryWay = entryWay;
        this.uri = uri;
        this.type = type;
        this.catalog_level = catalog_level;
    }

    public int getRankOrder() {
        return rankOrder;
    }

    public void setRankOrder(int rankOrder) {
        this.rankOrder = rankOrder;
    }

    public String getRankInfo() {
        return rankInfo;
    }

    public void setRankInfo(String rankInfo) {
        this.rankInfo = rankInfo;
    }

    public String getCatalog_title() {
        return catalog_title;
    }

    public void setCatalog_title(String catalog_title) {
        this.catalog_title = catalog_title;
    }

    public int getCatalog_level() {
        return catalog_level;
    }

    public void setCatalog_level(int catalog_level) {
        this.catalog_level = catalog_level;
    }

    public static String toJson(CrawlUrl uri) {
        return gson.toJson(uri);
    }

    public static CrawlUrl fromJson(String json) {
        return gson.fromJson(json, CrawlUrl.class);
    }

    public String getEntryWay() {
        return entryWay;
    }

    public void setEntryWay(String entryWay) {
        this.entryWay = entryWay;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public Object getHolder() {
        return holder;
    }

    public void setHolder(Object holder) {
        this.holder = holder;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * @return the contentFrom
     */
    public int getContentFrom() {
        return contentFrom;
    }

    /**
     * @param contentFrom
     *            the contentFrom to set
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
     * @param usedBy
     *            the usedBy to set
     */
    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    public String getBaseUri() {
        return this.getUri().toString();
    }
    

    /**
	 * @return the flag
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

    @Override
    public String toString() {
        return "{url:" + uri.toString() + " \ntype:" + type
                + " \ncatalog_level:" + catalog_level + " \ncatalogTitle:"
                + catalog_title + "\ncontentFrom" + contentFrom + "}";
    }
}
