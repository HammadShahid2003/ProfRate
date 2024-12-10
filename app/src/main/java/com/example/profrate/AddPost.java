package com.example.profrate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPost extends AppCompatActivity {

    ImageView ivPost;
    Button btnPost;
    TextView txtDescription;
    Uri uri;
    FirebaseUser user;
    String uId;
    FirebaseDatabase database;
    Loader loader;
    String url;
    boolean imageUploaded;
    String description = "";

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            (v)->{
                if (v.getData() != null && v.getResultCode() == RESULT_OK){
                    uri = v.getData().getData();
                    imageUploaded = true;
                    ivPost.setImageURI(uri);
                    ivPost.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        ivPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intent);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });
    }

    private void init(){
        ivPost = findViewById(R.id.ivPost);
        txtDescription = findViewById(R.id.txtDescription);
        btnPost = findViewById(R.id.btnPost);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        database = FirebaseDatabase.getInstance();
        loader = new Loader(AddPost.this);
        imageUploaded = false;
    }


    private void post(){

        if (!imageUploaded){
            Toast.makeText(this, "Kindly Select Image", Toast.LENGTH_SHORT).show();
            return;
        }

        description = "";
        if (!txtDescription.getText().toString().trim().isEmpty()){
            description = txtDescription.getText().toString().trim();
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
                url = (String) resultData.get("secure_url");
                HashMap<String, Object> map = new HashMap<>();
                map.put("userId", uId);
                map.put("description", description);
                map.put("url", url);
                map.put("likes", new ArrayList<String>());
                map.put("timestamp", System.currentTimeMillis());
                database.getReference("Posts")
                        .push()
                        .setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loader.dismiss();
                                Toast.makeText(AddPost.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loader.dismiss();
                                Toast.makeText(AddPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                loader.dismiss();
                Toast.makeText(AddPost.this, error.getDescription(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                loader.dismiss();
                Toast.makeText(AddPost.this, error.getDescription(), Toast.LENGTH_SHORT).show();
            }
        }).dispatch();

    }
}