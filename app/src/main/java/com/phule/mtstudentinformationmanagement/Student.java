package com.phule.mtstudentinformationmanagement;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class Student implements Serializable {
    @PropertyName("Code")
    private String code;
    @PropertyName("Name")
    private String name;
    @PropertyName("Birthday")
    private String birthday;
    @PropertyName("Address")
    private String address;
    @PropertyName("Gender")
    private boolean gender;
    @PropertyName("Phone")
    private String phone;
    @PropertyName("EnrollmentDate")
    private String enrollmentDate;
    @PropertyName("Major")
    private String major;
    public Student() {

    }

    public Student(String code, String name, String birthday, String address, boolean gender, String phone, String enrollmentDate, String major) {
        this.code = code;
        this.name = name;
        this.birthday = birthday;
        this.address = address;
        this.gender = gender;
        this.phone = phone;
        this.enrollmentDate = enrollmentDate;
        this.major = major;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
