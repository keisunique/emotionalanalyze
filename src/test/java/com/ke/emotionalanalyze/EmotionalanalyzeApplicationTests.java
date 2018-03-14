package com.ke.emotionalanalyze;

import com.ke.emotionalanalyze.dao.CommentsDao;
import com.ke.emotionalanalyze.pojo.BookMessage;
import com.ke.emotionalanalyze.util.ProcessUtils;
import org.apache.tomcat.jni.Proc;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmotionalanalyzeApplicationTests {

	@Autowired
	private CommentsDao commentsDao;

	@Test
	public void contextLoads() {
		System.out.println(commentsDao.getCommentsByName("认识电影（插图第12版） [Understanding Movies, 12e]"));
	}

}
