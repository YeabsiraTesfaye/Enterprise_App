package com.example.enterprisapp.car;


import com.google.firebase.Timestamp;

public class Request {
    String nameOfEmployee;
    String reason;
    Timestamp forWhen, requestTime;
    int status;
    String remark;
    String driver ,from, to;

    public Request() {
    }

    public Request(String nameOfEmployee, String driver, String reason, Timestamp forWhen, Timestamp requestTime, int status, String remark, String from, String to) {
        this.nameOfEmployee = nameOfEmployee;
        this.driver = driver;
        this.reason = reason;
        this.forWhen = forWhen;
        this.status = status;
        this.remark = remark;
        this.requestTime = requestTime;
        this.from = from;
        this.to = to;

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getNameOfEmployee() {
        return nameOfEmployee;
    }

    public void setNameOfEmployee(String nameOfEmployee) {
        this.nameOfEmployee = nameOfEmployee;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getForWhen() {
        return forWhen;
    }

    public void setForWhen(Timestamp forWhen) {
        this.forWhen = forWhen;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }
}
