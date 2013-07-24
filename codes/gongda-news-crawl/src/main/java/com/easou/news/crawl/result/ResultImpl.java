package com.easou.news.crawl.result;

import java.util.HashMap;
import java.util.Map;

public class ResultImpl implements Result{

	private Map<String,Object> model = new HashMap<String,Object>(8);

	private ResultCode code;

	private boolean success = false;

	public ResultImpl(){}

	public ResultImpl(boolean success){
		this.success = success;
	}

	public Map<String, Object> getModel() {
		return model;
	}
	public void setModel(Map<String, Object> model) {
		this.model = model;
	}
	public ResultCode getCode() {
		return code;
	}
	public void setCode(ResultCode code) {
		this.code = code;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

}
