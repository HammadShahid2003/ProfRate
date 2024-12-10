package com.example.profrate.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.profrate.ProfessorDetailActivity;
import com.example.profrate.R;

import com.example.profrate.model.Professor;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

public class professor_fragment_adapter extends FirestoreRecyclerAdapter<Professor, professor_fragment_adapter.ProfessorViewHolder> {
private Context context;
    public professor_fragment_adapter(@NonNull FirestoreRecyclerOptions<Professor> options, Context c) {
        super(options);
        this.context=c;
    }

    @NonNull
    @Override
    public ProfessorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_professor_layout, parent, false);
        return new ProfessorViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProfessorViewHolder holder, int position, @NonNull Professor model) {
        Log.d("FirestoreDebug", "Binding data at position " + position + ": " + model.getName());

        holder.name.setText(model.getName());
       holder.designation.setText(model.getDesignation());
//        holder.email.setText(model.getEmail());
//        holder.profile.setText(model.getProfile());
        Glide.with(holder.image.getContext()).load(model.getImage()).into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfessorDetailActivity.class);
            intent.putExtra("professorId", getSnapshots().getSnapshot(position).getId()); // Pass document ID
            intent.putExtra("professorName", model.getName());
            intent.putExtra("professorPicture", model.getImage());
            context.startActivity(intent);
        });

    }

    static class ProfessorViewHolder extends RecyclerView.ViewHolder {
        TextView name, designation, email, profile;
        ImageView image;

        public ProfessorViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvprofessor_name);
            designation = itemView.findViewById(R.id.tv_professor_designation);
            image = itemView.findViewById(R.id.iv_professor_card_image);
        }
    }
}