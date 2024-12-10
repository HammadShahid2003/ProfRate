package com.example.profrate.model;

public class Professor {
    private String name;
    private String designation;
    private String email;
//    private String profile;
    private String image;

    // Constructor
    public Professor(String name, String designation, String email, String image) {
        this.name = name;
        this.designation = designation;
        this.email = email;
//        this.profile = profile;
        this.image = image;
    }
    public Professor(){

    }
    // Getters
    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public String getEmail() {
        return email;
    }

//    public String getProfile() {
//        return profile;
//    }

    public String getImage() {
        return image;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public void setProfile(String profile) {
//        this.profile = profile;
//    }

    public void setImage(String image) {
        this.image = image;
    }

//    // toString method
//    @Override
//    public String toString() {
//        return "Professor{" +
//                "name='" + name + '\'' +
//                ", designation='" + designation + '\'' +
//                ", email='" + email + '\'' +
//                ", profile='" + profile + '\'' +
//                ", image='" + image + '\'' +
//                '}';
//    }
}