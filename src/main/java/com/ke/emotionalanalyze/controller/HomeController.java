package com.ke.emotionalanalyze.controller;


import com.ke.emotionalanalyze.dao.CommentsDao;
import com.ke.emotionalanalyze.pojo.Comments;
import com.ke.emotionalanalyze.service.CommentsSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @Autowired
    private CommentsSevice commentsSevice;

    @Autowired
    private CommentsDao commentsDao;

    @RequestMapping(value={"/","/home"})
    public String home(){
        return "html/home.html";
    }

}
