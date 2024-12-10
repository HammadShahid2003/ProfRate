package com.example.profrate.ViewsFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profrate.R;
import com.example.profrate.adapters.LikesAdapter;
import com.example.profrate.model.Like;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class LikesSheet extends BottomSheetDialogFragment {

    Context context;
    RecyclerView rvLikes;
    LikesAdapter adapter;
    private String postKey;
    ProgressBar pbLoading;

    public LikesSheet(String postKey){
        this.postKey = postKey;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.likes_layout, container, false);

        rvLikes = v.findViewById(R.id.rvLikes);
        pbLoading = v.findViewById(R.id.pbLoading);
        Query query = FirebaseDatabase.getInstance().getReference("Posts").child(postKey).child("likes");
        FirebaseRecyclerOptions<Like> options = new FirebaseRecyclerOptions.Builder<Like>()
                .setQuery(query, Like.class)
                .build();
        adapter = new LikesAdapter(options, context, pbLoading);
        rvLikes.setAdapter(adapter);
        rvLikes.setHasFixedSize(true);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            int desiredHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.7);
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
