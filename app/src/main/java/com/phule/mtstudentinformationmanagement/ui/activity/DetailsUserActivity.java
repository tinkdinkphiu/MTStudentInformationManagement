package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.phule.mtstudentinformationmanagement.R;

import java.util.Date;

public class DetailsUserActivity extends AppCompatActivity {
    private TextView tvEmail, tvName, tvAge, tvPhone, tvRole, tvStatus;
    private LinearLayout linearLoginHistory;
    private String userEmail;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_user);

        initFirebase();
        initUi();

        Intent intent = getIntent();

        userEmail = intent.getStringExtra("email");
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        String phone = intent.getStringExtra("phone");
        String role = intent.getStringExtra("role");
        String status = intent.getStringExtra("status");

        populateField(userEmail, name, age, phone, role, status);
    }

    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initUi() {
        tvEmail = findViewById(R.id.tv_email);
        tvName = findViewById(R.id.tv_name);
        tvAge = findViewById(R.id.tv_age);
        tvPhone = findViewById(R.id.tv_phone);
        tvRole = findViewById(R.id.tv_role);
        tvStatus = findViewById(R.id.tv_status);

        linearLoginHistory = findViewById(R.id.linear_login_history);
    }

    private void populateField(String email, String name, String age, String phone, String role, String status) {
        tvEmail.setText(email);
        tvName.setText(name);
        tvAge.setText(age);
        tvPhone.setText(phone);
        tvRole.setText(role);
        tvStatus.setText(status);

        getLoginHistory();
    }

    private void getLoginHistory() {
        firebaseFirestore.collection("Users").whereEqualTo("email", userEmail).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot userSnapshot = task.getResult();
                        if(userSnapshot != null && !userSnapshot.isEmpty()) {
                            for (DocumentSnapshot userDocument : userSnapshot.getDocuments()) {
                                CollectionReference loginHistoryCollection = userDocument.getReference().collection("LoginHistory");
                                loginHistoryCollection.get().addOnCompleteListener(loginHistoryTask -> {
                                    if (loginHistoryTask.isSuccessful()) {
                                        QuerySnapshot loginHistorySnapshot = loginHistoryTask.getResult();
                                        if (loginHistorySnapshot != null && !loginHistorySnapshot.isEmpty()) {
                                            for (DocumentSnapshot loginHistoryDocument : loginHistorySnapshot.getDocuments()) {
                                                Date timestamp = loginHistoryDocument.getDate("timestamp");

                                                TextView textView = new TextView(DetailsUserActivity.this);
                                                textView.setText(timestamp.toString());
                                                linearLoginHistory.addView(textView);
                                            }
                                        } else {
                                            Log.d("fetchHistory", "No login history found for this user");
                                        }
                                    } else {
                                        Log.w("fetchHistory", "Error querying login history", loginHistoryTask.getException());
                                    }
                                });
                            }
                        } else {
                            Log.d("fetchHistory", "No user found with the provided email");
                        }
                    } else {
                        Log.w("fetchHistory", "Error querying user ", task.getException());
                    }
                });

    }
}