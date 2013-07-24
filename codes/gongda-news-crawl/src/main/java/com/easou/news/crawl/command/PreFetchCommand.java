package com.easou.news.crawl.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.stereotype.Component;

@Component("preFetchCommand")
public class PreFetchCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		return false;
	}
}