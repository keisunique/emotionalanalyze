package com.ke.emotionalanalyze.dao;

import com.ke.emotionalanalyze.pojo.Comments;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class CommentsDao {

    @Autowired
    private MongoTemplate template;

    public void insert(Comments comments){
        template.insert(comments);
    }

    public void insertList(ArrayList<Comments> commentsList){
        template.insert(commentsList);
    }

    /**
     *根据书名获取所有评论
     */
    public List<Comments> getCommentsByName(String bookName){
        Query query = new Query(Criteria.where("bookName").is(bookName));
        return  template.find(query,Comments.class);
    }

    /**
     *删除一条评论
     */
    public DeleteResult deleteComment(String id){
        Query query = new Query(Criteria.where("_id").is(id));
        return template.remove(query,Comments.class);
    }
}
