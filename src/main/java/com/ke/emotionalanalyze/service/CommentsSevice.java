package com.ke.emotionalanalyze.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ke.emotionalanalyze.dao.CommentsDao;
import com.ke.emotionalanalyze.dao.LibraryDao;
import com.ke.emotionalanalyze.pojo.BookMessage;
import com.ke.emotionalanalyze.pojo.Comments;
import com.ke.emotionalanalyze.pojo.Feature;
import com.ke.emotionalanalyze.pojo.Result;
import com.ke.emotionalanalyze.util.Apriori2;
import com.ke.emotionalanalyze.util.HttpUtils;
import com.ke.emotionalanalyze.util.ProcessUtils;
import com.mongodb.client.result.DeleteResult;
import com.sun.org.apache.regexp.internal.RE;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommentsSevice {

    @Autowired
    private CommentsDao commentsDao;

    @Autowired
    private LibraryDao libraryDao;

    private String origin = "JD";

    private boolean endFlag = false;

    public BookMessage crawlCommentsByUrl(String origin,String url){
        //根据url获取书本名称，作者，bookID,
        BookMessage bookMessage;
        try {
            bookMessage = getBookMsgByUrlFromJD(url);
            if(bookMessage==null)
                return null;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        //判断数据库library表是否已经存在改书本评论信息
        if(libraryDao.findOneByName(bookMessage.getBookName())!=null)
            return null;
        System.out.println(bookMessage);
        //将该书本添加到采集队列
        ProcessUtils.crawlQueue.add(bookMessage);
        //根据bookid爬取评论,爬取完成后在library表新增书本信息
        //insertAllComments(bookMessage);

        return bookMessage;
    }

    private BookMessage getBookMsgByUrlFromJD(String url)throws IOException{
        //提交GET请求
        HttpEntity httpEntity = HttpUtils.httpGet(url);
        //获得html文档
        String html = EntityUtils.toString(httpEntity,"UTF-8");
        //解析网页 得到文档对象
        Document doc= Jsoup.parse(html);
        //DOM选择器
        Elements element = doc.select("#name");
        //新建书本信息对象
        BookMessage bookMessage = new BookMessage();
        //操作DOM获取书名
        bookMessage.setBookName(element.select("h1").text());
        //获取作者
        bookMessage.setAuthor(element.select("#p-author").text());
        //获取bookId
        Pattern comments = Pattern.compile("https://item.jd.com/(.+?).html");
        Matcher m1 = comments.matcher(url);
        while(m1.find()) {
            bookMessage.setBookId(m1.group(1));
        }
        //初始化采集状态
        bookMessage.setCrawlOver(false);
        return bookMessage;
    }


    //采集所有评论信息
    @Async
    public void insertAllComments(BookMessage bookMessage){
        String bookId = bookMessage.getBookId();
        System.out.println("import comments begin------------------------------------------");
        for(int i=0;i<100;i++){
            if(endFlag){
                System.out.println("共"+i+"页，已完成");
                break;
            }
            System.out.println("第"+i+"页开始");
            String url = "https://sclub.jd.com/comment/productPageComments.action?productId="+bookId+"&score=0&sortType=1&page="+i+"&pageSize=10";
            insertComment(url,bookMessage.getBookName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("第"+i+"页结束");
        }
        System.out.println("import comments finish------------------------------------------");
        libraryDao.insert(bookMessage);
        ProcessUtils.alterStateByBookName(bookMessage.getBookName(),true);
    }
    //获取1页评论信息
    public void insertComment(String url,String bookName){

        HttpEntity httpEntity = HttpUtils.httpGet(url);
        String htmlString = HttpUtils.getHtml(httpEntity);
        //将json字符串转换为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(htmlString);
        //获取评论JSON数组
        JSONArray jsonArray = jsonObject.getJSONArray("comments");
        if(jsonArray.size()==0){
            System.out.println("jsonArray is null");
            endFlag = true;
            return;
        }
        //评论列表
        ArrayList<Comments> commentsList = new ArrayList<>();
        //遍历数组获得score productColor creationTime content userLevelName referenceName字段的值
        for(int i=0;i<jsonArray.size();i++){
            JSONObject j = jsonArray.getJSONObject(i);
            //评论对象
            Comments comment = new Comments();
            //书名
            comment.setBookName(bookName);
            //星级
            comment.setStat(j.getString("score"));
            //评论时间
            comment.setTime(j.getString("creationTime"));
            //评论内容
            comment.setContent(j.getString("content"));
            //评论来源（京东or亚马逊）
            comment.setOrigin(origin);
            //加入评论列表
            commentsDao.insert(comment);
        }
    }

    /**
     *根据书名从数据库获取所有评论
     */
    public List<Comments> getCommentsByBookName(String bookName){
        return commentsDao.getCommentsByName(bookName);
    }



    /**
     *删除评论
     */
    public Result deleteComment(String id){
        DeleteResult deleteResult = commentsDao.deleteComment(id);
        if(deleteResult.getDeletedCount()==1){
            return new Result("ok");
        }else{
            return new Result("删除评论失败！");
        }
    }

    /**
     * 删除重复评论
     */
    public Integer deleteRepeatComments(String bookName){
        //获取该书评论
        System.out.println("清洗书名"+bookName);
        List<Comments> comments = getCommentsByBookName(bookName);
        HashMap<Integer,Comments> commentMap = new HashMap<>();
        List<String> ids = new ArrayList<>();
        //将评论内容进行哈希计算
        for (Comments c:comments) {
            String content = c.getContent();
            //如果map里有相同的hashcode则进行字符串对比，相同则放如待删除列表
            if(commentMap.containsKey(content.hashCode())){
                if(c.getContent().equals(commentMap.get(content.hashCode()).getContent())){
                    ids.add(c.getId());
                }
            }else{
                commentMap.put(content.hashCode(),c);
            }
        }
        //删除
        System.out.println("重复数据"+ids.size());
        for (String id:ids) {
            commentsDao.deleteComment(id);
        }
        //返回删除条数
        return ids.size();
    }


    /**
     * 分词和词性标注
     */
    public List<Comments> mark(String bookName){
        //获取书本所有评论
        List<Comments> comments = commentsDao.getCommentsByName(bookName);
        //分词
        for (Comments c:comments) {
            org.ansj.domain.Result result = ToAnalysis.parse(c.getContent());
            c.setContent(result.getTerms().toString());
        }
        return comments;
    }

    /**
     * 去停用词
     */
    public List<Comments> stopWork(String bookName){
        //获取书本所有评论
        List<Comments> comments = commentsDao.getCommentsByName(bookName);
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
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("停用词表读取失败");
        }
        //分词
        for (Comments c:comments) {
            org.ansj.domain.Result result = ToAnalysis.parse(c.getContent());
            List<Term> terms = result.getTerms();
            //遍历停用词表进行对比
            for(int i=0;i<terms.size();i++){
                for (int j=0;j<stopWordTable.size();j++){
                    if(terms.get(i).getName().equals(stopWordTable.get(j))){
                        System.out.println("停用词："+terms.get(i).getName());
                        terms.remove(i);
                        break;
                    }
                }
            }
            c.setContent(terms.toString());
        }
        return comments;
    }

    /**
     * 去停用词
     */
    public List<Comments> Character(String bookName){
        //获取书本所有评论
        List<Comments> comments = commentsDao.getCommentsByName(bookName);
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

        //分词
        for (Comments c:comments) {
            org.ansj.domain.Result result = ToAnalysis.parse(c.getContent());
            List<Term> terms = result.getTerms();
            //遍历停用词表进行对比
            for(int i=0;i<terms.size();i++){
                if(terms.get(i).getName().equals(" ")){
                    terms.remove(i);
                    i--;
                    continue;
                }
                for (int j=0;j<stopWordTable.size();j++){
                    if(terms.get(i).getName().equals(stopWordTable.get(j))){
                        terms.remove(i);
                        i--;
                        break;
                    }
                }
            }
            for(int k=0;k<terms.size();k++){
                String natureStr = terms.get(k).getNatureStr();
                if(!(natureStr.equals("n"))){
                    terms.remove(k);
                    k--;
                }
            }
            StringBuilder sb = new StringBuilder();
            for(int k=0;k<terms.size();k++){
                sb.append(terms.get(k).getName()+" ");
            }
            if(sb==null || sb.toString().equals("")){
                c.setContent("");
            }else{
                c.setContent(sb.substring(0,sb.length()-1));
            }

        }
        for(int i=0;i<comments.size();i++){
            String content = comments.get(i).getContent();
            if(content.equals("") || content==null){
                comments.remove(i);
                i--;
            }
        }
        return comments;
    }

    /**
     * 冗余词替换
     */
    public List<Comments> redundantReplace(String bookName){
        //获取所有评论
        List<Comments> comments = getCommentsByBookName(bookName);
        return redundantReplace(comments);
    }

    /**
     * 冗余词替换 重载1
     */
    public List<Comments> redundantReplace(List<Comments> comments){
        //获取冗余词表
        HashMap<String,String> map = ProcessUtils.redundantTable;
        //遍历评论
        for (Comments c:comments) {
            //遍历冗余词表
            Iterator iter = ProcessUtils.redundantTable.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String)entry.getKey();
                //冗余词替换
                if(c.getContent().contains(key)){
                    String value = (String)entry.getValue();
                    String content = c.getContent().replace(key,value);
                    c.setContent(content);
                }
            }
        }
        return comments;
    }

    /**
     * 特征词提取
     */
    public List<Feature> getFeatureWords(String bookName){

        //去重，去停用词后的评论
        List<Comments> c1 = Character(bookName);
        //去冗余词后的评论
        List<Comments> c2 = redundantReplace(c1);
        System.out.println("共"+c2.size()+"条数据");
        //特征提取
        ArrayList<String> contents = new ArrayList<>();

        for (int i=0;i<c2.size();i++) {
            contents.add(c2.get(i).getContent());
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
        //排序
        features.sort(new Feature());
        return features;
    }

    /**
     * 情感分析
     */
    public List<Feature> sentimentAnalysis(List<Feature> features){
        //读入情感字典
        List<String> positive = new ArrayList<>();
        List<String> negative = new ArrayList<>();

        try{
            Resource resource = new ClassPathResource("static/txt/ntusd-positive.txt");
            BufferedReader br1 = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String line = "";
            line = br1.readLine();
            while (line != null) {
                positive.add(line);
                line = br1.readLine();
            }
            br1.close();

            Resource resource2 = new ClassPathResource("static/txt/ntusd-negative.txt");
            BufferedReader br2 = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String line2 = "";
            line = br2.readLine();
            while (line != null) {
                negative.add(line);
                line = br2.readLine();
            }
            br2.close();

        }catch (IOException e){
            e.printStackTrace();
            System.out.println("停用词表读取失败");
        }

        //遍历特征词与情感词典。

    }




}
