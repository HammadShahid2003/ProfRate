package com.example.profrate.ViewsFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profrate.R;
import com.example.profrate.adapters.CommentsAdapter;
import com.example.profrate.model.Comment;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class CommentSheet extends BottomSheetDialogFragment {

    Context context;
    String postKey;
    RecyclerView rvComments;
    EditText etComment;
    ImageView ivSend;
    FirebaseDatabase database;
    String uId;
    CommentsAdapter adapter;

    public CommentSheet(String postKey){
        this.postKey = postKey;
        database = FirebaseDatabase.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.from(context).inflate(R.layout.comments_layout, container, false);
        rvComments = v.findViewById(R.id.rvComments);
        etComment = v.findViewById(R.id.etComment);
        ivSend = v.findViewById(R.id.ivSend);
        Query query = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postKey)
                .child("comments");
        FirebaseRecyclerOptions<Comment> options = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new CommentsAdapter(options, context);
        rvComments.setAdapter(adapter);
        rvComments.setHasFixedSize(true);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = "";
                comment = etComment.getText().toString().trim();
                HashMap<String, Object> map = new HashMap<>();
                map.put("uId", uId);
                map.put("comment", comment);
                database.getReference("Posts").child(postKey).child("comments")
                        .push()
                        .setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                etComment.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        if (view != null) {
            int desiredHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.7); // 50% of screen height
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = desiredHeight;
            view.setLayoutParams(layoutParams);
        }

        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
