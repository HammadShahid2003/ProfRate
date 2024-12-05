package com.example.profrate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private ImageView visibilityToggle;
    private Button loginButton;
    private TextView forgotPasswordTextView, signupTextView;
    private LinearLayout googleLoginButton;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private AlertDialog loaderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        visibilityToggle = findViewById(R.id.visibility_toggle_login);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        googleLoginButton = findViewById(R.id.btnLoginGoogle);
        signupTextView = findViewById(R.id.signupTextView);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Set up visibility toggle for password
        visibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        // Set up Google sign-in button click listener
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        // Set up forgot password button click listener
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });

        // Set up signup text view click listener
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                } else {
                    Log.w("LoginActivity", "Google sign in failed");
                }
            });

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()){
            showErrorDialog("Please fill in email");
            return;
        }
        else if (password.isEmpty()){
            showErrorDialog("Please fill in Password");
            return;
        }

        showLoaderDialog(); // Show loader before starting the sign-in process

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    hideLoaderDialog(); // Hide loader once the task is complete
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                        showErrorDialog("Authentication failed. Please check your email and password.");
                    }
                });
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Login Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void togglePasswordVisibility() {
        if (passwordEditText.getInputType() == 129) { // 129: TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD
            passwordEditText.setInputType(144); // 144: TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            visibilityToggle.setImageResource(R.drawable.visibility_off);
        } else {
            passwordEditText.setInputType(129);
            visibilityToggle.setImageResource(R.drawable.ic_visibility);
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
        showLoaderDialog(); // Show loader when Google sign-in is initiated
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Google Sign-In was successful, authenticate with Firebase
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // Google Sign-In failed, update UI appropriately
            Log.w("LoginActivity", "Google sign in failed", e);
            hideLoaderDialog(); // Hide loader if sign-in fails
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    hideLoaderDialog(); // Hide loader once the task is complete
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void forgotPassword() {
        EditText resetEmail = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Enter your email to receive reset instructions")
                .setView(resetEmail)
                .setPositiveButton("Send", (dialog, which) -> {
                    String email = resetEmail.getText().toString().trim();
                    if (!email.isEmpty()) {
                        showLoaderDialog(); // Show loader before sending the reset email
                        mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(task -> {
                                    hideLoaderDialog(); // Hide loader once the task is complete
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Reset instructions sent to your email", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Error: Unable to send reset instructions", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void signup() {
        Intent i = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(i);
    }


    private void showLoaderDialog() {
        if (loaderDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.loader_dialog);
            builder.setCancelable(false);
            loaderDialog = builder.create();
        }
        loaderDialog.show();
    }

    private void hideLoaderDialog() {
        if (loaderDialog != null && loaderDialog.isShowing()) {
            loaderDialog.dismiss();
        }
    }

}