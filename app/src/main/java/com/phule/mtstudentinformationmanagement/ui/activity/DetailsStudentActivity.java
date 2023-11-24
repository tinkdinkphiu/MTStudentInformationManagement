package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private RecyclerView recyclerView;
    private String studentCode;
    private CertificateAdapter certificateAdapter;
    private FirebaseFirestore firebaseFirestore;
    private List<Certificate> certificateList;

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

        initUi();
        initRecyclerView();
        initFirebase();
        populateField(studentCode, name, birthday, address, gender, phone, enrollmentDate, major);
        initListener();
    }
    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        certificateAdapter = new CertificateAdapter(new ArrayList<>());
        recyclerView.setAdapter(certificateAdapter);
    }
    private void updateRecyclerView(List<Certificate> certificateList) {
        certificateAdapter.updateCertificatesList(certificateList);
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

        recyclerView = findViewById(R.id.recycler_view);
        certificateList = new ArrayList<>();
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
        firebaseFirestore.collection("Students").document(studentCode)
                .collection("Certificate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d("getCertificates", "No certificates found");
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Certificate certificate = document.toObject(Certificate.class);
                                certificateList.add(certificate);
                                Log.d("getCertificates", "Certificate: " + certificate.getCertiName());
                            }
                            certificateAdapter.updateCertificatesList(certificateList);
                        }
                    } else {
                        Log.d("getCertificates", "Error getting certificates: ", task.getException());
                    }
                });
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
                                                certificateList.add(certificate);
                                            }
                                            // Update RecyclerView Adapter
                                            certificateAdapter.updateCertificatesList(certificateList);
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
                        Log.w("Student", "Error querying student", task.getException());
                    }
                });
    }

}