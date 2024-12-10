package com.example.profrate.model;

public class University {
    private String id;
    private String name;
    private String URLLink;

    public String getImage() {
        return image;
    }

    private String image;
    public University(String name,String id, String URL) {
        this.name = name;
        this.id=id;
        this.URLLink=URL;

    }
    public University(String name ,String image) {
        this.name = name;
        this.image=image;
    }
public University(){

}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURLLink() {
        return URLLink;
    }

    public void setURLLink(String URLLink) {
        this.URLLink = URLLink;
    }
}
