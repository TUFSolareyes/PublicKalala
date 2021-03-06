package com.letmesee.www.pojo;

import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Map;


/**
 * 正排索引实体类
 */
public class ForwardPacking {

    @MongoId
    private String _id;

    private Map<String,WordPacking> map;

    public ForwardPacking() {
    }

    public ForwardPacking(String tid, Map<String, WordPacking> map) {
        this._id = tid;
        this.map = map;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Map<String, WordPacking> getMap() {
        return map;
    }

    public void setMap(Map<String, WordPacking> map) {
        this.map = map;
    }
}
