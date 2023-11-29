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
import com.phule.mtstudentinformationmanagement.helper.FieldValidator;

import java.util.HashMap;
import java.util.Map;

public class CreateUserActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private TextInputEditText etEmail, etPassword, etName, etAge, etPhone, etStatus, etRole;
    private Button btnCreate;
    private DialogHelper dialogHelper;
    private FieldValidator fieldValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        dialogHelper = new DialogHelper(this);
        fieldValidator = new FieldValidator(this);

        initFirebase();
        initUi();
        initListener();
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initUi() {
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
                if (isValidField()) {
                    firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    Toast.makeText(CreateUserActivity.this, "User created", Toast.LENGTH_SHORT).show();

                                    DocumentReference documentReference = firebaseFirestore.collection("Users").document(user.getUid());
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("email", etEmail.getText().toString());
                                    userInfo.put("name", etName.getText().toString());
                                    userInfo.put("age", etAge.getText().toString());
                                    userInfo.put("phone", etPhone.getText().toString());
                                    userInfo.put("status", etStatus.getText().toString());
                                    userInfo.put("role", etRole.getText().toString());

                                    documentReference.set(userInfo);

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

    private boolean isValidField() {
        if (!fieldValidator.isValidEmail(etEmail.getText().toString())) {
            Toast.makeText(this, "Wrong email format", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidPassword(etPassword.getText().toString())) {
            Toast.makeText(this, "Password must be 6 characters or more", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidName(etName.getText().toString())) {
            Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidIntegerField(etAge.getText().toString())) {
            Toast.makeText(this, "Invalid age", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidPhone(etPhone.getText().toString())) {
            Toast.makeText(this, "Phone must be number and 10 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}