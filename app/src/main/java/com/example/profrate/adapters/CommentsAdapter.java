package com.example.profrate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profrate.R;
import com.example.profrate.model.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CommentsAdapter extends FirebaseRecyclerAdapter<Comment, CommentsAdapter.CommentViewHolder> {

    Context context;
    public CommentsAdapter(@NonNull FirebaseRecyclerOptions<Comment> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
        FirebaseFirestore.getInstance().collection("Users").document(model.getuId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        holder.tvUsername.setText(documentSnapshot.getString("name"));
                    }
                });
        //holder.tvUsername.setText(model.getuId());
        holder.tvComment.setText(model.getComment());
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_comment_layout, parent, false);
        return new CommentViewHolder(v);
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername, tvComment;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
        }
    }
}
