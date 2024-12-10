package com.example.profrate.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profrate.R;
import com.example.profrate.model.Review;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ReviewAdapter extends FirestoreRecyclerAdapter<Review, ReviewAdapter.ReviewViewHolder> {

    public ReviewAdapter(@NonNull FirestoreRecyclerOptions<Review> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull Review model) {
        holder.ratingBar.setRating((float) model.getRating());

        if (model.getReviewText() != null && !model.getReviewText().isEmpty()) {
            holder.reviewText.setText(model.getReviewText());
            holder.reviewText.setVisibility(View.VISIBLE);
        } else {
            holder.reviewText.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_review_layout, parent, false);
        return new ReviewViewHolder(view);
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView reviewText;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.each_user_rating_bar);
            reviewText = itemView.findViewById(R.id.user_review_text);
        }
    }
}
