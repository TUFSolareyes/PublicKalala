package com.letmesee.www.pojo;

import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;

public class SearchLog {

    @MongoId
    private String _id;

    private Long timeStamp;

    private ArrayList<Long> tids;

    private String csid;

    public SearchLog() {
    }

    public SearchLog(String searchText, Long timeStamp, ArrayList<Long> tids, String csid) {
        this._id = searchText;
        this.timeStamp = timeStamp;
        this.tids = tids;
        this.csid = csid;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ArrayList<Long> getTids() {
        return tids;
    }

    public void setTids(ArrayList<Long> tids) {
        this.tids = tids;
    }

    public String getCsid() {
        return csid;
    }

    public void setCsid(String csid) {
        this.csid = csid;
    }
}
