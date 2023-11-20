package com.phule.mtstudentinformationmanagement.ui.activity;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.helper.DialogHelper;

import java.util.HashMap;
import java.util.Map;

public class CreateUserActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private TextInputLayout ilEmail, ilPassword, ilName, ilAge, ilPhone, ilStatus, ilRole;
    private TextInputEditText etEmail, etPassword, etName, etAge, etPhone, etStatus, etRole;
    private Button btnCreate;
    private DialogHelper dialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        dialogHelper = new DialogHelper(this);

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
                                userInfo.put("email", etEmail.getText().toString());
                                userInfo.put("name", etName.getText().toString());
                                userInfo.put("age", etAge.getText().toString());
                                userInfo.put("phone", etPhone.getText().toString());
                                userInfo.put("status", etStatus.getText().toString());
                                userInfo.put("role", etRole.getText().toString());

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
        etStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelper.openStatusDialog(etStatus);
            }
        });
        etRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelper.openRoleDialog(etRole);
            }
        });
    }
}