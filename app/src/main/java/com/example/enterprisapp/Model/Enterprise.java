package com.example.enterprisapp.Model;

import com.google.firebase.Timestamp;

public class Enterprise {

    String Name, PM, Status;
    int Frequency, no_of_reg_emp, no_of_total_emp;
    Timestamp date;

    public Enterprise(String name, String PM, String status, int frequency, int no_of_reg_emp, int no_of_total_emp,  int status_type) {
        Name = name;
        this.PM = PM;
        Status = status;
        Frequency = frequency;
        this.no_of_reg_emp = no_of_reg_emp;
        this.no_of_total_emp = no_of_total_emp;
        this.status_type = status_type;
    }

    public Enterprise(String name, String PM, String status, int frequency, int status_type) {
        Name = name;
        this.PM = PM;
        Status = status;
        Frequency = frequency;
        this.status_type = status_type;
    }

    public int getStatus_type() {
        return status_type;
    }

    public void setStatus_type(int status_type) {
        this.status_type = status_type;
    }

    int status_type;

    public Enterprise() {
    }

    public Enterprise(String name, String PM, String status, int frequency) {
        Name = name;
        this.PM = PM;
        Status = status;
        Frequency = frequency;
    }

    public Enterprise(String name, String PM, com.google.firebase.Timestamp date, int status_type, int no_of_reg_emp, int no_of_total_emp) {
        Name = name;
        this.PM = PM;
        this.date = date;
        this.status_type = status_type;
        this.no_of_reg_emp = no_of_reg_emp;
        this.no_of_total_emp = no_of_total_emp;
    }

    public Enterprise(String name, String PM, com.google.firebase.Timestamp date, String status, int status_type, int no_of_total_emp, int frequency) {
        Name = name;
        this.PM = PM;
        this.date = date;
        this.Status = status;
        this.status_type = status_type;
        this.Frequency = frequency;
        this.no_of_total_emp = no_of_total_emp;
    }

    public com.google.firebase.Timestamp getDate() {
        return date;
    }

    public void setDate(com.google.firebase.Timestamp date) {
        this.date = date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPM() {
        return PM;
    }

    public void setPM(String PM) {
        this.PM = PM;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getFrequency() {
        return Frequency;
    }

    public void setFrequency(int frequency) {
        Frequency = frequency;
    }

    public int getNo_of_reg_emp() {
        return no_of_reg_emp;
    }

    public void setNo_of_reg_emp(int no_of_reg_emp) {
        this.no_of_reg_emp = no_of_reg_emp;
    }

    public int getNo_of_total_emp() {
        return no_of_total_emp;
    }

    public void setNo_of_total_emp(int no_of_total_emp) {
        this.no_of_total_emp = no_of_total_emp;
    }
}
