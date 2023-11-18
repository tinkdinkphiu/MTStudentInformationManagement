package com.phule.mtstudentinformationmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private TextInputLayout ilEmail, ilName, ilAge, ilPhone, ilStatus, ilRole;
    private TextInputEditText etEmail, etName, etAge, etPhone, etStatus, etRole;
    private Button btnSave;
    private String originalEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Intent intent = getIntent();

        initialFirebase();
        initUi();

        originalEmail = intent.getStringExtra("email");
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        String phone = intent.getStringExtra("phone");
        String status = intent.getStringExtra("status");
        String role = intent.getStringExtra("role");

        populateField(originalEmail, name, age, phone, status, role);

        initListener();
    }
    private void initialFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initUi() {
        ilEmail = findViewById(R.id.il_email);
        ilName = findViewById(R.id.il_name);
        ilAge = findViewById(R.id.il_age);
        ilPhone = findViewById(R.id.il_phone);
        ilStatus = findViewById(R.id.il_status);
        ilRole = findViewById(R.id.il_role);

        etEmail = findViewById(R.id.et_email);
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etPhone = findViewById(R.id.et_phone);
        etStatus = findViewById(R.id.et_status);
        etRole = findViewById(R.id.et_role);

        btnSave = findViewById(R.id.btn_save);
    }
    private void populateField(String email, String name, String age, String phone, String status, String role) {
        etEmail.setText(email);
        etName.setText(name);
        etAge.setText(age);
        etPhone.setText(phone);
        etStatus.setText(status);
        etRole.setText(role);
    }
    private void initListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClick();
            }
        });
    }

    private void onSaveClick() {
        String editedEmail = etEmail.getText().toString();
        String editedName = etName.getText().toString();
        String editedAge = etAge.getText().toString();
        String editedPhone = etPhone.getText().toString();
        String editedStatus = etStatus.getText().toString();
        String editedRole = etRole.getText().toString();

        firebaseFirestore.collection("Users").whereEqualTo("email", originalEmail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();

                            Map<String, Object> updatedUser = new HashMap<>();
                            updatedUser.put("email", editedEmail);
                            updatedUser.put("name", editedName);
                            updatedUser.put("age", editedAge);
                            updatedUser.put("phone", editedPhone);
                            updatedUser.put("status", editedStatus);
                            updatedUser.put("role", editedRole);

                            firebaseFirestore.collection("Users").document(documentId)
                                    .update(updatedUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(EditUserActivity.this, "User updated", Toast.LENGTH_SHORT).show();
                                        Log.d("updateUser", "User updated successfully");
                                        // ReloadAfterEditUser(4) - Return result to MainActivity
                                        setResult(Activity.RESULT_OK);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EditUserActivity.this, "Error updating user", Toast.LENGTH_SHORT).show();
                                        Log.d("updateUser", "User updated failed");
                                    });
                        }
                    } else {
                        Toast.makeText(EditUserActivity.this, "Failed to find user", Toast.LENGTH_SHORT).show();
                        Log.d("updateUser", "Failed to find user");
                    }
                });
    }
}