package com.example.profrate.ViewsFragments;

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
import com.example.profrate.adapters.RecyclerAdapter;
import com.example.profrate.adapters.professor_fragment_adapter;
import com.example.profrate.adapters.university_card_adapter;
import com.example.profrate.model.Professor;
import com.example.profrate.model.University;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link University_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class University_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    private university_card_adapter adapter;
    public University_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Professors_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static University_Fragment newInstance(String param1, String param2) {
        University_Fragment fragment = new University_Fragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_university_, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        RecyclerView recyclerView = view.findViewById(R.id.rvUniversities);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        ArrayList<University>itemList = new ArrayList<>();
//        itemList.add(new University("Fast NUCES","https://lhr.nu.edu.pk/media/Faculty/01._Dr_Kashif_Zafar_Prof._CS.JPG"));
//        itemList.add(new University("Fast NUCES", "Hamamdshahid","https://lhr.nu.edu.pk/media/Faculty/6302_5wrK2JS-removebg-preview.png"));
//
//        // Add more items to the list as needed
//
//        RecyclerAdapter adapter = new RecyclerAdapter(itemList);
//        recyclerView.setAdapter(adapter);
        recyclerView = view.findViewById(R.id.rvUniversities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        FirestoreRecyclerOptions<University> options =
                new FirestoreRecyclerOptions.Builder<University>()
                        .setQuery(FirebaseFirestore.getInstance()
                                .collection("University"), University.class)
                        .build();
        FirebaseFirestore.getInstance()
                .collection("University")
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

        adapter = new university_card_adapter(options);
        recyclerView.setAdapter(adapter);
         adapter.startListening();


    }
}