package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.adapter.CertificateAdapter;
import com.phule.mtstudentinformationmanagement.data.model.Certificate;
import com.phule.mtstudentinformationmanagement.data.model.Student;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManageCertificateActivity extends AppCompatActivity {
    private String originalCode;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton, floatingActionButtonExport;
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
        floatingActionButtonExport = findViewById(R.id.fab_export);
    }

    private void initAdapter() {
        certificateList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        certificateAdapter = new CertificateAdapter(certificateList, originalCode);
        recyclerView.setAdapter(certificateAdapter);
    }

    private void initListener() {
        floatingActionButton.setOnClickListener(v -> {
            showPopupMenu(v);
        });
        floatingActionButtonExport.setOnClickListener(v -> {
            writeToFile();
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

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_add_student, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_add_student_manual) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManageCertificateActivity.this);
                    LayoutInflater inflater = ManageCertificateActivity.this.getLayoutInflater();
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
                                                            Toast.makeText(ManageCertificateActivity.this, "Certificate added to Student", Toast.LENGTH_SHORT).show();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(ManageCertificateActivity.this, "Error adding certificate", Toast.LENGTH_SHORT).show();
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
                } else if (menuItem.getItemId() == R.id.menu_add_student_import) {
                    // Import from csv
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            // Choosing csv file
                            Intent intent = new Intent();
                            intent.setType("*/*");
                            intent.putExtra(Intent.EXTRA_AUTO_LAUNCH_SINGLE_CHOICE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select CSV File "), 101);
                        } else {
                            // Getting permission from user
                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.fromParts("package", ManageCertificateActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            ManageCertificateActivity.this.startActivity(intent);
                        }
                    } else {
                        // For below android 11
                        Intent intent = new Intent();
                        intent.setType("*/*");
                        intent.putExtra(Intent.EXTRA_AUTO_LAUNCH_SINGLE_CHOICE, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        ActivityCompat.requestPermissions(ManageCertificateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                        startActivityForResult(Intent.createChooser(intent, "Select CSV File "), 101);
                    }
                }
                return true;
            }
        });

        popupMenu.show();
    }
    private Uri fileuri;
    private List<Certificate> importedCertificateList;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && data != null) {
            Log.d("FileRead", "onActivityResult");
            fileuri = data.getData();
            importedCertificateList = new ArrayList<>();
            importedCertificateList = readCSVFile(getFilePathFromUri(fileuri));

            for (Certificate certificate : importedCertificateList) {
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
                                                    Toast.makeText(ManageCertificateActivity.this, "Certificate added to Student", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(ManageCertificateActivity.this, "Error adding certificate", Toast.LENGTH_SHORT).show();
                                                    Log.w("FileRead", "Error adding certificate", e);
                                                });
                                    }
                                } else {
                                    Log.d("FileRead", "No student found with the provided originalCode");
                                }
                            } else {
                                Log.w("FileRead", "Error querying student", task.getException());
                            }
                        });
            }
        }
    }
    // Getting file path from Uri
    public String getFilePathFromUri(Uri uri) {
        String filePath = null;
        String scheme = uri.getScheme();
        Log.d("FileRead", "scheme: " + scheme);
        if (scheme != null && scheme.equals("content")) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = ManageCertificateActivity.this.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
                cursor.close();
            }
        } else if (scheme != null && scheme.equals("file")) {
            filePath = uri.getPath();
        }
        return filePath;
    }

    // Reading file data
    public List<Certificate> readCSVFile(String path) {
        List<Certificate> certificates = new ArrayList<>();
        if (path == null) {
            Log.e("FileRead", "File path is null");
            return certificates; // Return empty list
        }

        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file, "UTF-8");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splited = line.split(",");

                // Check if the split line has enough data for a Certificate object
                if (splited.length == 2) {
                    Certificate certificate = new Certificate();
                    certificate.setCertiName(splited[0]);
                    certificate.setCertiScore(splited[1]);

                    certificates.add(certificate);
                }
                else {
                    Toast.makeText(this, "Imported file format error", Toast.LENGTH_SHORT).show();
                    Log.e("FileRead", "Imported file format error");
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("FileRead", "File not found: " + path);
        }
        return certificates;
    }
    private void writeToFile() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, originalCode + "Certificates.csv");

        try {
            path.mkdirs();

            FileOutputStream stream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stream);

            outputStreamWriter.write("Name,Score\n");

            for(Certificate certificate : certificateList) {
                String[] row = {certificate.getCertiName(), certificate.getCertiScore()};

                String csvRow = TextUtils.join(",", row) + "\n";
                outputStreamWriter.write(csvRow);
            }

            outputStreamWriter.close();

            Toast.makeText(this, originalCode + "Certificates.csv written to DOWNLOAD successfully", Toast.LENGTH_SHORT).show();
            Log.d("CSVWriteFile", "Successfully writing to CSV file. File store at Download");
        } catch (IOException e) {
            Log.e("CSVWriteFile", "Error writing CSV file", e);

            Toast.makeText(this, "Error writing CSV file", Toast.LENGTH_SHORT).show();
        }
    }
}