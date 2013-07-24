package com.easou.news.crawl.service.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.easou.news.crawl.model.CrawlBasicInfo;
import com.easou.news.crawl.service.IRedisService;
import com.easou.news.crawl.util.JsonUtil;

@Service
public class RedisServiceImpl implements IRedisService {
    private static Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Resource(name = "storageRedisTemplate")
    private StringRedisTemplate storageRedisTemplate;

    @Override
    public boolean pushNewsInfo(String queueName, CrawlBasicInfo basicInfo) {
        try {
            String json = JsonUtil.json(basicInfo);
            BoundListOperations<String, String> operations = storageRedisTemplate
                    .boundListOps(queueName);
            return operations.leftPush(json) > 0;
        } catch (Exception e) {
            logger.error("", e);
        }

        return false;
    }

    @Override
    public CrawlBasicInfo popNewsInfo(String queueName) {
        try {
            BoundListOperations<String, String> operations = storageRedisTemplate
                    .boundListOps(queueName);
            String jsonStr = operations.rightPop();
            if (jsonStr != null)
                return (CrawlBasicInfo) JsonUtil.toObject(jsonStr);
        } catch (Exception e) {
            logger.error("", e);
        }

        return null;
    }

    @Override
    public boolean pushRankInfo(String queueName, CrawlBasicInfo basicInfo) {
        try {
            String json = JsonUtil.json(basicInfo);
            BoundSetOperations<String, String> operations = storageRedisTemplate
                    .boundSetOps(queueName);
            return operations.add(json);
        } catch (Exception e) {
            logger.error("", e);
        }

        return false;
    }

    @Override
    public CrawlBasicInfo popRankInfo(String queueName) {
        try {
            BoundSetOperations<String, String> operations = storageRedisTemplate
                    .boundSetOps(queueName);
            String jsonStr = operations.pop();
            if (jsonStr != null)
                return (CrawlBasicInfo) JsonUtil.toObject(jsonStr);
        } catch (Exception e) {
            logger.error("", e);
        }

        return null;
    }
    
    public boolean setImageInfos(final String key, final byte[] data, long timeout, TimeUnit unit) {
    	try {
    		final byte[] rawKey = key.getBytes();
    		final long rawTimeout = unit.toSeconds(timeout);
    		storageRedisTemplate.execute(new RedisCallback<Object>() {    			
    			public Object doInRedis(RedisConnection connection) throws DataAccessException {
    				connection.setEx(rawKey, (int) rawTimeout, data);
    				return null;
    			}
    		}, true);
    	} catch (Exception e) {
            logger.error("pushImageInfos", e);    		
    	}
    	return true;
    }
    
    public byte[] getImageInfos(final String key) {
		byte[] datas = null;
    	try {
    		final byte[] rawKey = key.getBytes();
    		datas = storageRedisTemplate.execute(new RedisCallback<byte[]>() {	
    			public byte[] doInRedis(RedisConnection connection) {
    				return connection.get(rawKey);
    			}
    		}, true);
    		
    	} catch (Exception e) {
            logger.error("pushImageInfos", e);    		
    	}
    	return datas;
    }
}
