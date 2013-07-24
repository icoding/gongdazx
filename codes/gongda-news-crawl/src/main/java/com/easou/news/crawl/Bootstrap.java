package com.easou.news.crawl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.easou.news.crawl.factory.TemplateFactory;

public class Bootstrap {
	private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	private AbstractApplicationContext ac;
	private ServerSocket serverSocket;
	private int port = 10811;

	public void start() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException("bind port error.", e);
		}
		ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		ac.registerShutdownHook();
	}

	public void waitForStop() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				socket.setSoTimeout(6000);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String command = reader.readLine();
				if (command == null) {
					continue;
				}
				if ("shutdown".equalsIgnoreCase(command)) {
					break;
				} else if (command.startsWith("load")) {
					int id= Integer.parseInt(command.split(" ")[1]);
					TemplateFactory factory = (TemplateFactory)ac.getBean("templateFactory");
					logger.info("begin to load template:" + id);
					factory.loadTemplateById(id);
					OutputStreamWriter ow = new OutputStreamWriter(socket.getOutputStream());
					ow.write("complate load template:" + id);
					ow.flush();
					continue;
				}
			} catch (SocketTimeoutException e) {
				logger.error("",e);
			} catch (IOException e) {
				logger.error("",e);
			} catch (Exception e) {
				logger.error("",e);		
			} finally{
				if(socket != null){
					try {
						socket.close();
					} catch (IOException e) {
						logger.error("",e);
					}
				}
			}

		}
	}

	public void stop() {
		ac.close();
		try {
			serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("close port error.", e);
		}
	}
	
	public static void main(String[] args) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.start();
		bootstrap.waitForStop();
		bootstrap.stop();
	}
}