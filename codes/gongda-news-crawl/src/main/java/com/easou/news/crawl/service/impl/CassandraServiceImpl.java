package com.easou.news.crawl.service.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easou.news.crawl.service.ICassandraService;
import com.easou.news.crawl.service.IRedisService;

@Service
public class CassandraServiceImpl implements ICassandraService {
	private static Logger logger = LoggerFactory.getLogger(CassandraServiceImpl.class);
	
	@Autowired
	private IRedisService redisService;
	
	@Override
	public boolean saveImage(String key, byte[] image) {
		try {
//			ICas07ClientMgr cas07ClientMgr = cassandraFactory.getImageClient();
//			cas07ClientMgr.insert(key, image, new byte[]{1});
			redisService.setImageInfos(key, image, 24, TimeUnit.HOURS);
			return true;
		} catch (Exception e) {
			logger.error("save image error! key[{}]", key, e);
		}
		return false;
	}

	@Override
	public byte[] getImage(String key) {
		try {
			return redisService.getImageInfos(key);
		} catch (Exception e) {
			logger.error("get image error! key[{}]", key, e);
		}
		
		return null;
	}
}
