package com.example.profrate;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etSignupEmail, etSignupPassword, etConfirmPassword;
    private RadioGroup radioGroup;
    private RadioButton rbStudent, rbTeacher, rbOther;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private TextView tvLogin;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult();
                        firebaseAuthWithGoogle(account);
                    } catch (Exception e) {
                        showAlertDialog("Google Sign-In failed", e.getMessage());
                    }
                } else {
                    Toast.makeText(this, "Google Sign-In canceled"+result.getResultCode(), Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        initializeViews();

        // Initialize Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set listeners
        setViewListeners();
    }

    private void initializeViews() {
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etsignupPassword);
        etConfirmPassword = findViewById(R.id.etconfirmpassword);
        radioGroup = findViewById(R.id.radioGroup);
        rbStudent = findViewById(R.id.rbStudent);
        rbTeacher = findViewById(R.id.rbTeacher);
        rbOther = findViewById(R.id.rbOther);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setViewListeners() {
        tvLogin.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));

        findViewById(R.id.visibility_toggle).setOnClickListener(v -> togglePasswordVisibility(etSignupPassword, (ImageView) v));
        findViewById(R.id.confirm_visibility_toggle).setOnClickListener(v -> togglePasswordVisibility(etConfirmPassword, (ImageView) v));

        findViewById(R.id.btnsignupGoogle).setOnClickListener(v -> signInWithGoogle());

        btnRegister.setOnClickListener(v -> registerWithEmailPassword());
    }

    private void togglePasswordVisibility(TextInputEditText editText, ImageView toggleView) {
        boolean isVisible = editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleView.setImageResource(R.drawable.ic_visibility); // "Eye closed" icon
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleView.setImageResource(R.drawable.visibility_off); // "Eye open" icon
        }
        editText.setSelection(editText.getText().length());
    }

    private void registerWithEmailPassword() {
        String email = etSignupEmail.getText().toString().trim();
        String password = etSignupPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (validateInputs(email, password, confirmPassword)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            showAlertDialog("Error", task.getException().getMessage());
                        }
                    });
        }
    }

    private boolean validateInputs(String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(email)) {
            etSignupEmail.setError("Email is required!");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etSignupPassword.setError("Password is required!");
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm Password is required!");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match!");
            return false;
        }
        if (password.length() < 8) {
            etSignupPassword.setError("Password must be at least 8 characters!");
            return false;
        }

        int selectedRoleId = radioGroup.getCheckedRadioButtonId();
        if ((selectedRoleId == rbStudent.getId() || selectedRoleId == rbTeacher.getId()) && !email.contains(".edu")) {
            etSignupEmail.setError("Students and Teachers must use a .edu email!");
            return false;
        }
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Select a Role", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        showAlertDialog("Google Sign-In Error", task.getException().getMessage());
                    }
                });
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
