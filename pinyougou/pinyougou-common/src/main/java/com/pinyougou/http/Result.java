package com.pinyougou.http;

import java.io.Serializable;

public class Result implements Serializable {

    //响应的消息
    private String message;
    //响应的状态
    private boolean success;

    public Result() {
    }

    public Result(boolean success) {
        this.success = success;
    }

    public Result(boolean success,String message ) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
