package com.example.profrate.model;

public class Post {
    String url;
    String description;
    String userId;
    String likeCount;
    String commentCount;
    String key;
    String profilePic;


    public Post(){
        url = "";
        description = "";
        userId = "";
        key = "";
        profilePic = "";
    }

    public Post(String url, String description, String userId, String likeCount, String CommentCount, String profilePic) {
        this.url = url;
        this.description = description;
        this.userId = userId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.profilePic = profilePic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}