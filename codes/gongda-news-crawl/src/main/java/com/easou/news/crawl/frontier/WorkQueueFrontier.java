package com.easou.news.crawl.frontier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.easou.news.crawl.model.CrawlUrl;

@Component("workQueueFrontier")
public class WorkQueueFrontier {
	protected Map<String, WorkQueue> allQueues;
	protected BlockingQueue<String> readyClassQueues;
	protected SortedSet<WorkQueue> snoozedClassQueues;
	protected transient Timer wakeTimer;
	protected transient WakeTask nextWake;
	protected transient Timer wakeAllQueuesTimer;
	private HostnameQueueAssignmentPolicy queueAssignmentPolicy;
	
	private static final long DEFAULT_WAIT = 1000;
	
	@Resource(name="frontierRedisTemplate")
	private StringRedisTemplate queueRedisTemplate;

	@Resource(name="uriUniqFilter")
	private UriUniqFilter uriUniqFilter ;

	@PostConstruct
	public void init() {
		queueAssignmentPolicy = new HostnameQueueAssignmentPolicy();
		allQueues = Collections.synchronizedMap(new HashMap<String, WorkQueue>());
		readyClassQueues = new LinkedBlockingQueue<String>();
		snoozedClassQueues = Collections.synchronizedSortedSet(new TreeSet<WorkQueue>());

		this.wakeTimer = new Timer("waker for snoozedClassQueues");
		
		loadAllQueues();
		this.wakeAllQueuesTimer = new Timer("waker for allQueues");
		this.wakeAllQueuesTimer.schedule(new WakeAllQueuesTask(), 10000, 10000);
	}
	
	public void loadAllQueues() {
		Set<String> keys = queueRedisTemplate.keys("wq:*");
		for (String key : keys) {
			String k = key.substring(3);
			WorkQueue wq = getQueue(k);
			sendToQueue(wq);
		}
	}

	private WorkQueue getQueue(String key) {
		synchronized (allQueues) {
			WorkQueue q = allQueues.get(key);
			if (q == null) {
				q = new WorkQueue(key);
				q.setRedisTemplate(queueRedisTemplate);
				allQueues.put(key, q);
			}
			return q;
		}
	}

	public void schedule(CrawlUrl curl) {
		if (curl.isForce()) {
			uriUniqFilter.addUrl(curl);
			recive(curl);
		} else if (uriUniqFilter.addUrl(curl)) {
			recive(curl);
		}
	}

	private void recive(CrawlUrl curl) {
		String key = queueAssignmentPolicy.getClassKey(curl);
		WorkQueue wq = getQueue(key);
		wq.push(curl);
		sendToQueue(wq);
	}
	
	private void sendToQueue(WorkQueue wq){
		synchronized (readyClassQueues) {
			if(wq.isHeld()){
				return;
			}
			wq.setHeld(true);
		}
		readyQueue(wq);
	}

	public CrawlUrl next() throws InterruptedException {
		while (true) {
			WorkQueue readyQ = null;
			Object key = readyClassQueues.poll(DEFAULT_WAIT, TimeUnit.MILLISECONDS);
			if (key != null) {
				readyQ = (WorkQueue) this.allQueues.get((String) key);
			}
			if (readyQ != null) {
				CrawlUrl url = readyQ.pop();
				if (url != null) {
					return url;
				} else {
					// 延时
					snoozeQueue(readyQ, DEFAULT_WAIT);
				}
			}
		}
	}

	public void finished(CrawlUrl curi, long delay) {
		WorkQueue wq = (WorkQueue) curi.getHolder();
		if (delay > 0) {
			snoozeQueue(wq, delay);
		} else {
			readyQueue(wq);
		}
	}

	private void readyQueue(WorkQueue wq) {
		try {
			readyClassQueues.put(wq.getClassKey());
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void snoozeQueue(WorkQueue wq, long delay_ms) {
		long now = System.currentTimeMillis();
		long nextTime = now + delay_ms;
		wq.setWakeTime(nextTime);
		synchronized (snoozedClassQueues) {
			snoozedClassQueues.add(wq);
			
//			System.out.println("add snoozed "+wq.getClassKey());
			if (wq == snoozedClassQueues.first()) {
				this.nextWake = new WakeTask();
				this.wakeTimer.schedule(nextWake, delay_ms);
			}
		}
	}

	void wakeQueues() {
		long nowish = System.currentTimeMillis();
		synchronized (snoozedClassQueues) {
			long nextWakeDelay = 0;
			while (true) {
				if (snoozedClassQueues.isEmpty()) {
					return;
				}
				WorkQueue peek = (WorkQueue) snoozedClassQueues.first();
				nextWakeDelay = peek.getWakeTime() - nowish;
				if (nextWakeDelay <= 0) {
					snoozedClassQueues.remove(peek);
					peek.setWakeTime(0);
					readyClassQueues.add(peek.getClassKey());
//					System.out.println("wake snoozed "+peek.getClassKey());
				} else {
					break;
				}
			}
			this.nextWake = new WakeTask();
			this.wakeTimer.schedule(nextWake, nextWakeDelay);
		}
	}

	public class WakeTask extends TimerTask {
		@Override
		public void run() {
			synchronized (snoozedClassQueues) {
				if (this != nextWake) {
					// an intervening waketask was made
					return;
				}

				wakeQueues();
			}
		}
	}
	public class WakeAllQueuesTask extends TimerTask {
		@Override
		public void run() {
			loadAllQueues();
		}
	}
}
