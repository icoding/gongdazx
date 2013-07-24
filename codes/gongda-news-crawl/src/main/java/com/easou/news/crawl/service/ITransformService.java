package com.easou.news.crawl.service;

import java.io.IOException;
import java.net.SocketException;

public interface ITransformService {

    /**
     * 请求转换服务, 发送 html完整页面, 返回 转换后新闻全文页面 
     * @param htmlData
     * @return
     * @throws SocketException 
     * @throws IOException 
     */
    String transform(byte[] htmlData);
}
