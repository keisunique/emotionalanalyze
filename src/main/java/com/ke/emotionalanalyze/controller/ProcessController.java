package com.ke.emotionalanalyze.controller;

import com.ke.emotionalanalyze.pojo.BookMessage;
import com.ke.emotionalanalyze.pojo.Comments;
import com.ke.emotionalanalyze.pojo.Result;
import com.ke.emotionalanalyze.service.BookService;
import com.ke.emotionalanalyze.service.CommentsSevice;
import com.ke.emotionalanalyze.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 数据处理控制器（爬取数据，数据清洗，分词及词性标注，冗余数据替换）
 */
@Controller
public class ProcessController {

    @Autowired
    private CommentsSevice commentsSevice;
    @Autowired
    private BookService bookService;

    /**
     * 爬取数据页面
     */
    @RequestMapping("/crawl")
    public String crawl(){
        return "html/process/crawl.html";
    }

    /**
     *根据书本链接爬取数据
     */
    @ResponseBody
    @RequestMapping(value = "/crawlbyurl")
    public BookMessage crawlByUrl(HttpServletRequest request){
        String origin = request.getParameter("origin");
        String url = request.getParameter("book");
        BookMessage bookMessage = commentsSevice.crawlCommentsByUrl(origin,url);
        commentsSevice.insertAllComments(bookMessage);
        return bookMessage;
    }

    /**
     *获取采集队列
     */
    @ResponseBody
    @RequestMapping("/getcrawlqueue")
    public List<BookMessage> getCrwalQueue(){
        return ProcessUtils.crawlQueue;
    }

    /**
     *删除采集队列中的一本书
     */
    @ResponseBody
    @RequestMapping("/removebookinqueue/{bookId}")
    public BookMessage removeBookInCrawlQueue(@PathVariable("bookId")String bookId){
        return ProcessUtils.removeByBookId(bookId);
    }

    /**
     *根据书名从数据库获取所有评论
     */
    @ResponseBody
    @RequestMapping(value = "/getcomments/{bookName}")
    public List<Comments> getCommentsByBookName(@PathVariable("bookName")String bookName){
        return commentsSevice.getCommentsByBookName(bookName);
    }


    /**
     *删除一条评论
     */
    @ResponseBody
    @RequestMapping(value = "/deletecomment/{id}")
    public Result deleteComment(@PathVariable("id")String id){
        return commentsSevice.deleteComment(id);
    }

    //--------数据清洗部分----------------------------

    @RequestMapping(value = "/dataclean")
    public String dataClean(){
        return "html/process/dataclean.html";
    }

    /**
     *从书库获取所有书
     */
    @ResponseBody
    @RequestMapping("/getbooklist")
    public List<BookMessage> getBooklistInDb(){
        return bookService.getBookListInDb();
    }

    /**
     * 去重
     */
    @ResponseBody
    @RequestMapping("/deleterepeat/{bookName}")
    public Result deleteRepeatComments(@PathVariable("bookName")String bookName){
       return new Result(commentsSevice.deleteRepeatComments(bookName).toString());
    }


    //分词---------------------------------------

    /**
     * 分词页
     */
    @RequestMapping("/decomposition")
    public String decomposition(){
        return "html/process/decomposition.html";
    }

    /**
     * 分词和词性标注
     */
    @ResponseBody
    @RequestMapping("/mark/{bookName}")
    public List<Comments> mark(@PathVariable("bookName")String bookName){
        return commentsSevice.mark(bookName);
    }

}
