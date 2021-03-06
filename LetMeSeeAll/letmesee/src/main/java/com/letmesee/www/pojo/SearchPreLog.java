package com.letmesee.www.pojo;

import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;

/**
 * 搜索日志实体类
 */
public class SearchPreLog {

    @MongoId
    private String _id;

    private Long timeStamp;

    private ArrayList<String> tids;

    private String csid;

    public SearchPreLog() {
    }

    public SearchPreLog(String searchText, Long timeStamp, ArrayList<String> tids, String csid) {
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

    public ArrayList<String> getTids() {
        return tids;
    }

    public void setTids(ArrayList<String> tids) {
        this.tids = tids;
    }

    public String getCsid() {
        return csid;
    }

    public void setCsid(String csid) {
        this.csid = csid;
    }
}
