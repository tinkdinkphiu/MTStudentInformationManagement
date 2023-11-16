package com.phule.mtstudentinformationmanagement;

public class User {
    private String email;
    private String name;
    private String age;
    private String phone;
    private String status;
    private String role;

    public User() {

    }

    public User(String email, String name, String age, String phone, String status, String role) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.status = status;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
