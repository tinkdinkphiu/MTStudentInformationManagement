package com.phule.mtstudentinformationmanagement;

import androidx.appcompat.app.AppCompatActivity;

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

public class EditStudentActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    private TextInputLayout ilCode, ilName, ilBirthday, ilAddress, ilGender, ilPhone, ilEnrollmentDate, ilMajor;
    private TextInputEditText etCode, etName, etBirthday, etAddress, etGender, etPhone, etEnrollmentDate, etMajor;
    private Button btnSave;
    private String originalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        Intent intent = getIntent();

        originalCode = intent.getStringExtra("code");
        String name = intent.getStringExtra("name");
        String birthday = intent.getStringExtra("birthday");
        String address = intent.getStringExtra("address");
        String gender = intent.getStringExtra("gender");
        String phone = intent.getStringExtra("phone");
        String enrollmentDate = intent.getStringExtra("enrollmentDate");
        String major = intent.getStringExtra("major");

        initialFirebase();
        initUi();

        populateField(originalCode, name, birthday, address, gender, phone, enrollmentDate, major);

        initListener();
    }

    private void initialFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initUi() {
        ilCode = findViewById(R.id.il_code);
        ilName = findViewById(R.id.il_name);
        ilBirthday = findViewById(R.id.il_birthday);
        ilAddress = findViewById(R.id.il_address);
        ilGender = findViewById(R.id.il_gender);
        ilPhone = findViewById(R.id.il_phone);
        ilEnrollmentDate = findViewById(R.id.il_enrollment_date);
        ilMajor = findViewById(R.id.il_major);

        etCode = findViewById(R.id.et_code);
        etName = findViewById(R.id.et_name);
        etBirthday = findViewById(R.id.et_birthday);
        etAddress = findViewById(R.id.et_address);
        etGender = findViewById(R.id.et_gender);
        etPhone = findViewById(R.id.et_phone);
        etEnrollmentDate = findViewById(R.id.et_enrollment_date);
        etMajor = findViewById(R.id.et_major);

        btnSave = findViewById(R.id.btn_save);
    }

    private void populateField(String code, String name, String birthday, String address, String gender, String phone, String enrollmentDate, String major) {
        etCode.setText(code);
        etName.setText(name);
        etBirthday.setText(birthday);
        etAddress.setText(address);
        etGender.setText(gender);
        etPhone.setText(phone);
        etEnrollmentDate.setText(enrollmentDate);
        etMajor.setText(major);
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
        String editedCode = etCode.getText().toString();
        String editedName = etName.getText().toString();
        String editedBirthday = etBirthday.getText().toString();
        String editedAddress = etAddress.getText().toString();
        String editedGender = etGender.getText().toString();
        String editedPhone = etPhone.getText().toString();
        String editedEnrollmentDate = etEnrollmentDate.getText().toString();
        String editedMajor = etMajor.getText().toString();

        firebaseFirestore.collection("Students").whereEqualTo("code", originalCode).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();

                            Map<String, Object> updatedStudent = new HashMap<>();
                            updatedStudent.put("code", editedCode);
                            updatedStudent.put("name", editedName);
                            updatedStudent.put("birthday", editedBirthday);
                            updatedStudent.put("address", editedAddress);
                            updatedStudent.put("gender", editedGender);
                            updatedStudent.put("phone", editedPhone);
                            updatedStudent.put("enrollmentDate", editedEnrollmentDate);
                            updatedStudent.put("major", editedMajor);

                            firebaseFirestore.collection("Students").document(documentId)
                                    .update(updatedStudent)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(EditStudentActivity.this, "Student updated", Toast.LENGTH_SHORT).show();
                                        Log.d("updateStudent", "Student updated successfully");
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EditStudentActivity.this, "Error updating student", Toast.LENGTH_SHORT).show();
                                        Log.d("updateStudent", "Student updated failed");
                                    });
                        }
                    } else {
                        Toast.makeText(EditStudentActivity.this, "Failed to find student code", Toast.LENGTH_SHORT).show();
                        Log.d("updateStudent", "Failed to find student code");
                    }
                });
    }
}