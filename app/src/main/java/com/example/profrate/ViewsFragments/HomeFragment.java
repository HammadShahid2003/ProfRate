package com.example.profrate.ViewsFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.profrate.R;
import com.example.profrate.adapters.HomeUniversityAdapter;
import com.example.profrate.adapters.HomeProfessorAdapter;
import com.example.profrate.model.Professor;
import com.example.profrate.model.University;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {

    private RecyclerView professorRecyclerView;
    private RecyclerView universityRecyclerView;
    private HomeProfessorAdapter professorAdapter;
    private HomeUniversityAdapter universityAdapter;

    private CollectionReference professorRef, universityRef;
    private Dialog loaderDialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        professorRecyclerView = rootView.findViewById(R.id.rvhome_professor);
        universityRecyclerView = rootView.findViewById(R.id.rvhome_universities);

        // Initialize Firestore references
        professorRef = FirebaseFirestore.getInstance().collection("Professor");
        universityRef = FirebaseFirestore.getInstance().collection("University");

        loaderDialog = new Dialog(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.loader_dialog, null);
        loaderDialog.setContentView(dialogView);
        LottieAnimationView animationView = dialogView.findViewById(R.id.lottie);
        animationView.playAnimation();
        loaderDialog.setCancelable(false);

        // Show loader while data is loading
       loaderDialog.show();
        // Set up RecyclerViews with adapters
        professorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        universityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Query top professors and universities (for example, limit to 3)
        Query professorQuery = professorRef.limit(3);
        Query universityQuery = universityRef.limit(3);

        FirestoreRecyclerOptions<Professor> professorOptions = new FirestoreRecyclerOptions.Builder<Professor>()
                .setQuery(professorQuery, Professor.class)
                .build();

        FirestoreRecyclerOptions<University> universityOptions = new FirestoreRecyclerOptions.Builder<University>()
                .setQuery(universityQuery, University.class)
                .build();

        professorAdapter = new HomeProfessorAdapter(professorOptions,loaderDialog);
        universityAdapter = new HomeUniversityAdapter(universityOptions,loaderDialog);

        professorRecyclerView.setAdapter(professorAdapter);
        universityRecyclerView.setAdapter(universityAdapter);

        // Hide loader after data is loaded
        professorAdapter.startListening();
        universityAdapter.startListening();

    }

    private void showLoader() {
        if (loaderDialog != null && !loaderDialog.isShowing()) {
            loaderDialog.show();
        }
    }

    private void hideLoader() {
        if (loaderDialog != null && loaderDialog.isShowing()) {
            loaderDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (professorAdapter != null) {
            professorAdapter.startListening();
        }
        if (universityAdapter != null) {
            universityAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (professorAdapter != null) {
            professorAdapter.stopListening();
        }
        if (universityAdapter != null) {
            universityAdapter.stopListening();
        }
    }
}