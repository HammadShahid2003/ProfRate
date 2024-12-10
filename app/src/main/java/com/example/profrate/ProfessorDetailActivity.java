package com.example.profrate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_detail);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        reviewsRecyclerView = findViewById(R.id.rv_professor_reviews);
        overallRatingBar = findViewById(R.id.overallRating);
        addReviewButton = findViewById(R.id.btnaddreview);
        professorNameText = findViewById(R.id.name_text);
        intent=getIntent();
        professorId=intent.getStringExtra("professorId");
        profilepic=findViewById(R.id.profile_image);

        Glide.with(profilepic.getContext()).load((intent.getStringExtra("professorPicture"))).into(profilepic);
        professorNameText.setText(intent.getStringExtra("professorName"));

        // Set up RecyclerView
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up FirebaseRecyclerAdapter
        Query query = firestore.collection("Professor")
                .document("lgKbdVr4ffLRA1r7UJs9")
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


        addReviewButton.setOnClickListener(v -> openAddReviewDialog());

        // Calculate and display the professor's overall rating
        calculateOverallRating();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout using LayoutInflater
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_add_review, null);

        // Set the custom layout to the AlertDialog builder
        builder.setView(dialogView);
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
}
