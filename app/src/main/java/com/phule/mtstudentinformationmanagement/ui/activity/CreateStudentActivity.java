package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.Student;
import com.phule.mtstudentinformationmanagement.helper.DialogHelper;
import com.phule.mtstudentinformationmanagement.helper.FieldValidator;

import java.util.Calendar;

public class CreateStudentActivity extends AppCompatActivity {
    private TextInputEditText etCode, etName, etBirthday, etAddress, etGender, etPhone, etEnrollmentDate, etMajor;
    private Button btnCreate;
    private DialogHelper dialogHelper;
    private FieldValidator fieldValidator;
    private FirebaseFirestore firebaseFirestore;
    private TextView tvClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        dialogHelper = new DialogHelper(this);
        fieldValidator = new FieldValidator(this);

        initFirebase();
        initUi();
        initListener();
    }

    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initUi() {
        etCode = findViewById(R.id.et_code);
        etName = findViewById(R.id.et_name);
        etBirthday = findViewById(R.id.et_birthday);
        etAddress = findViewById(R.id.et_address);
        etGender = findViewById(R.id.et_gender);
        etPhone = findViewById(R.id.et_phone);
        etEnrollmentDate = findViewById(R.id.et_enrollment_date);
        etMajor = findViewById(R.id.et_major);

        etBirthday.setShowSoftInputOnFocus(false);
        etEnrollmentDate.setShowSoftInputOnFocus(false);

        tvClose = findViewById(R.id.tv_close);

        btnCreate = findViewById(R.id.btn_create);
    }

    private void initListener() {
        tvClose.setOnClickListener(view -> {
            finish();
        });
        btnCreate.setOnClickListener(view -> {
            if (isValidField()) {
                String code = etCode.getText().toString();

                checkValidCode(code, () -> {
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
                }, () -> {
                    Toast.makeText(CreateStudentActivity.this, "Student with this code already exists", Toast.LENGTH_SHORT).show();
                });
            }
        });

        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelper.openDatePickerDialog(etBirthday);
            }
        });
        etEnrollmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelper.openDatePickerDialog(etEnrollmentDate);
            }
        });
        etGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelper.openGenderDialog(etGender);
            }
        });
        etMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelper.openMajorDialog(etMajor);
            }
        });
    }

    private void checkValidCode(String code, Runnable onUnique, Runnable onDuplicate) {
        firebaseFirestore.collection("Students")
                .whereEqualTo("code", code)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            onUnique.run();
                        } else {
                            onDuplicate.run();
                        }
                    } else {
                        Toast.makeText(CreateStudentActivity.this, "Error checking code", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidField() {
        if (!fieldValidator.isValidCode(etCode.getText().toString())) {
            Toast.makeText(this, "Code must be number and 8 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidName(etName.getText().toString())) {
            Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidTextField(etAddress.getText().toString())) {
            Toast.makeText(this, "Invalid address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidPhone(etPhone.getText().toString())) {
            Toast.makeText(this, "Phone must be number and 10 characters", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}