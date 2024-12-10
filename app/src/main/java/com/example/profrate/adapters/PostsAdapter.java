package com.example.profrate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profrate.R;
import com.example.profrate.ViewsFragments.CommentSheet;
import com.example.profrate.ViewsFragments.LikesSheet;
import com.example.profrate.model.Keys;
import com.example.profrate.model.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class PostsAdapter extends FirebaseRecyclerAdapter<Post, PostsAdapter.PostViewHolder> {


    Context context;
    FragmentManager manager;
    String uId;
    ProgressBar progressBar;

    public PostsAdapter(@NonNull FirebaseRecyclerOptions<Post> options, Context context, FragmentManager manager, ProgressBar progressBar) {
        super(options);
        this.context = context;
        this.manager = manager;
        this.progressBar = progressBar;
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
        progressBar.setVisibility(View.GONE);
        holder.tvUserName.setText(model.getUserId());
        holder.progressBar.setVisibility(View.VISIBLE);

        if (!model.getProfilePic().trim().isEmpty()){
            Picasso.get().load(model.getProfilePic()).into(holder.ivProfilePic);
        }

        Picasso.get().load(model.getUrl()).into(holder.ivPost, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressBar.setVisibility(View.GONE);
            }
        });
        if(!model.getDescription().isEmpty()){
        holder.tvDescription.setText(model.getDescription());
        holder.tvDescription.setVisibility(View.VISIBLE);
        }
        holder.tvLikeCount.setText(model.getLikeCount());
        holder.tvCommentCount.setText(model.getCommentCount());
        holder.ivLike.setTag(R.drawable.like);
        uId = "";

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        FirebaseDatabase.getInstance().getReference("Posts")
                .child(getRef(position).getKey())
                .child(Keys.PostKeys.likes)
                .orderByChild(Keys.PostKeys.userId)
                .equalTo(uId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.ivLike.setImageResource(R.drawable.liked);
                            holder.ivLike.setTag(R.drawable.liked);
                        } else {
                            holder.ivLike.setImageResource(R.drawable.like);
                            holder.ivLike.setTag(R.drawable.like);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Integer)holder.ivLike.getTag() == R.drawable.liked){
                    FirebaseDatabase.getInstance().getReference("Posts")
                            .child(getRef(position).getKey())
                            .child(Keys.PostKeys.likes)
                            .orderByChild(Keys.PostKeys.userId)
                            .equalTo(uId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot likedSnapshot: snapshot.getChildren()){
                                            likedSnapshot.getRef().removeValue();
                                        }
                                        holder.ivLike.setImageResource(R.drawable.like);
                                        holder.ivLike.setTag(R.drawable.like);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    return;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put(Keys.PostKeys.userId, uId);
                FirebaseDatabase.getInstance().getReference("Posts")
                        .child(Objects.requireNonNull(getRef(position).getKey()))
                        .child("likes")
                        .push()
                        .setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                holder.ivLike.setImageResource(R.drawable.liked);
                                holder.ivLike.setTag(R.drawable.liked);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Could Not Like. Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        holder.ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentSheet commentSheet = new CommentSheet(model.getKey());
                commentSheet.show(manager, commentSheet.getTag());
            }
        });

        holder.tvLikeCount.setOnClickListener(v -> {
            LikesSheet likesSheet = new LikesSheet(model.getKey());
            likesSheet.show(manager, likesSheet.getTag());
        });
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_post_layout, parent, false);
        return new PostViewHolder(v);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        ImageView ivPost, ivLike, ivComment, ivProfilePic;
        TextView tvDescription, tvUserName, tvLikeCount, tvCommentCount;
        ProgressBar progressBar;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUsername);
            ivPost = itemView.findViewById(R.id.ivPost);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            progressBar = itemView.findViewById(R.id.pbLoading);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
        }
    }

}