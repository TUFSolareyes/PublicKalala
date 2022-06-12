package com.letmesee.www.pojo;

/**
 * 通信结果实体类
 */
public class ResultVO {

    public static final int OK = 4000;

    public static final int EXCEPTION = 4002;

    public static final int TOKENEXP = 4007;

    public static final int NOLOGIN = 4009;

    private Integer code;

    private String msg;

    private Object data;

    public ResultVO() {
    }

    public ResultVO(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
