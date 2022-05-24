package com.letmesee.www.pojo;

import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;

public class RelatedSet {

    @MongoId
    private String _id;

    private ArrayList<String> rws;

    public RelatedSet() {
    }

    public RelatedSet(String _id, ArrayList<String> rws) {
        this._id = _id;
        this.rws = rws;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public ArrayList<String> getRws() {
        return rws;
    }

    public void setRws(ArrayList<String> rws) {
        this.rws = rws;
    }
}
