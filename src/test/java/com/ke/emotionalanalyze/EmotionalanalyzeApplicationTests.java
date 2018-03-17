package com.ke.emotionalanalyze;

import com.ke.emotionalanalyze.dao.CommentsDao;
import com.ke.emotionalanalyze.pojo.Comments;
import com.ke.emotionalanalyze.service.CommentsSevice;
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
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmotionalanalyzeApplicationTests {

	@Autowired
	private CommentsDao commentsDao;
    @Autowired
    private CommentsSevice commentsSevice;

	@Test
	public void contextLoads() throws Exception{
        List<Comments> comments =  commentsSevice.getCommentsByBookName("橘子不是唯一的水果");
    }

}
