package com.easou.news.crawl.frontier;

import javax.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.easou.news.crawl.model.CrawlUrl;
import com.easou.news.crawl.util.Md5Util;

@Component("uriUniqFilter")
public class UriUniqFilter {
    @Resource(name = "uniRedisTemplate")
    private StringRedisTemplate uniRedisTemplate;
    public boolean addUrl(CrawlUrl curl) {
        String md5 = Md5Util.generateMd5ByUrl(curl.getUri().toString());
        boolean r = uniRedisTemplate.opsForValue().setIfAbsent(md5, "");
        return r;
    }
}
