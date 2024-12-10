package com.example.profrate.model;

public class Like {
    private String userId;

    public Like(){
        userId = "";
    }

    public Like(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
