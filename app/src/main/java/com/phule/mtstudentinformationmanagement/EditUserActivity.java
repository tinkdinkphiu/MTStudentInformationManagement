package com.phule.mtstudentinformationmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditUserActivity extends AppCompatActivity {
    private TextInputLayout ilEmail, ilName, ilAge, ilPhone, ilStatus, ilRole;
    private TextInputEditText etEmail, etName, etAge, etPhone, etStatus, etRole;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        initUi();
        initListener();
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
    private void initListener() {

    }
}