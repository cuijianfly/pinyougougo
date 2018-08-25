package com.pinyougou.entity;

import java.io.Serializable;

/**
 * 返回页面提示信息
 */
public class Result implements Serializable {
    //是否成功
    private Boolean success;
    //提示信息
    private String msg;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Result(Boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }
}
