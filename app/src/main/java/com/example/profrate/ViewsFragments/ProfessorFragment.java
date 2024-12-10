package com.example.profrate.ViewsFragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.profrate.R;

import com.example.profrate.adapters.professor_fragment_adapter;
import com.example.profrate.model.Professor;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfessorFragment extends Fragment {
    Context context;

    private professor_fragment_adapter adapter;
    RecyclerView recyclerView;
    private Dialog loaderDialog;
    public ProfessorFragment(){

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_professors, container, false);

        return v;
    }
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvProfessors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        loaderDialog = new Dialog(getContext());
        loaderDialog.setContentView(R.layout.loader_dialog);
        loaderDialog.setCancelable(false);

        // Show loader while data is loading
        showLoader();
        FirestoreRecyclerOptions<Professor> options =
                new FirestoreRecyclerOptions.Builder<Professor>()
                        .setQuery(FirebaseFirestore.getInstance()
                                .collection("Professor"), Professor.class)
                        .build();
        FirebaseFirestore.getInstance()
                .collection("Professor")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Log.d("FirestoreDebug", "Data retrieved: " + task.getResult().getDocuments());
                        } else {
                            Log.d("FirestoreDebug", "No data found in 'Professor' collection.");
                        }
                    } else {
                        Log.e("FirestoreDebug", "Error retrieving data: ", task.getException());
                    }
                });

        adapter = new professor_fragment_adapter(options,getContext());
        recyclerView.setAdapter(adapter);
       // adapter.startListening();
        Log.d("FirestoreDebug", "onViewCreated:adapter setup "+options.getSnapshots().toString());
//        RecyclerView recyclerView = view.findViewById(R.id.rvProfessors);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        ArrayList<Professor>itemList = new ArrayList<>();
//        itemList.add(new Professor("Dr Kashif zafar", "Professor", "Hamamdshahid","https://lhr.nu.edu.pk/media/Faculty/01._Dr_Kashif_Zafar_Prof._CS.JPG"));
//        itemList.add(new Professor("Hammad", "Professor", "Hamamdshahid","https://lhr.nu.edu.pk/media/Faculty/6302_5wrK2JS-removebg-preview.png"));
//itemList.add(new Professor("Dr Kashif zafar", "Professor", "Hamamdshahid","https://lhr.nu.edu.pk/media/Faculty/01._Dr_Kashif_Zafar_Prof._CS.JPG"));
//        itemList.add(new Professor("Hammad", "Professor", "Hamamdshahid","https://lhr.nu.edu.pk/media/Faculty/6302_5wrK2JS-removebg-preview.png"));
//itemList.add(new Professor("Dr Kashif zafar", "Professor", "Hamamdshahid","https://lhr.nu.edu.pk/media/Faculty/01._Dr_Kashif_Zafar_Prof._CS.JPG"));
//        itemList.add(new Professor("Hammad", "Professor", "Hamamdshahid","https://lhr.nu.edu.pk/media/Faculty/6302_5wrK2JS-removebg-preview.png"));
//
//        // Add more items to the list as needed
//
//        RecyclerAdapter adapter = new RecyclerAdapter(itemList);
//        recyclerView.setAdapter(adapter);
loaderDialog.dismiss();
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

}