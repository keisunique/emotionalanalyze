package com.ke.emotionalanalyze.service;

import com.ke.emotionalanalyze.dao.LibraryDao;
import com.ke.emotionalanalyze.pojo.BookMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private LibraryDao libraryDao;

    /**
     *从书库获取所有书本
     */
    public List<BookMessage> getBookListInDb(){
        return libraryDao.getBookList();
    }
}
