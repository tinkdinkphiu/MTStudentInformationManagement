package com.phule.mtstudentinformationmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateUserActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private TextInputLayout ilEmail, ilPassword, ilName, ilAge, ilPhone, ilStatus, ilRole;
    private TextInputEditText etEmail, etPassword, etName, etAge, etPhone, etStatus, etRole;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        initUi();
        initListener();
    }

    private void initUi() {
        ilEmail = findViewById(R.id.il_email);
        ilPassword = findViewById(R.id.il_password);
        ilName = findViewById(R.id.il_name);
        ilAge = findViewById(R.id.il_age);
        ilPhone = findViewById(R.id.il_phone);
        ilStatus = findViewById(R.id.il_status);
        ilRole = findViewById(R.id.il_role);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etPhone = findViewById(R.id.et_phone);
        etStatus = findViewById(R.id.et_status);
        etRole = findViewById(R.id.et_role);

        btnCreate = findViewById(R.id.btn_create);
    }

    private void initListener() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                Toast.makeText(CreateUserActivity.this, "User created", Toast.LENGTH_SHORT).show();

                                DocumentReference df = firebaseFirestore.collection("Users").document(user.getUid());
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("Email", etEmail.getText().toString());
                                userInfo.put("Name", etName.getText().toString());
                                userInfo.put("Age", etAge.getText().toString());
                                userInfo.put("Phone", etPhone.getText().toString());
                                userInfo.put("Status", etStatus.getText().toString());
                                userInfo.put("Role", etRole.getText().toString());

                                df.set(userInfo);

                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateUserActivity.this, "User creation failed", Toast.LENGTH_SHORT).show();
                                Log.e("UserCreation", "Error: " + e.getMessage());
                            }
                        });
            }
        });
    }
}