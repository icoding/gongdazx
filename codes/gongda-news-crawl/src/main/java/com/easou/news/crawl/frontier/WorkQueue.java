package com.easou.news.crawl.frontier;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.easou.news.crawl.model.CrawlUrl;

public class WorkQueue implements Comparable<WorkQueue>{
    protected final String classKey;
    private long wakeTime = 0;
	private StringRedisTemplate redisTemplate;
    private boolean isHeld = false;
	
    public WorkQueue(final String pClassKey) {
        this.classKey = pClassKey;
    }
	public CrawlUrl pop(){
		try {
			String url = redisTemplate.opsForList().rightPop("wq:"+classKey);
			if(url != null){
				CrawlUrl curl = CrawlUrl.fromJson(url);
				curl.setHolder(this);
				return curl;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public void push(CrawlUrl curl){
		try {
			String url = CrawlUrl.toJson(curl);
			redisTemplate.opsForList().leftPush("wq:"+classKey, url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int compareTo(WorkQueue obj) {
		if (this == obj) {
			return 0; // for exact identity only
		}
		if (getWakeTime() > obj.getWakeTime()) {
			return 1;
		}
		if (getWakeTime() < obj.getWakeTime()) {
			return -1;
		}
		// at this point, the ordering is arbitrary, but still
		// must be consistent/stable over time
		return this.classKey.compareTo(obj.getClassKey());
	}
	public long getWakeTime() {
		return wakeTime;
	}
	public void setWakeTime(long wakeTime) {
		this.wakeTime = wakeTime;
	}
	public StringRedisTemplate getRedisTemplate() {
		return redisTemplate;
	}
	public void setRedisTemplate(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	public String getClassKey() {
		return classKey;
	}
	public boolean isHeld() {
		return isHeld;
	}
	public void setHeld(boolean isHeld) {
		this.isHeld = isHeld;
	}
}
