package com.ke.emotionalanalyze;

import com.ke.emotionalanalyze.dao.CommentsDao;
import com.ke.emotionalanalyze.pojo.Comments;
import com.ke.emotionalanalyze.pojo.Feature;
import com.ke.emotionalanalyze.service.CommentsSevice;
import com.ke.emotionalanalyze.util.Apriori2;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.Counter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
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
        String s1 = "书本的印刷质量很好，内容讲解不错，总体很满意";
        System.out.println(ToAnalysis.parse(s1));
        //获取停用词表ai
        List<String> stopWordTable = new ArrayList<>();
        try{
            Resource resource = new ClassPathResource("static/txt/StopWordTable.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String line = "";
            line = br.readLine();
            while (line != null) {
                stopWordTable.add(line);
                line = br.readLine();
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("停用词表读取失败");
        }
        org.ansj.domain.Result result  = ToAnalysis.parse(s1);
        List<Term> terms = result.getTerms();
        for (Term term:terms) {
            for (int j=0;j<stopWordTable.size();j++){
                if(term.getName().equals(stopWordTable.get(j))){
                    System.out.println(term.getName());
                    break;
                }
            }
        }

    }

    @Test
    public void removeRepeat(){
        commentsSevice.deleteRepeatComments("原则 [Principles]");
    }

	@Test
	public void contextLoads() throws Exception{

        //停用词过滤,冗余词替换
        List<Comments> comments = commentsSevice.redundantReplace(commentsSevice.Character("原则 [Principles]"));


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
        List<Feature> features = new ArrayList<>();
        for(String key:keySet)
        {
            features.add(new Feature(key,frequentSetMap.get(key)));
        }
        features.sort(new Feature());
        for (Feature f:features) {
            System.out.println(f.getWord()+" : "+f.getCount());
        }

/*        System.out.println("=关联规则==========");
        Map<String, Double> relationRulesMap = apriori2.getRelationRules(frequentSetMap);
        Set<String> rrKeySet = relationRulesMap.keySet();
        for (String rrKey : rrKeySet)
        {
            System.out.println(rrKey + "  :  " + relationRulesMap.get(rrKey));
        }
        Long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-begin)+"毫秒");*/

    }

}
