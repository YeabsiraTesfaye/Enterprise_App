package com.example.enterprisapp.Model_;

import com.google.firebase.Timestamp;

public class Enterprise {
    String name, remark, assigned_person;
    int type, status, no_of_emp, no_of_reg_emp,meetings;
    Timestamp reg_date, last_meeting, last_payment;

    public Enterprise() {
    }

    public Enterprise(String name, String remark, String assigned_person, int type, int status, int no_of_emp, int no_of_reg_emp, int meetings, Timestamp reg_date, Timestamp last_meeting, Timestamp last_payment) {
        this.name = name;
        this.remark = remark;
        this.assigned_person = assigned_person;
        this.type = type;
        this.status = status;
        this.no_of_emp = no_of_emp;
        this.no_of_reg_emp = no_of_reg_emp;
        this.meetings = meetings;
        this.reg_date = reg_date;
        this.last_meeting = last_meeting;
        this.last_payment = last_payment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAssigned_person() {
        return assigned_person;
    }

    public void setAssigned_person(String assigned_person) {
        this.assigned_person = assigned_person;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNo_of_emp() {
        return no_of_emp;
    }

    public void setNo_of_emp(int no_of_emp) {
        this.no_of_emp = no_of_emp;
    }

    public int getNo_of_reg_emp() {
        return no_of_reg_emp;
    }

    public void setNo_of_reg_emp(int no_of_reg_emp) {
        this.no_of_reg_emp = no_of_reg_emp;
    }

    public int getMeetings() {
        return meetings;
    }

    public void setMeetings(int meetings) {
        this.meetings = meetings;
    }

    public Timestamp getReg_date() {
        return reg_date;
    }

    public void setReg_date(Timestamp reg_date) {
        this.reg_date = reg_date;
    }

    public Timestamp getLast_meeting() {
        return last_meeting;
    }

    public void setLast_meeting(Timestamp last_meeting) {
        this.last_meeting = last_meeting;
    }

    public Timestamp getLast_payment() {
        return last_payment;
    }

    public void setLast_payment(Timestamp last_payment) {
        this.last_payment = last_payment;
    }
}
