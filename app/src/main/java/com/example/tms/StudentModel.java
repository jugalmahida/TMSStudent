package com.example.tms;

import java.math.BigInteger;
import java.util.ArrayList;

public class StudentModel {
    private String tcName,fullName,email,phoneNumber,standards,status;
    private ArrayList<String> subjects;

public StudentModel(){}

    public StudentModel(String tcName, String fullName, String email, String phoneNumber, String standards, ArrayList<String> subjects,String status) {
        this.tcName = tcName;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.standards = standards;
        this.subjects = subjects;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTcName() {
        return tcName;
    }

    public void setTcName(String tcName) {
        this.tcName = tcName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStandards() {
        return standards;
    }

    public void setStandards(String standards) {
        this.standards = standards;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        return "StudentModel{" +
                "tcName='" + tcName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", standards='" + standards + '\'' +
                ", subjects=" + subjects +
                '}';
    }
}
