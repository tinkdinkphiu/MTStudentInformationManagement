package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.adapter.CertificateAdapter;
import com.phule.mtstudentinformationmanagement.data.model.Certificate;

import java.util.ArrayList;
import java.util.List;

public class ManageCertificateActivity extends AppCompatActivity {
    private String originalCode;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private CertificateAdapter certificateAdapter;
    private List<Certificate> certificateList;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_certificate);

        Intent intent = getIntent();
        originalCode = intent.getStringExtra("originalCode");

        initFirebase();
        initUi();
        initAdapter();
        initListener();

        getCertificates();
    }

    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    private void initUi() {
        recyclerView = findViewById(R.id.recycler_view);
        floatingActionButton = findViewById(R.id.fab_add_certificate);
    }
    private void initAdapter() {
        certificateList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        certificateAdapter = new CertificateAdapter(certificateList, originalCode);
        recyclerView.setAdapter(certificateAdapter);
    }
    private void initListener() {
        floatingActionButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_add_certificate, null);

            TextInputEditText etCertiName = view.findViewById(R.id.et_certificate_name);
            TextInputEditText etCertiScore = view.findViewById(R.id.et_certificate_score);

            builder.setView(view);


            builder.setPositiveButton("Add", (dialog, which) -> {
                String name = etCertiName.getText().toString();
                String score = etCertiScore.getText().toString();

                Certificate certificate = new Certificate(name, score);
                firebaseFirestore.collection("Students").whereEqualTo("code", originalCode).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot studentsSnapshot = task.getResult();
                                if (studentsSnapshot != null && !studentsSnapshot.isEmpty()) {
                                    for (DocumentSnapshot studentDocument : studentsSnapshot.getDocuments()) {
                                        studentDocument.getReference()
                                                .collection("Certificates")
                                                .add(certificate)
                                                .addOnSuccessListener(documentReference -> {
                                                    Toast.makeText(this, "Certificate added to Student", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(this, "Error adding certificate", Toast.LENGTH_SHORT).show();
                                                    Log.w("Certificates", "Error adding certificate", e);
                                                });
                                    }
                                } else {
                                    Log.d("Student", "No student found with the provided originalCode");
                                }
                            } else {
                                Log.w("Student", "Error querying student", task.getException());
                            }
                        });


                dialog.dismiss();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });
    }

    private void getCertificates() {
        firebaseFirestore.collection("Students").whereEqualTo("code", originalCode).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot studentsSnapshot = task.getResult();
                        if (studentsSnapshot != null && !studentsSnapshot.isEmpty()) {
                            for (DocumentSnapshot studentDocument : studentsSnapshot.getDocuments()) {
                                CollectionReference certificatesCollection = studentDocument.getReference().collection("Certificates");

                                certificatesCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
                                    if (e != null) {
                                        Log.w("Certificates", "Listen failed.", e);
                                        return;
                                    }

                                    if (queryDocumentSnapshots != null) {
                                        certificateList.clear();
                                        for (DocumentSnapshot certificateDocument : queryDocumentSnapshots.getDocuments()) {
                                            Certificate certificate = certificateDocument.toObject(Certificate.class);
                                            certificateList.add(certificate);
                                        }
                                        certificateAdapter.updateCertificatesList(certificateList);
                                    }
                                });
                            }
                        } else {
                            Log.d("Student", "No student found with the provided originalCode");
                        }
                    } else {
                        Log.w("Student", "Error querying student", task.getException());
                    }
                });
    }

}