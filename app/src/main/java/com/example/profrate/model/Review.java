package com.example.profrate.model;

public class Review {
    private String UID;
    private double rating;
    private String reviewText;

    public Review() {
        // Default constructor required for Firestore
    }

    public Review(double rating, String reviewText) {
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public double getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }
}
