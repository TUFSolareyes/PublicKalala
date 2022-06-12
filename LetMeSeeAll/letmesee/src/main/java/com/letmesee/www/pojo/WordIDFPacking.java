package com.letmesee.www.pojo;


/**
 * 用于单词信息打分的实体类
 */
public class WordIDFPacking {

    private String word;

    private int len;

    public WordIDFPacking(String word, int len) {
        this.word = word;
        this.len = len;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }
}
