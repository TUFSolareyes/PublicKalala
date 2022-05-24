package com.letmesee.www.pojo.user;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;

public class StarCollectionPacking {

    @MongoId
    private String _id;

    @Indexed
    private String uid;

    private String cname;

    private Long timestamp;

    private ArrayList<Long> tids;

    public StarCollectionPacking() {
    }

    public StarCollectionPacking(String _id, String uid, String cname, Long timestamp, ArrayList<Long> tids) {
        this._id = _id;
        this.uid = uid;
        this.cname = cname;
        this.timestamp = timestamp;
        this.tids = tids;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<Long> getTids() {
        return tids;
    }

    public void setTids(ArrayList<Long> tids) {
        this.tids = tids;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }
}
