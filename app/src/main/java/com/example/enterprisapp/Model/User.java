package com.example.enterprisapp.Model;

public class User {
    String email;
    int role;

    public User() {
    }

    public User(String email, int role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
