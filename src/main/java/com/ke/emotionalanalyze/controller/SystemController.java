package com.ke.emotionalanalyze.controller;

import com.ke.emotionalanalyze.pojo.Result;
import com.ke.emotionalanalyze.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SystemController {

    @Autowired
    private BookService bookService;

    @RequestMapping("/library")
    public String library(){
        return "html/system/library.html";
    }

    @ResponseBody
    @RequestMapping("/deletebook/{id}")
    public Result deleteBook(@PathVariable("id")String id){
        return bookService.deleteBook(id);
    }




}
