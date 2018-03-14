package com.ke.emotionalanalyze.pojo;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="library")
public class BookMessage {

    @Id
    private String id;

    private String bookName;

    private String bookId;

    private String author;

    //采集状态，是否完成
    private Boolean crawlOver;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean getCrawlOver() {
        return crawlOver;
    }

    public void setCrawlOver(Boolean crawlOver) {
        this.crawlOver = crawlOver;
    }

    @Override
    public String toString() {
        return "BookMessage{" +
                "id='" + id + '\'' +
                ", bookName='" + bookName + '\'' +
                ", bookId='" + bookId + '\'' +
                ", author='" + author + '\'' +
                ", crawlOver=" + crawlOver +
                '}';
    }
}
