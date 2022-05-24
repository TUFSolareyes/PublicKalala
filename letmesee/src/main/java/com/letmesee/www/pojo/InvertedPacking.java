package com.letmesee.www.pojo;

import java.util.Collections;
import java.util.List;

public class InvertedPacking {

    private String _id;

    private List<Long> tids;

    public InvertedPacking() {
    }

    public InvertedPacking(String _id, List<Long> tids) {
        this._id = _id;
        this.tids = Collections.synchronizedList(tids);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<Long> getTids() {
        return tids;
    }

    public void setTids(List<Long> tids) {
        this.tids = tids;
    }
}
