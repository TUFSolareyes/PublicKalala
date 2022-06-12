package com.letmesee.www.pojo.user;

import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

public class StarPacking {

    @MongoId
    private String _id;

    private List<String> tids;

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

    public StarPacking(String cname, List<String> tids) {
        this._id = cname;
        this.tids = tids;
    }

    public StarPacking() {
    }
}
