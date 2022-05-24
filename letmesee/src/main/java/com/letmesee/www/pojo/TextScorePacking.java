package com.letmesee.www.pojo;

public class TextScorePacking {

    private long tid;

    private double score;

    public TextScorePacking() {
    }

    public TextScorePacking(long tid, double score) {
        this.tid = tid;
        this.score = score;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
