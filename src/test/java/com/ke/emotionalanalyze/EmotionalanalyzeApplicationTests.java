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
    public void test1(){

    }

    @Test
    public void removeRepeat(){
        commentsSevice.deleteRepeatComments("Java编程思想（第4版） [thinking in java]");
    }

	@Test
	public void contextLoads() throws Exception{

        //停用词过滤,冗余词替换
        List<Comments> comments = commentsSevice.redundantReplace(commentsSevice.Character("Java编程思想（第4版） [thinking in java]"));


        ArrayList<String> contents = new ArrayList<>();

        for (int i=0;i<400;i++) {
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


        System.out.println("=关联规则==========");
        Map<String, Double> relationRulesMap = apriori2.getRelationRules(frequentSetMap);
        Set<String> rrKeySet = relationRulesMap.keySet();
        for (String rrKey : rrKeySet)
        {
            System.out.println(rrKey + "  :  " + relationRulesMap.get(rrKey));
        }
        Long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-begin)+"毫秒");

    }

}
