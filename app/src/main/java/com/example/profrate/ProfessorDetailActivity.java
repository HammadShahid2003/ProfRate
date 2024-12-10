package com.example.profrate;

import static java.security.AccessController.getContext;
import com.airbnb.lottie.LottieAnimationView;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.profrate.R;
import com.example.profrate.adapters.ReviewAdapter;
import com.example.profrate.model.Review;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfessorDetailActivity extends AppCompatActivity {

    private RecyclerView reviewsRecyclerView;
    private RatingBar overallRatingBar;
    private Button addReviewButton;
    private TextView professorNameText;
    CircleImageView profilepic;
    private FirebaseFirestore firestore;
    private ReviewAdapter reviewAdapter;
private Intent intent;
    // Replace with the actual professorId (e.g., passed via Intent)
    private String professorId;
    private Dialog loaderDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_detail);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        loaderDialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.loader_dialog, null);
        loaderDialog.setContentView(dialogView);
        LottieAnimationView animationView = dialogView.findViewById(R.id.lottie);
        animationView.playAnimation();
        loaderDialog.setCancelable(false);

        // Initialize views
        reviewsRecyclerView = findViewById(R.id.rv_professor_reviews);
        overallRatingBar = findViewById(R.id.overallRating);
        addReviewButton = findViewById(R.id.btnaddreview);
        professorNameText = findViewById(R.id.name_text);
        intent=getIntent();
        professorId=intent.getStringExtra("professorId");
        profilepic=findViewById(R.id.profile_image);
        loaderDialog.show();
        Glide.with(profilepic.getContext()).load((intent.getStringExtra("professorPicture"))).into(profilepic);
        professorNameText.setText(intent.getStringExtra("professorName"));

        // Set up RecyclerView
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up FirebaseRecyclerAdapter
        Query query = firestore.collection("Professor")
                .document(professorId)
                .collection("reviews")
                .orderBy("rating") ;
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("FireStoredebug", "onEvent: "+e.toString());
                    return;
                }

                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Log.d("FireStoreDebug", "onEvent: "+document.getData());
                    }
                } else {
                    Log.d("FireStoreDebug","No matching documents.");
                }
            }
        });

        FirestoreRecyclerOptions<Review> options = new FirestoreRecyclerOptions.Builder<Review>()
                .setQuery(query, Review.class)
                .build();

        reviewAdapter = new ReviewAdapter(options);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        // Start listening for Firestore data
        reviewAdapter.startListening();


        addReviewButton.setOnClickListener(v -> openAddReviewDialog(professorId));

        // Calculate and display the professor's overall rating
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
    private void openAddReviewDialog(String professorId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfessorDetailActivity.this);

        // Inflate the custom layout
        LayoutInflater inflater = ProfessorDetailActivity.this.getLayoutInflater();
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
                Toast.makeText(ProfessorDetailActivity.this, "Please provide a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a map or object for the review
            Map<String, Object> review = new HashMap<>();
            review.put("rating", rating);
            review.put("reviewText", reviewText);// Add a timestamp
            loaderDialog.show();
            // Save to Firestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("Professor")
                    .document(professorId)
                    .collection("reviews")
                    .add(review)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ProfessorDetailActivity.this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                        reviewAdapter.notifyDataSetChanged();
                        loaderDialog.dismiss();
                        dialog.dismiss(); // Close the dialog
                    })
                    .addOnFailureListener(e -> {
                        loaderDialog.dismiss();
                        Toast.makeText(ProfessorDetailActivity.this, "Failed to submit review. Try again!", Toast.LENGTH_SHORT).show();
                    });
        });

        // Show the dialog
        dialog.show();
    }


    /**
     * Calculates the overall rating for the professor based on all reviews.
     */
    private void calculateOverallRating() {
        firestore.collection("Professor")
                .document(professorId)
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
