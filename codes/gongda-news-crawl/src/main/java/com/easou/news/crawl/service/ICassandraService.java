package com.easou.news.crawl.service;

public interface ICassandraService {
	
	/**
	 * 保存图片
	 * @param key
	 * @param image
	 * @param type
	 * @return
	 */
	boolean saveImage(String key, byte[] image);
	
	/**
	 * 获取图片
	 * @param key
	 * @return
	 */
	byte[] getImage(String key);
}
