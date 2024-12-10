package com.example.profrate.model;

public class User {
    private String userId;
    private String name;
    private String university;
    private String profilePicture;

    // Constructor
    public User(String userId, String name, String university, String profilePicture) {
        this.userId = userId;
        this.name = name;
        this.university = university;
        this.profilePicture = profilePicture;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }


}
