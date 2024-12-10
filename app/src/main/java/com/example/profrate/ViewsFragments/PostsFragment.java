package com.example.profrate.ViewsFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profrate.AddPost;
import com.example.profrate.R;
import com.example.profrate.adapters.PostsAdapter;
import com.example.profrate.model.Keys;
import com.example.profrate.model.Post;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class PostsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context context;
    private PostsAdapter adapter;
    FragmentManager manager;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    private String uId;
    private FloatingActionButton fabAddPost;
    FirebaseFirestore firestore;
    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsFragment newInstance(String param1, String param2) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        manager = getActivity().getSupportFragmentManager();
        View v = inflater.inflate(R.layout.fragment_posts, container, false);
        recyclerView = v.findViewById(R.id.rvPosts);
        progressBar = v.findViewById(R.id.pbLoading);
        fabAddPost = v.findViewById(R.id.fabAddPost);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        fabAddPost.setOnClickListener(view ->{

            FirebaseFirestore.getInstance().collection("Users").document(uId)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                startActivity(new Intent(context, AddPost.class));
                            }
                            else {
                                Toast.makeText(context, "Kindly Complete Your Profile Information First", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
        firestore=FirebaseFirestore.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query query = database.getReference().child("Posts").orderByPriority();
        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(query, new SnapshotParser<Post>() {
                    @NonNull
                    @Override
                    public Post parseSnapshot(@NonNull DataSnapshot snapshot) {

                        Post post = new Post();
                        //post.setUserId(snapshot.child(Keys.PostKeys.userId).getValue().toString());
                        post.setKey(snapshot.getKey());
                        post.setDescription(snapshot.child(Keys.PostKeys.description).getValue().toString());
                        post.setUrl(snapshot.child(Keys.PostKeys.url).getValue().toString());
                        post.setLikeCount("0");
                        if (snapshot.hasChild(Keys.PostKeys.likes)){
                            post.setLikeCount("" + snapshot.child(Keys.PostKeys.likes).getChildrenCount());
                        }
                        post.setCommentCount("0");
                        if (snapshot.hasChild(Keys.PostKeys.comments)){
                            post.setCommentCount("" + snapshot.child(Keys.PostKeys.comments).getChildrenCount());
                        }
                        firestore.collection("Users").document(snapshot.child(Keys.PostKeys.userId).getValue().toString())
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()){
                                            post.setUserId(documentSnapshot.getString("name"));
                                            post.setProfilePic(documentSnapshot.getString("url"));

                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                        return post;
                    }
                })
                .build();

        adapter = new PostsAdapter(options, context, manager, progressBar);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);



        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}