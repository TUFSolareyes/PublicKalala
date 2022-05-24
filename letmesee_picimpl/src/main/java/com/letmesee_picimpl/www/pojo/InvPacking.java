package com.letmesee_picimpl.www.pojo;

import java.util.Collections;
import java.util.List;

public class InvPacking {

    private String _id;

    private List<Long> tids;

    public InvPacking() {
    }

    public InvPacking(String _id, List<Long> pids) {
        this._id = _id;
        this.tids = Collections.synchronizedList(pids);
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
