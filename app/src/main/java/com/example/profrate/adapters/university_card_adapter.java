package com.example.profrate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.profrate.R;
import com.example.profrate.model.University;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class university_card_adapter extends FirestoreRecyclerAdapter<University, university_card_adapter.UniversityViewHolder> {

    // Constructor to pass FirestoreRecyclerOptions to the adapter
    public university_card_adapter(@NonNull FirestoreRecyclerOptions<University> options) {
        super(options);
    }

    @NonNull
    @Override
    public UniversityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the university card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_university_layout, parent, false);
        return new UniversityViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull UniversityViewHolder holder, int position, @NonNull University model) {
        // Bind data to the views
        holder.name.setText(model.getName());
        Glide.with(holder.image.getContext()).load(model.getImage()).into(holder.image);
    }

    // ViewHolder class for the university card
    static class UniversityViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        public UniversityViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvUniversityCard);
            image = itemView.findViewById(R.id.ivUniversityCard);
        }
    }
}
