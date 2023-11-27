package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.adapter.CertificateAdapter;
import com.phule.mtstudentinformationmanagement.data.model.Certificate;

import java.util.ArrayList;
import java.util.List;

public class DetailsStudentActivity extends AppCompatActivity {
    private TextView tvCode, tvName, tvBirthday, tvGender, tvAddress, tvPhone, tvEnrollmentDate, tvMajor, tvClose;
    private String studentCode;
    private FirebaseFirestore firebaseFirestore;
    private LinearLayout linearCertificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_student);

        Intent intent = getIntent();

        studentCode = intent.getStringExtra("code");
        String name = intent.getStringExtra("name");
        String birthday = intent.getStringExtra("birthday");
        String address = intent.getStringExtra("address");
        String gender = intent.getStringExtra("gender");
        String phone = intent.getStringExtra("phone");
        String enrollmentDate = intent.getStringExtra("enrollmentDate");
        String major = intent.getStringExtra("major");

        initFirebase();
        initUi();
        populateField(studentCode, name, birthday, address, gender, phone, enrollmentDate, major);
        initListener();
    }
    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
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
        tvClose = findViewById(R.id.tv_close);
        linearCertificate = findViewById(R.id.linear_certificate);
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

        getCertificates();
    }
    private void initListener() {
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void getCertificates() {
        firebaseFirestore.collection("Students").whereEqualTo("code", studentCode).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot studentsSnapshot = task.getResult();
                        if (studentsSnapshot != null && !studentsSnapshot.isEmpty()) {
                            for (DocumentSnapshot studentDocument : studentsSnapshot.getDocuments()) {
                                CollectionReference certificatesCollection = studentDocument.getReference().collection("Certificates");
                                certificatesCollection.get().addOnCompleteListener(certificatesTask -> {
                                    if (certificatesTask.isSuccessful()) {
                                        QuerySnapshot certificatesSnapshot = certificatesTask.getResult();
                                        if (certificatesSnapshot != null && !certificatesSnapshot.isEmpty()) {
                                            for (DocumentSnapshot certificateDocument : certificatesSnapshot.getDocuments()) {
                                                Certificate certificate = certificateDocument.toObject(Certificate.class);
                                                addCertificateToLinearLayout(certificate);
                                            }
                                        } else {
                                            Log.d("Certificates", "No certificates found for this student");
                                        }
                                    } else {
                                        Log.w("Certificates", "Error querying certificates", certificatesTask.getException());
                                    }
                                });
                            }
                        } else {
                            Log.d("Student", "No student found with the provided code");
                        }
                    } else {
                        Log.w("Student", "Error querying student ", task.getException());
                    }
                });
    }
    private void addCertificateToLinearLayout(Certificate certificate) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textViewName = new TextView(this);
        textViewName.setText(certificate.getCertiName());
        textViewName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView textViewScore = new TextView(this);
        textViewScore.setText(String.valueOf(certificate.getCertiScore()));
        textViewScore.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        linearLayout.addView(textViewName);
        linearLayout.addView(textViewScore);

        linearCertificate.addView(linearLayout);
    }
}