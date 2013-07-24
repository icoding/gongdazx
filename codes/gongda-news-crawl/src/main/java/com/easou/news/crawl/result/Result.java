package com.easou.news.crawl.result;

import java.util.Map;

public interface Result {

	public Map<String, Object> getModel();

	public void setModel(Map<String, Object> model);

	public ResultCode getCode() ;

	public void setCode(ResultCode code) ;

	public boolean isSuccess() ;

	public void setSuccess(boolean success) ;
	
	

}
