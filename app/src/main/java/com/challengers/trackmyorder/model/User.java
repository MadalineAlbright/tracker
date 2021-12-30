package com.challengers.trackmyorder.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class User{
    private String userId, username,type;

    public User() {
    }

    public User(String userId, String username, String type) {
        this.userId = userId;
        this.username = username;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
