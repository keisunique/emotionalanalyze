package com.ke.emotionalanalyze;

import com.ke.emotionalanalyze.dao.CommentsDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmotionalanalyzeApplicationTests {

	@Autowired
	private CommentsDao commentsDao;

	@Test
	public void contextLoads() throws Exception{
        Resource resource = new ClassPathResource("static/txt/StopWordTable.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        String line = "";
        line = br.readLine();
        while (line != null) {
            line = br.readLine();
            System.out.println(line);
        }
    }

}
