package com.example.profrate.adapters;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.profrate.R;
import com.example.profrate.model.University;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeUniversityAdapter extends FirestoreRecyclerAdapter<University, HomeUniversityAdapter.UniversityViewHolder> {
    Dialog loaderDialog;
    public HomeUniversityAdapter(@NonNull FirestoreRecyclerOptions<University> options, Dialog loaderDialog) {
        super(options);
        this.loaderDialog=loaderDialog;
    }

    @Override
    protected void onBindViewHolder(@NonNull UniversityViewHolder holder, int position, @NonNull University model) {
        holder.universityName.setText(model.getName());

        // Load university image using Glide (or Picasso)
        Glide.with(holder.image.getContext()).load(model.getImage()).into(holder.image);
        loaderDialog.dismiss();// Use Glide for image loading
    }

    @NonNull
    @Override
    public UniversityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_card, parent, false);
        return new UniversityViewHolder(view);
    }

    public static class UniversityViewHolder extends RecyclerView.ViewHolder {
        TextView universityName;
        CircleImageView image;

        public UniversityViewHolder(View itemView) {
            super(itemView);
            universityName = itemView.findViewById(R.id.centered_text);  // Make sure the TextView id matches your layout// Add TextView for URL if you want to show it
            image = itemView.findViewById(R.id.circular_image);  // Make sure the ImageView id matches your layout
        }
    }
}

