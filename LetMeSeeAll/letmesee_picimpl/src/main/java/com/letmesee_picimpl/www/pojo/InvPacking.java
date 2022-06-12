package com.letmesee_picimpl.www.pojo;

import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Collections;
import java.util.List;

public class InvPacking {

    @MongoId
    private String _id;

    private List<String> tids;

    public InvPacking() {
    }

    public InvPacking(String _id, List<String> pids) {
        this._id = _id;
        this.tids = pids;
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
