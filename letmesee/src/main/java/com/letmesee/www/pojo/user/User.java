package com.letmesee.www.pojo.user;

public class User {


    private String _id;

    private String userName;

    private String pass;

    public User(String _id, String userName, String pass) {
        this._id = _id;
        this.userName = userName;
        this.pass = pass;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
