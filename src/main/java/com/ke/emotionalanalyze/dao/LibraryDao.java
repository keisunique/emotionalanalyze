package com.ke.emotionalanalyze.dao;

import com.ke.emotionalanalyze.pojo.BookMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LibraryDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     *插入一本图书信息
     */
    public void insert(BookMessage bookMessage){
        mongoTemplate.insert(bookMessage);
    }

    /**
     *根据书名查找书库是否存在此书
     */
    public BookMessage findOneByName(String bookName){
        Query query = new Query(Criteria.where("bookName").is(bookName));
        return mongoTemplate.findOne(query, BookMessage.class);
    }

    /**
     *获取书库所有的书籍
     */
    public List<BookMessage> getBookList(){
        return mongoTemplate.findAll(BookMessage.class);
    }


}
