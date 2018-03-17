package com.ke.emotionalanalyze;

import com.ke.emotionalanalyze.dao.CommentsDao;
import com.ke.emotionalanalyze.pojo.Comments;
import com.ke.emotionalanalyze.service.CommentsSevice;
import com.ke.emotionalanalyze.util.Apriori2;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmotionalanalyzeApplicationTests {

	@Autowired
	private CommentsDao commentsDao;
    @Autowired
    private CommentsSevice commentsSevice;

	@Test
	public void contextLoads() throws Exception{

        //停用词过滤
        List<Comments> comments = commentsSevice.Character("冰波王一梅童话故事书全20册任选 美丽+快乐童年微童话 彩图注音 3-6-9岁带拼音绘本 快乐童年微童话10册");

        ArrayList<String> contents = new ArrayList<>();

        for (int i=0;i<500;i++) {
            contents.add(comments.get(i).getContent());
            System.out.println(comments.get(i).getContent());
        }

        Apriori2 apriori2 = new Apriori2();

        System.out.println("=频繁项集==========");
        Long begin = System.currentTimeMillis();
        Map<String, Integer> frequentSetMap = apriori2.apriori(contents);
        Set<String> keySet = frequentSetMap.keySet();
        for(String key:keySet)
        {
            System.out.println(key+" : "+frequentSetMap.get(key));
        }
        Long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-begin)+"毫秒");

    }

}
