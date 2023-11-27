package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phule.mtstudentinformationmanagement.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private TextInputLayout ilEmail, ilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFirebase();
        initUi();
        initListener();
    }
    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initUi() {
        ilEmail = findViewById(R.id.il_email);
        ilPassword = findViewById(R.id.il_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignin = findViewById(R.id.btn_sign_in);
    }

    private void initListener() {
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignIn();
            }
        });
    }

    private void onClickSignIn() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()) {
            Log.d("loginUser", "Empty email or password");
            Toast.makeText(LoginActivity.this, "Please enter Email and Password", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("loginUserSuccess", "signInWithEmail:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                firebaseFirestore.collection("Users").document(user.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                progressDialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String status = document.getString("status");
                                                        if (status.equals("Locked")) {
                                                            firebaseAuth.signOut();
                                                            Toast.makeText(LoginActivity.this, "Account Locked", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            // Add login history
                                                            Map<String, Object> loginRecord = new HashMap<>();
                                                            loginRecord.put("email", user.getEmail());
                                                            loginRecord.put("timestamp", new Timestamp(new Date()));

                                                            firebaseFirestore.collection("Users").document(user.getUid())
                                                                    .collection("LoginHistory").add(loginRecord)
                                                                    .addOnSuccessListener(documentReference ->
                                                                            Log.d("loginHistory", "Login history added " + documentReference.getId()))
                                                                    .addOnFailureListener(e ->
                                                                            Log.w("loginHistory", "Error adding history ", e));
                                                            // Perform login
                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finishAffinity();
                                                        }
                                                    } else {
                                                        Log.d("loginUserFailed", "No such document");
                                                    }
                                                } else {
                                                    Log.d("loginUserFailed", "get failed with ", task.getException());
                                                }
                                            }
                                        });
                            } else {
                                progressDialog.dismiss();
                                Log.w("loginUserFailed", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}