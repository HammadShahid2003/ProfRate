package com.example.profrate;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.profrate.adapters.ReviewAdapter;
import com.example.profrate.model.Review;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class UniversityReviewActivity extends AppCompatActivity {

    private RecyclerView reviewsRecyclerView;
    private RatingBar overallRatingBar;
    private Button addReviewButton;
    private TextView universityTitleText;
    private ImageView image;
    private FirebaseFirestore firestore;
    private ReviewAdapter reviewAdapter;
    private Dialog loaderDialog;
    // Replace with the actual universityId (e.g., passed via Intent)
    private String universityId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_review);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        reviewsRecyclerView = findViewById(R.id.restaurant_reviews_recycler);
        overallRatingBar = findViewById(R.id.ratingBar);
        addReviewButton = findViewById(R.id.add_review);
        universityTitleText = findViewById(R.id.University_title);
        image=findViewById(R.id.university_profile_pic);
        loaderDialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.loader_dialog, null);
        loaderDialog.setContentView(dialogView);
        LottieAnimationView animationView = dialogView.findViewById(R.id.lottie);
        animationView.playAnimation();
        loaderDialog.setCancelable(false);
        loaderDialog.show();
        // Retrieve data from the intent
        universityId = getIntent().getStringExtra("universityId");
        String universityName = getIntent().getStringExtra("universityName");
        universityTitleText.setText(universityName);
        Glide.with(image.getContext()).load((getIntent().getStringExtra("universityPicture"))).into(image);
        // Set up RecyclerView
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up FirebaseRecyclerAdapter
        Query query = firestore.collection("University")
                .document(universityId)
                .collection("reviews")
                .orderBy("rating");
        FirestoreRecyclerOptions<Review> options = new FirestoreRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class)
                .build();

        reviewAdapter = new ReviewAdapter(options);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        // Start listening for Firestore data
        reviewAdapter.startListening();

        // Set button click listener
        addReviewButton.setOnClickListener(v -> openAddReviewDialog());

        // Calculate and display the university's overall rating
        calculateOverallRating();
        loaderDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (reviewAdapter != null) {
            reviewAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (reviewAdapter != null) {
            reviewAdapter.stopListening();
        }
    }

    /**
     * Opens the dialog to add a new review.
     */
    private void openAddReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UniversityReviewActivity.this);

        // Inflate the custom layout
        LayoutInflater inflater = UniversityReviewActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_add_review, null);

        // Initialize the views from the dialog
        RatingBar ratingBar = dialogView.findViewById(R.id.review_rating_bar);
        EditText reviewEditText = dialogView.findViewById(R.id.review_edit_text);
        Button submitButton = dialogView.findViewById(R.id.submit_review_button);

        // Set the custom layout to the AlertDialog builder
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set submit button click listener
        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String reviewText = reviewEditText.getText().toString().trim();

            if (rating == 0.0f) {
                Toast.makeText(UniversityReviewActivity.this, "Please provide a rating", Toast.LENGTH_SHORT).show();
                return;
            }
            loaderDialog.show();

            // Create a map or object for the review
            Map<String, Object> review = new HashMap<>();
            review.put("rating", rating);
            review.put("reviewText", reviewText);

            // Save to Firestore
            firestore.collection("University")
                    .document(universityId)
                    .collection("reviews")
                    .add(review)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(UniversityReviewActivity.this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                        reviewAdapter.notifyDataSetChanged();
                        loaderDialog.dismiss();
                        dialog.dismiss(); // Close the dialog
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UniversityReviewActivity.this, "Failed to submit review. Try again!", Toast.LENGTH_SHORT).show();
                        loaderDialog.dismiss();
                    });
        });

        // Show the dialog
        dialog.show();
    }

    /**
     * Calculates the overall rating for the university based on all reviews.
     */
    private void calculateOverallRating() {

        firestore.collection("University")
                .document(universityId)
                .collection("reviews")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    double totalRating = 0;
                    int reviewCount = querySnapshot.size();

                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : querySnapshot) {
                        Double rating = document.getDouble("rating");
                        if (rating != null) {
                            totalRating += rating;
                        }
                    }

                    if (reviewCount > 0) {
                        double averageRating = totalRating / reviewCount;
                        overallRatingBar.setRating((float) averageRating);
                    } else {
                        overallRatingBar.setRating(0);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to calculate overall rating.", Toast.LENGTH_SHORT).show();
                });
    }
    private void showLoader() {
        if (loaderDialog != null && !loaderDialog.isShowing()) {
            loaderDialog.show();
        }
    }

    private void hideLoader() {
        if (loaderDialog != null && loaderDialog.isShowing()) {
            loaderDialog.dismiss();
        }
    }

}
