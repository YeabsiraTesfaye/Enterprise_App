package com.example.enterprisapp.car.driver.model;

public class Driver {
    String name;
    String email;
    int status;

    public Driver() {
    }

    public Driver(String name, String email, int status) {
        this.name = name;
        this.email = email;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
