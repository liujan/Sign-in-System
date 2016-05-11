package com.liujan.domain;

import java.io.Serializable;

/**
 * Created by liujan on 5/9/16.
 */
public class Result<T> implements Serializable {
    private T data;
    private int status;
    private String msg;

    public enum Status {
        SUCCESS(200, "success"),
        USER_ERROR(-1, "user name error!"),
        PWD_ERROR(-2, "password error!"),
        DEVICE_ERROR(-3, "please use your phone to sign in!"),
        EMAIL_ERROR(-4, "this email has been used!"),
        STUDENT_ERROR(-5, "this is no student!"),
        ERROR(404, "error");
        private int status;
        private String msg;

        Status(int status, String msg) {
            this.status = status;
            this.msg = msg;
        }
        public int getStatus() {
            return this.status;
        }
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Result<T> status(Status status) {
        this.status = status.status;
        this.msg = status.msg;
        return this;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }
}
