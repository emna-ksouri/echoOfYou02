package com.example.echoofyou02;

import java.util.Date;

public class Capsule {
    private String title;
    private String description;
    private String users;
    private String fileBase64;
    private Date dateTime;

    // Default constructor
    public Capsule() {
    }

    public Capsule(String title, String description, String users, String fileBase64, Date dateTime) {
        this.title = title;
        this.description = description;
        this.users = users;
        this.fileBase64 = fileBase64;
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getFileBase64() {
        return fileBase64;
    }

    public void setFileBase64(String fileBase64) {
        this.fileBase64 = fileBase64;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
