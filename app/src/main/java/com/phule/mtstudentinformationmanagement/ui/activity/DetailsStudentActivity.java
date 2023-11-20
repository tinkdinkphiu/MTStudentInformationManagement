package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.phule.mtstudentinformationmanagement.R;

public class DetailsStudentActivity extends AppCompatActivity {
    private TextView tvCode, tvName, tvBirthday, tvGender, tvAddress, tvPhone, tvEnrollmentDate, tvMajor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_student);

        Intent intent = getIntent();

        String code = intent.getStringExtra("code");
        String name = intent.getStringExtra("name");
        String birthday = intent.getStringExtra("birthday");
        String address = intent.getStringExtra("address");
        String gender = intent.getStringExtra("gender");
        String phone = intent.getStringExtra("phone");
        String enrollmentDate = intent.getStringExtra("enrollmentDate");
        String major = intent.getStringExtra("major");

        initUi();
        populateField(code, name, birthday, address, gender, phone, enrollmentDate, major);
    }
    private void initUi() {
        tvCode = findViewById(R.id.tv_code);
        tvName = findViewById(R.id.tv_name);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvGender = findViewById(R.id.tv_gender);
        tvAddress = findViewById(R.id.tv_address);
        tvPhone = findViewById(R.id.tv_phone);
        tvEnrollmentDate = findViewById(R.id.tv_enrollmentDate);
        tvMajor = findViewById(R.id.tv_major);
    }

    private void populateField(String code, String name, String birthday, String address, String gender, String phone, String enrollmentDate, String major) {
        tvCode.setText(code);
        tvName.setText(name);
        tvBirthday.setText(birthday);
        tvAddress.setText(address);
        tvGender.setText(gender);
        tvPhone.setText(phone);
        tvEnrollmentDate.setText(enrollmentDate);
        tvMajor.setText(major);
    }
}