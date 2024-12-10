package com.example.profrate.adapters;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.profrate.R;
import com.example.profrate.model.Professor;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeProfessorAdapter extends FirestoreRecyclerAdapter<Professor, HomeProfessorAdapter.ProfessorViewHolder> {
    Dialog loaderDialog;
    public HomeProfessorAdapter(@NonNull FirestoreRecyclerOptions<Professor> options, Dialog loaderDialog) {
        super(options);
        this.loaderDialog=loaderDialog;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProfessorViewHolder holder, int position, @NonNull Professor model) {
        holder.professorName.setText(model.getName());

        Glide.with(holder.Image.getContext()).load(model.getImage()).into(holder.Image);
        // Use Picasso or Glide to load image
        loaderDialog.dismiss();
    }

    @NonNull
    @Override
    public ProfessorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_card, parent, false);
        return new ProfessorViewHolder(view);
    }

    public static class ProfessorViewHolder extends RecyclerView.ViewHolder {
        TextView professorName;
        CircleImageView Image;

        public ProfessorViewHolder(View itemView) {
            super(itemView);
            professorName = itemView.findViewById(R.id.centered_text);

           Image = itemView.findViewById(R.id.circular_image);
        }
    }
}
