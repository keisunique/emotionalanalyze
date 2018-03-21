package com.ke.emotionalanalyze.service;

import com.ke.emotionalanalyze.dao.CommentsDao;
import com.ke.emotionalanalyze.dao.LibraryDao;
import com.ke.emotionalanalyze.pojo.BookMessage;
import com.ke.emotionalanalyze.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private LibraryDao libraryDao;
    @Autowired
    private CommentsDao commentsDao;

    /**
     *从书库获取所有书本
     */
    public List<BookMessage> getBookListInDb(){
        return libraryDao.getBookList();
    }

    /**
     * 从书库删除书本
     */
    public Result deleteBook(String id){

        String bookName = libraryDao.getBookNameById(id);
        System.out.println("bookservice:"+bookName);
        if(bookName==null)
            return new Result("error");
        if(!commentsDao.deleteComments(bookName).wasAcknowledged()){
            return new Result("error");
        }
        if(!libraryDao.deleteBook(id).wasAcknowledged()){
            return new Result("error");
        }
        return new Result("ok");
    }
}
