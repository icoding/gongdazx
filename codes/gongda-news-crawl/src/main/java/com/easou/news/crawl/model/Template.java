package com.easou.news.crawl.model;

import java.io.Serializable;
import java.util.List;

/**
 * 模板类
 * @author moxm
 * 
 */
public class Template implements Serializable {
    private static final long serialVersionUID = 6992335486250800412L;

    private int id;
    private String source;
    private String classify;
    private int rank;
    private String webName;
    private String charset;
    private int contentFrom;
    private String contentExpression;
	private String usedBy;

    private String rankInfo;

    private List<String> filterRegexs;
    private List<String> webNameRegexs;
    private List<String> contentRegexs;
    private List<TemplateBasicInfo> templateBasicInfos;
    private List<TemplateTagWeight> templateTagWeights;
    private TemplateContentPage templateContentPage;

    public Template() {
    }

    public TemplateContentPage getTemplateContentPage() {
        return templateContentPage;
    }

    public void setTemplateContentPage(TemplateContentPage templateContentPage) {
        this.templateContentPage = templateContentPage;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public List<TemplateBasicInfo> getTemplateBasicInfos() {
        return templateBasicInfos;
    }

    public void setTemplateBasicInfos(List<TemplateBasicInfo> templateBasicInfos) {
        this.templateBasicInfos = templateBasicInfos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getWebName() {
        return webName;
    }
    
    public String getFirstWebName() {
        return webName.split(",")[0];
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public List<String> getFilterRegexs() {
        return filterRegexs;
    }

    public void setFilterRegexs(List<String> filterRegexs) {
        this.filterRegexs = filterRegexs;
    }

    public List<String> getWebNameRegexs() {
        return webNameRegexs;
    }

    public void setWebNameRegexs(List<String> webNameRegexs) {
        this.webNameRegexs = webNameRegexs;
    }

    public List<String> getContentRegexs() {
        return contentRegexs;
    }

    public void setContentRegexs(List<String> contentRegexs) {
        this.contentRegexs = contentRegexs;
    }

    public List<TemplateTagWeight> getTemplateTagWeights() {
        return templateTagWeights;
    }

    public void setTemplateTagWeights(List<TemplateTagWeight> templateTagWeights) {
        this.templateTagWeights = templateTagWeights;
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

    public String getRankInfo() {
        return rankInfo;
    }

    public void setRankInfo(String rankInfo) {
        this.rankInfo = rankInfo;
    }

    /**
	 * @return the contentExpression
	 */
	public String getContentExpression() {
		return contentExpression;
	}

	/**
	 * @param contentExpression the contentExpression to set
	 */
	public void setContentExpression(String contentExpression) {
		this.contentExpression = contentExpression;
	}


}