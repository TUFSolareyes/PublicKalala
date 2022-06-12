package com.letmesee.www.pojo;

import java.util.List;

/**
 * 倒排索引实体类
 */
public class InvertedPacking {

    private String _id;

    private List<String> tids;

    public InvertedPacking() {
    }

    public InvertedPacking(String _id, List<String> tids) {
        this._id = _id;
        this.tids = tids;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<String> getTids() {
        return tids;
    }

    public void setTids(List<String> tids) {
        this.tids = tids;
    }
}
