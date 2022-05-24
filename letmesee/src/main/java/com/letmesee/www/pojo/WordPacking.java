package com.letmesee.www.pojo;

import java.util.List;

/**
 * 关键词包装类
 */
public class WordPacking {

    /**
     * 关键词
     */
    private String k;

    /**
     * 标题出现的次数
     */
    private int tt;

    /**
     * 内容出现的次数
     */
    private int ct;

    /**
     * TF
     */
    private double tf;

    /**
     * 出现的位置
     */
    private List<Integer> p;

    public WordPacking() {
    }

    public WordPacking(String k, int tt, int ct, double tf, List<Integer> p) {
        this.k = k;
        this.tt = tt;
        this.ct = ct;
        this.tf = tf;
        this.p = p;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public int getTt() {
        return tt;
    }

    public void setTt(int tt) {
        this.tt = tt;
    }

    public int getCt() {
        return ct;
    }

    public void setCt(int ct) {
        this.ct = ct;
    }

    public double getTf() {
        return tf;
    }

    public void setTf(double tf) {
        this.tf = tf;
    }

    public List<Integer> getP() {
        return p;
    }

    public void setP(List<Integer> p) {
        this.p = p;
    }
}
