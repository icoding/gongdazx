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
import com.easou.news.crawl.frontier.SeedManager;

public class BootstrapSeed {
	private static Logger logger = LoggerFactory.getLogger(BootstrapSeed.class);
	private AbstractApplicationContext ac;
	private ServerSocket serverSocket;
	private int port = 10000;

	public void start() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException("bind port error.", e);
		}
		ac = new ClassPathXmlApplicationContext("applicationContext-seed.xml");
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
					SeedManager manager = (SeedManager)ac.getBean("seedManager");
					logger.info("socket begin to load seed:" + id);
					manager.loadSeedById(id);
					OutputStreamWriter ow = new OutputStreamWriter(socket.getOutputStream());
					ow.write("load seed complate:" + id);
					ow.flush();
					continue;
				} else if (command.equalsIgnoreCase("reload")) {
					SeedManager manager = (SeedManager)ac.getBean("seedManager");
					logger.info("socket begin to reload seed");
					manager.reloadAllSeed();
					OutputStreamWriter ow = new OutputStreamWriter(socket.getOutputStream());
					ow.write("reload seed complate:");
					ow.flush();
					continue;
				}
			} catch (SocketTimeoutException e) {
				logger.error("",e);
			} catch (IOException e) {
				logger.error("",e);
				break;
			} catch (Exception e) {
				logger.error("Exception:",e);
				break;				
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
		BootstrapSeed bootstrap = new BootstrapSeed();
		bootstrap.start();
		bootstrap.waitForStop();
		bootstrap.stop();
	}
}