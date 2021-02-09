package com.example.blogapp.Activites;

import java.sql.Timestamp;

public class Post {

    private String title, description, picture, userId, UserPhoto, Key;
   // private Timestamp timestamp;

    public Post(String title, String description, String picture, String userId, String userPhoto, Timestamp timestamp) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.userId = userId;
        UserPhoto = userPhoto;
        //this.timestamp = timestamp;
    }

    public Post() {
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhoto() {
        return UserPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        UserPhoto = userPhoto;
    }

    /*public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }*/
}
