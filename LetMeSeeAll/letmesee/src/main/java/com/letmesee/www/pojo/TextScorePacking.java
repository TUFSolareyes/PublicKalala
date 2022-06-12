package com.letmesee.www.pojo;


/**
 * 文章打分实体类
 */
public class TextScorePacking {

    private String tid;

    private double score;

    public TextScorePacking() {
    }

    public TextScorePacking(String tid, double score) {
        this.tid = tid;
        this.score = score;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
