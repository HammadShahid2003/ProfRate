package com.example.profrate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profrate.R;
import com.example.profrate.model.Like;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LikesAdapter extends FirebaseRecyclerAdapter<Like, LikesAdapter.LikeViewHolder> {

    Context context;
    ProgressBar progressBar;


    public LikesAdapter(@NonNull FirebaseRecyclerOptions<Like> options, Context context, ProgressBar progressBar) {
        super(options);
        this.context = context;
        this.progressBar = progressBar;
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onBindViewHolder(@NonNull LikeViewHolder holder, int position, @NonNull Like model) {
        FirebaseFirestore.getInstance().collection("Users").document(model.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        holder.tvUserName.setText(documentSnapshot.getString("name"));
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @NonNull
    @Override
    public LikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_like_layout, parent, false);
        return  new LikeViewHolder(v);
    }

    public class LikeViewHolder extends RecyclerView.ViewHolder{

        TextView tvUserName;
        public LikeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUsername);
        }
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }
}
