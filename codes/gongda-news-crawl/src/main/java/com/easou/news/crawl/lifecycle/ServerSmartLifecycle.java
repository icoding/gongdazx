package com.easou.news.crawl.lifecycle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class ServerSmartLifecycle implements SmartLifecycle {
	private static Logger logger = LoggerFactory.getLogger(ServerSmartLifecycle.class);
	private boolean autoStartup = true;
	private boolean isRunning = false;
	private int phase = Integer.MAX_VALUE;

	/**
	 * 定时器
	 */
	private ThreadPoolTaskScheduler scheduler;
	/**
	 * 线程池
	 */
	private ThreadPoolTaskExecutor executor;

	@Override
	public void start() {
		logger.info("start "+ServerSmartLifecycle.class);
		this.isRunning = true;
	}

	@Override
	public void stop() {
		logger.info("stop "+ServerSmartLifecycle.class);
		// 先停止定时器
		scheduler.destroy();
		while (!scheduler.getScheduledExecutor().isTerminated()) {
			logger("scheduler",scheduler.getScheduledExecutor());
			try {
				scheduler.getScheduledExecutor().awaitTermination(5, TimeUnit.SECONDS);
			} catch (IllegalStateException e) {
				logger.error("wait scheduler stop.", e);
				break;
			} catch (InterruptedException e) {
				logger.error("wait scheduler stop.", e);
			}
		}
		logger("scheduler",scheduler.getScheduledExecutor());
		
		
		// 再停止线程池
		executor.destroy();
		while (!executor.getThreadPoolExecutor().isTerminated()) {
			logger("executor",executor.getThreadPoolExecutor());
			try {
				executor.getThreadPoolExecutor().awaitTermination(5, TimeUnit.SECONDS);
			} catch (IllegalStateException e) {
				logger.error("wait executor stop.", e);
				break;
			} catch (InterruptedException e) {
				logger.error("wait executor stop.", e);
			}
		}
		logger("executor",executor.getThreadPoolExecutor());

	}
	
	/**
	 * 输出日志
	 * @param name
	 * @param executorService
	 */
	private void logger(String name,ExecutorService executorService){
		if (executorService instanceof ThreadPoolExecutor){
			ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
			logger.info(name+"[TaskCount:{}][CompletedTaskCount:{}][QueueCount:{}][ActiveCount:{}]", 
					new Object[] { 
					threadPoolExecutor.getTaskCount(),
					threadPoolExecutor.getCompletedTaskCount(),
					threadPoolExecutor.getQueue().size(),
					threadPoolExecutor.getActiveCount()
					});
		}
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public int getPhase() {
		return phase;
	}

	@Override
	public boolean isAutoStartup() {
		return autoStartup;
	}

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

	public void setScheduler(ThreadPoolTaskScheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setExecutor(ThreadPoolTaskExecutor executor) {
		this.executor = executor;
	}

}
