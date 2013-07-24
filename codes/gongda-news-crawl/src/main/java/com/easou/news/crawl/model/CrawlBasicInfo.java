package com.easou.news.crawl.model;

import java.util.List;

public class CrawlBasicInfo {
    private String entryWay;// 种子
    private String pageUrl;// 页面url
    private String source;// 来源
    private String classify;// 类别

    private String navigation;// 导航
    private String pageTitle;// 页面标题
    private String catalogTitle;// 目录页标题
    private String contentTitle;// 内容页标题
    private Long downloadTime;// 下载时间
    private Long publishTime;// 发布时间
    private String content;// 内容

    private boolean hasImage;// 是否含有图片
    private List<CrawlImage> crawlImages;// 图片的信息集合
    private int weight;
    private int contentFrom;
    private int usedBy;
    private String host;
    
    private String class1;

	private int type;

	private int rankOrder; // 序号

    public int getRankOrder() {
        return rankOrder;
    }

    public void setRankOrder(int rankOrder) {
        this.rankOrder = rankOrder;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public List<CrawlImage> getCrawlImages() {
        return crawlImages;
    }

    public void setCrawlImages(List<CrawlImage> crawlImages) {
        this.crawlImages = crawlImages;
    }

    public Long getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(Long downloadTime) {
        this.downloadTime = downloadTime;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getEntryWay() {
        return entryWay;
    }

    public void setEntryWay(String entryWay) {
        this.entryWay = entryWay;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNavigation() {
        return navigation;
    }

    public void setNavigation(String navigation) {
        this.navigation = navigation;
    }

    public String getCatalogTitle() {
        return catalogTitle;
    }

    public void setCatalogTitle(String catalogTitle) {
        this.catalogTitle = catalogTitle;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
    public int getUsedBy() {
        return usedBy;
    }

    /**
     * @param usedBy
     *            the usedBy to set
     */
    public void setUsedBy(int usedBy) {
        this.usedBy = usedBy;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host) {
        this.host = host;
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
	 * @return the class1
	 */
	public String getClass1() {
		return class1;
	}

	/**
	 * @param class1 the class1 to set
	 */
	public void setClass1(String class1) {
		this.class1 = class1;
	}

    @Override
    public String toString() {
        return "{entryWay:" + entryWay + "\npageUrl:" + pageUrl + "\nsource:"
                + source + "\nclassify:" + classify + "\ndownloadTime:"
                + downloadTime + "\nnavigation:" + navigation + "\npageTitle:"
                + pageTitle + "\ncatalogTitle:" + catalogTitle
                + "\ncontentTitle:" + contentTitle + "\npublishTime:"
                + publishTime + "\ncontent:" + content + "\nhasImage:"
                + hasImage + "\ncrawlImages:" + crawlImages + "\nrankOrder:"
                + rankOrder + "}";
    }
}
