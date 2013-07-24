package com.easou.news.crawl.service;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {

	
	
	public static void main(String[] args) {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		ac.registerShutdownHook();
	}
}