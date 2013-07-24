package com.easou.news.crawl.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
public class CassandraServiceTest {
	@Autowired
	private ICassandraService cassandraService;
	
//	@Test
	public void saveImageTest(){
		try {
			String key = "test";
			byte[] image = FileUtils.readFileToByteArray(new File("f:\\3.jpg"));
			Assert.assertTrue(cassandraService.saveImage(key, image));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getImageTest(){
		try {
			String key = "074e2788a0bc4bbf21e5eb3b24144f2b";
			byte[] image = cassandraService.getImage(key);
			System.out.println(image);
			FileUtils.writeByteArrayToFile(new File("f:\\1.jpg"), image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
