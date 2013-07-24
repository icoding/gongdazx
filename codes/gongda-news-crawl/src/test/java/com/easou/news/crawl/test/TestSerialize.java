// Copyright(c) 2011 easou.com
package com.easou.news.crawl.test;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

/**
 * @author yunchat
 *
 */
public class TestSerialize {

	/**
	 * 
	 */
	public TestSerialize() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JdkSerializationRedisSerializer ser = new JdkSerializationRedisSerializer();
		System.out.println("test".getBytes());
		System.out.println(ser.serialize("test".getBytes()));
		
		
	}

}
