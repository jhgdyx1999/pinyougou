package com.pinyougou.entity;

import java.io.Serializable;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/08,21:33
 */
public class DMLResult implements Serializable {

    private boolean success;
    private String message;

    public DMLResult() {
    }

    public DMLResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
