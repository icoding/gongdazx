package com.easou.news.crawl.job;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.easou.news.crawl.task.CrawlTask;

public abstract class TaskExecutorManager {
	
	private ThreadPoolTaskExecutor executor;
	
	/**
	 * 定时检查线程池是否满任务运行
	 */
	public void handle(){
		int num = executor.getCorePoolSize() - executor.getActiveCount();
		if(num > 0){
			for(int i = 0; i < num ; i++){
				CrawlTask task = createCrawlTask();
				executor.execute(task);
			}
		}
	}
	
	public abstract CrawlTask createCrawlTask();

	public void setExecutor(ThreadPoolTaskExecutor executor) {
		this.executor = executor;
	}
}
