package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.phule.mtstudentinformationmanagement.R;

public class DetailsUserActivity extends AppCompatActivity {
    private TextView tvEmail, tvName, tvAge, tvPhone, tvRole, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_user);

        initUi();

        Intent intent = getIntent();

        String email = intent.getStringExtra("email");
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        String phone = intent.getStringExtra("phone");
        String role = intent.getStringExtra("role");
        String status = intent.getStringExtra("status");

        populateField(email, name, age, phone, role, status);
    }
    private void initUi() {
        tvEmail = findViewById(R.id.tv_email);
        tvName = findViewById(R.id.tv_name);
        tvAge = findViewById(R.id.tv_age);
        tvPhone = findViewById(R.id.tv_phone);
        tvRole = findViewById(R.id.tv_role);
        tvStatus = findViewById(R.id.tv_status);
    }
    private void populateField(String email, String name, String age, String phone, String role, String status) {
        tvEmail.setText(email);
        tvName.setText(name);
        tvAge.setText(age);
        tvPhone.setText(phone);
        tvRole.setText(role);
        tvStatus.setText(status);
    }
}