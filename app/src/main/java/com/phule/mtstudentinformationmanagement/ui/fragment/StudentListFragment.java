package com.phule.mtstudentinformationmanagement.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.Student;
import com.phule.mtstudentinformationmanagement.adapter.StudentAdapter;
import com.phule.mtstudentinformationmanagement.ui.activity.CreateStudentActivity;
import com.phule.mtstudentinformationmanagement.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class StudentListFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String userRole;
    private RecyclerView recyclerView;
    private List<Student> studentList;
    private List<Student> originalStudentList;
    private StudentAdapter adapter;
    private FloatingActionButton floatingActionButton;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        initUi(view);
        initFirebase();
        initListener();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getCurrentFirebaseUser();

        studentList = new ArrayList<>();
        originalStudentList = new ArrayList<>();

        // Add all students to originalStudentList
        for (Student student : studentList) {
            originalStudentList.add(student);
        }

        adapter = new StudentAdapter(studentList, this);
        recyclerView.setAdapter(adapter);

        EventChangeListener();

        return view;
    }


    private void EventChangeListener() {
        firebaseFirestore.collection("Students")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d("Firestore", "Firestore error");
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Student addedStudent = dc.getDocument().toObject(Student.class);
                                    studentList.add(addedStudent);
                                    originalStudentList.add(addedStudent);
                                    break;

                                default:
                                    // Handle other types if needed
                                    break;
                            }
                        }

                        // Notify the adapter of changes
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void initUi(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        floatingActionButton = view.findViewById(R.id.fab_add_student);
        searchView = view.findViewById(R.id.search_view);
    }

    private void initListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasAuthority()) {
                    Intent intent = new Intent(getActivity(), CreateStudentActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(), "You don't have the authority to do this action", Toast.LENGTH_SHORT).show();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchString) {
                if (searchString.isEmpty()) {
                    studentList.clear();
                    studentList.addAll(originalStudentList);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.getFilter().filter(searchString);
                }
                return false;
            }
        });
    }

    private void getCurrentFirebaseUser() {
        DocumentReference df = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    userRole = documentSnapshot.getString("role");
                    Log.d("getUserRole", "Get user role Succeeded: " + userRole);
                }
                else {
                    Log.d("getUserRole", "Get user role Failed");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("getUserRole", "Document Reference failed");
            }
        });
    }
    
    public boolean hasAuthority() {
        return userRole.equals("Admin") || userRole.equals("Manager");
    }

    // ReloadAfterEditStudent(2) - Pass intent to MainActivity
    public void receiveFromAdapter(Intent intent) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.editStudent(intent);
        }
    }
}