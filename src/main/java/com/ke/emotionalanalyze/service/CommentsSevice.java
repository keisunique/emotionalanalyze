package com.ke.emotionalanalyze.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ke.emotionalanalyze.dao.CommentsDao;
import com.ke.emotionalanalyze.dao.LibraryDao;
import com.ke.emotionalanalyze.pojo.BookMessage;
import com.ke.emotionalanalyze.pojo.Comments;
import com.ke.emotionalanalyze.pojo.Result;
import com.ke.emotionalanalyze.util.HttpUtils;
import com.ke.emotionalanalyze.util.ProcessUtils;
import com.mongodb.client.result.DeleteResult;
import com.sun.org.apache.regexp.internal.RE;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
     *从书库获取所有书本
     */
    public List<BookMessage> getBookListInDb(){
        return libraryDao.getBookList();
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

}
