package com.letmesee.www.pojo;

import org.springframework.data.mongodb.core.mapping.MongoId;


/**
 * 文章摘要实体类
 */
public class TextInfoPacking {

    @MongoId
    private String _id;

    private String t;

    private String su;

    private String djs;

    public TextInfoPacking() {
    }

    public TextInfoPacking(String _id, String t, String su, String djs) {
        this._id = _id;
        this.t = t;
        this.su = su;
        this.djs = djs;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getSu() {
        return su;
    }

    public void setSu(String su) {
        this.su = su;
    }

    public String getDjs() {
        return djs;
    }

    public void setDjs(String djs) {
        this.djs = djs;
    }
}
