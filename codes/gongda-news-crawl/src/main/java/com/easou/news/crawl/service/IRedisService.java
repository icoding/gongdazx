package com.easou.news.crawl.service;

import java.util.concurrent.TimeUnit;

import com.easou.news.crawl.model.CrawlBasicInfo;

/**
 * redis服务
 * 
 * @author moxm
 * 
 */
public interface IRedisService {

    /**
     * 保存新闻信息
     * @param crawlImage
     */
    boolean pushNewsInfo(String queueName, CrawlBasicInfo basicInfo);

    /**
     * 弹出新闻信息
     * @param crawlImage
     */
    CrawlBasicInfo popNewsInfo(String queueName);

    /**
     * 保存排行榜信息
     * @param crawlImage
     */
    boolean pushRankInfo(String queueName, CrawlBasicInfo basicInfo);

    /**
     * 弹出排行榜信息
     * @param crawlImage
     */
    CrawlBasicInfo popRankInfo(String queueName);
    
    
    boolean setImageInfos(final String key, final byte[] data, long timeout, TimeUnit unit);
    
    byte[] getImageInfos(final String key);

}
