package com.easou.news.crawl.factory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("transformFactory")
public class TransformFactory {
    private static Logger logger = LoggerFactory.getLogger(TransformFactory.class);
    private String transFile = "/transform.properties";

    private String host;
    private int port;
    private int timeoutInMs;
    private static TransformFactory instance;

    @PostConstruct
    public void init() {
        try {
            Properties pro = getProperties(transFile);
            host = pro.getProperty("tranform.host");
            port = Integer.parseInt(pro.getProperty("tranform.port"));
            timeoutInMs = Integer.parseInt(pro.getProperty("tranform.timeoutInMs"));
        } catch (Exception e) {
            logger.error("TransformFactory init error !");
            e.printStackTrace();
        }
    }
    
    public TransformFactory() {
        init();
    }

    public static synchronized TransformFactory getInstance() {
        if (null == instance) {
            instance = new TransformFactory();
        }
        return instance;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getTimeoutInMs() {
        return timeoutInMs;
    }

    public Socket getSocket() {
        Socket socket = new Socket();
        try {
            socket.setSoLinger(false, 0);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(timeoutInMs);
            socket.connect(new InetSocketAddress(host, port), timeoutInMs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    private Properties getProperties(String configFile) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(configFile);
        Properties pro = new Properties();
        pro.load(is);
        return pro;
    }
}
