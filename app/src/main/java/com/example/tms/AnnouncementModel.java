package com.example.tms;

import java.util.ArrayList;

public class AnnouncementModel {

    private String FullName,Time,Announce_content,tcName;

    public AnnouncementModel(){};

    public AnnouncementModel(String fullName, String time, String announce_content, String tcName) {
        FullName = fullName;
        Time = time;
        Announce_content = announce_content;
        this.tcName = tcName;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAnnounce_content() {
        return Announce_content;
    }

    public void setAnnounce_content(String announce_content) {
        Announce_content = announce_content;
    }

    public String getTcName() {
        return tcName;
    }

    public void setTcName(String tcName) {
        this.tcName = tcName;
    }

    @Override
    public String toString() {
        return "AnnouncementModel{" +
                "FullName='" + FullName + '\'' +
                ", Time='" + Time + '\'' +
                ", Announce_content='" + Announce_content + '\'' +
                ", tcName='" + tcName + '\'' +
                '}';
    }
}
