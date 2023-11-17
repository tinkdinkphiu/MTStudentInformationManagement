package com.phule.mtstudentinformationmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

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
        etBirthday = findViewById(R.id.et_birthday);
        etBirthday.setShowSoftInputOnFocus(false);
        etBirthday.setCursorVisible(false);
        etBirthday.setFocusableInTouchMode(true);

        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(etBirthday);
            }
        });

        etEnrollmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(etEnrollmentDate);
            }
        });
    }

    public void openDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                editText.setText(String.format("%d/%d/%d", day, month, year));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(CreateStudentActivity.this, R.color.green_primary));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(CreateStudentActivity.this, R.color.green_primary));
            }
        });
        dialog.show();
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