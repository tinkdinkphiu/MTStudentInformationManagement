package com.phule.mtstudentinformationmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateStudentActivity extends AppCompatActivity {
    private TextInputLayout ilCode, ilName, ilBirthday, ilAddress, ilGender, ilPhone, ilEnrollmentDate, ilMajor;
    private TextInputEditText etCode, etName, etBirthday, etAddress, etGender, etPhone, etEnrollmentDate, etMajor;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        initUi();
        initListener();
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

        btnCreate = findViewById(R.id.btn_create);
    }

    private void initListener() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = etCode.getText().toString();
                String name = etName.getText().toString();
                String birthday = etBirthday.getText().toString();
                String address = etAddress.getText().toString();
                String gender = etGender.getText().toString();
                String phone = etPhone.getText().toString();
                String enrollmentDate = etEnrollmentDate.getText().toString();
                String major = etMajor.getText().toString();

                Student newStudent = new Student(code, name, birthday, address, gender, phone, enrollmentDate, major);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Students").add(newStudent)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(CreateStudentActivity.this, "Student added SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CreateStudentActivity.this, "Student added FAILED", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}