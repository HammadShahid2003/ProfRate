package com.example.profrate.model;

public class Comment {
    private String uId;
    private String comment;

    public Comment(){
        this.uId = "";
        this.comment = "";
    }

    public Comment(String uId, String comment) {
        this.uId = uId;
        this.comment = comment;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
