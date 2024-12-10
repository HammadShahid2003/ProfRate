package com.example.profrate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.profrate.model.Loader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteProfile extends AppCompatActivity {

    private Uri uri;
    private Boolean imageUploaded;
    private CircleImageView ivProfilePic;
    private TextInputEditText txtName, txtField;
    private Spinner spinner;
    private Button btnSave;
    private FirebaseFirestore firestore;
    private String uId;
    private int selectedPosition = 0;
    Loader loader;
    ArrayList<String> universityList;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            (v)->{
                if (v.getData() != null && v.getResultCode() == RESULT_OK){
                    uri = v.getData().getData();
                    imageUploaded = true;
                    ivProfilePic.setImageURI(uri);

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complete_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();


        universityList.add("Select a University");
        universityList.add("FAST");
        universityList.add("NUST");
        universityList.add("UMT");
        universityList.add("UCP");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.options_layout,
                universityList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        ivProfilePic.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedPosition = i;
                  Toast.makeText(CompleteProfile.this, "" + i, Toast.LENGTH_SHORT).show();
              }

              @Override
              public void onNothingSelected(AdapterView<?> adapterView) {

              }
        });

        btnSave.setOnClickListener(v -> {
            saveProfileInformation();
        });

    }

    private void init(){
        ivProfilePic = findViewById(R.id.ivProfilePic);
        txtName = findViewById(R.id.tietName);
        txtField = findViewById(R.id.tietFieldName);
        spinner = findViewById(R.id.spinner);
        btnSave = findViewById(R.id.btnSave);
        firestore = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        loader = new Loader(CompleteProfile.this);
        universityList = new ArrayList<>();
    }

    private void saveProfileInformation(){
        if (!imageUploaded){
            Toast.makeText(this, "Kindly Select Picture", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtName.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Kindly Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedPosition == 0){
            Toast.makeText(this, "Kindly Select University", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtField.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Kindly Enter Field Name", Toast.LENGTH_SHORT).show();
            return;
        }
        MediaManager.get().upload(uri).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                loader.show();
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                String url = resultData.get("secure_url").toString();
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", txtName.getText().toString().trim());
                map.put("url", url);
                map.put("university", universityList.get(selectedPosition));
                map.put("field", txtField.getText().toString().trim());
                firestore.collection("Users")
                        .document(uId)
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loader.dismiss();
                                Toast.makeText(CompleteProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loader.dismiss();
                                Toast.makeText(CompleteProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Toast.makeText(CompleteProfile.this, error.getDescription(), Toast.LENGTH_SHORT).show();
                loader.dismiss();
                return;
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Toast.makeText(CompleteProfile.this, error.getDescription(), Toast.LENGTH_SHORT).show();
                loader.dismiss();
                return;
            }
        }).dispatch();
    }

}