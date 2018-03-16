package com.ke.emotionalanalyze.util;

import com.ke.emotionalanalyze.pojo.BookMessage;

import java.util.ArrayList;
import java.util.List;

public class ProcessUtils {

    /**
     * 采集队列
     */
    public static List<BookMessage> crawlQueue = new ArrayList<>();

    /**
     *修改书本采集状态
     */
    public static BookMessage alterStateByBookName(String bookName,Boolean CrawlOver){
        BookMessage b = new BookMessage();
        for(int i = 0;i<crawlQueue.size();i++){
            b = crawlQueue.get(i);
            if(b.getBookName().equals(bookName)){
                crawlQueue.get(i).setCrawlOver(CrawlOver);
            }
        }
        return b;
    }

    /**
     *根据书名将消息队列中的书本删除
     */
    public static BookMessage removeByBookName(String bookName){
        BookMessage b = new BookMessage();
        for(int i = 0;i<crawlQueue.size();i++){
            b = crawlQueue.get(i);
            if(b.getBookName().equals(bookName)){
                crawlQueue.remove(i);
            }
        }
        return b;
    }

    /**
     *根据书本ID删除
     */
    public static BookMessage removeByBookId(String bookId){
        BookMessage b = new BookMessage();
        for(int i = 0;i<crawlQueue.size();i++){
            b = crawlQueue.get(i);
            if(b.getBookId().equals(bookId)){
                crawlQueue.remove(i);
            }
        }
        return b;
    }



}
