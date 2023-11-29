package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.Certificate;
import com.phule.mtstudentinformationmanagement.helper.DialogHelper;
import com.phule.mtstudentinformationmanagement.helper.FieldValidator;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditStudentActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private TextInputEditText etCode, etName, etBirthday, etAddress, etGender, etPhone, etEnrollmentDate, etMajor;
    private TextView tvClose;
    private Button btnSave, btnCertificate;
    private String originalCode;
    private DialogHelper dialogHelper;
    private FieldValidator fieldValidator;

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

        dialogHelper = new DialogHelper(this);
        fieldValidator = new FieldValidator(this);

        initialFirebase();
        initUi();

        populateField(originalCode, name, birthday, address, gender, phone, enrollmentDate, major);

        initListener();
    }

    private void initialFirebase() {
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

        btnSave = findViewById(R.id.btn_save);
        btnCertificate = findViewById(R.id.btn_certificate);
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
        btnCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditStudentActivity.this, ManageCertificateActivity.class);
                intent.putExtra("originalCode", originalCode);
                startActivity(intent);
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
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void onSaveClick() {
        if(isValidField()) {
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
                                            // ReloadAfterEditStudent(4) - Return result to MainActivity
                                            setResult(Activity.RESULT_OK);
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
    private boolean isValidField() {
        if (!fieldValidator.isValidCode(etCode.getText().toString())) {
            Toast.makeText(this, "Code must be number and 8 character", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidName(etName.getText().toString())) {
            Toast.makeText(this, "Name must not contain special character or number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidTextField(etAddress.getText().toString())) {
            Toast.makeText(this, "Address is empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidPhone(etPhone.getText().toString())) {
            Toast.makeText(this, "Phone must be number and 10 character", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}