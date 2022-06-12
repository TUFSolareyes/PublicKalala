package com.letmesee.www.pojo;

import org.springframework.data.mongodb.core.mapping.MongoId;

public class TextPacking {

    @MongoId
    private String _id;

    private String t;

    private String c;

    public TextPacking() {
    }

    public TextPacking(String _id, String t, String c) {
        this._id = _id;
        this.t = t;
        this.c = c;
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

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }
}
