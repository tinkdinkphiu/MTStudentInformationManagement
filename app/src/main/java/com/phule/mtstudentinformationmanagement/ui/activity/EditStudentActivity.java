package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.phule.mtstudentinformationmanagement.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditStudentActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
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

        etBirthday.setShowSoftInputOnFocus(false);
        etEnrollmentDate.setShowSoftInputOnFocus(false);

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
        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog(etBirthday);
            }
        });
        etEnrollmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog(etEnrollmentDate);
            }
        });
        etGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGenderDialog(etGender);
            }
        });
        etMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMajorDialog(etMajor);
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

    private void openDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                editText.setText(String.format("%d/%d/%d", day, month, year));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        // Set the maximum date to the current date
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(EditStudentActivity.this, R.color.green_primary));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(EditStudentActivity.this, R.color.green_primary));
            }
        });
        dialog.show();
    }

    private void openGenderDialog(final TextInputEditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender");
        final String[] genderOptions = {"Male", "Female"};
        builder.setItems(genderOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.setText(genderOptions[which]);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void openMajorDialog(final TextInputEditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Major");
        final String[] majorOptions = {
                "Accounting",
                "Applied Mathematics",
                "Architecture",
                "Biotechnology",
                "Business Administration",
                "Business Administration - Major in Marketing Management",
                "Business Administration - Major in Restaurant Management",
                "Chemical Engineering",
                "Chinese - Major in Chinese",
                "Chinese - Major in Chinese-English",
                "Civil Engineering",
                "Computer Network and Data Communication",
                "Computer Science",
                "Control and Automation Engineering",
                "Electronic - Communication Engineering",
                "Electrical Engineering",
                "English",
                "Environmental Science",
                "Environmental Technology",
                "Fashion Design",
                "Finance - Banking",
                "Graphic Design",
                "Industrial Design",
                "Interior Design",
                "International Business",
                "Labor Protection",
                "Labor Relations",
                "Law",
                "Pharmacy",
                "Social Work",
                "Software Engineering",
                "Sociology",
                "Sports Management - Major in Sports Business and Event Organization",
                "Statistics",
                "Transportation Engineering",
                "Urban Planning",
                "Vietnamese Studies - Major in Tourism and Tourism Management",
                "Vietnamese Studies - Major in Tourism and Travel",
                "Vietnamese Studies - Major in Vietnamese Language"
        };
        builder.setItems(majorOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.setText(majorOptions[which]);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}