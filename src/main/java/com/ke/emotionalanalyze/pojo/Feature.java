package com.ke.emotionalanalyze.pojo;

import java.util.Comparator;

public class Feature implements Comparator{

    private String word;

    private Integer count;

    public Feature(){
        super();
    }

    public Feature(String word,Integer count){
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public int compare(Object o1, Object o2) {
        return ((Feature)o2).getCount()-((Feature)o1).getCount();
    }
}
