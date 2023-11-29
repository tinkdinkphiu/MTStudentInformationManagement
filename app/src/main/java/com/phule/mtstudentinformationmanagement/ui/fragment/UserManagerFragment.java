package com.phule.mtstudentinformationmanagement.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.phule.mtstudentinformationmanagement.data.model.User;
import com.phule.mtstudentinformationmanagement.adapter.UserAdapter;
import com.phule.mtstudentinformationmanagement.ui.activity.CreateUserActivity;
import com.phule.mtstudentinformationmanagement.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class UserManagerFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String userRole;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_manager, container, false);
        initUi(view);
        initFirebase();
        initListener();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getCurrentFirebaseUser();

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);

        recyclerView.setAdapter(userAdapter);

        EventChangeListener();

        return view;
    }
    private void EventChangeListener() {
        firebaseFirestore.collection("Users")
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
                                    User addedUsers = dc.getDocument().toObject(User.class);
                                    userList.add(addedUsers);
                                    break;

                                default:
                                    // Handle other types if needed
                                    break;
                            }
                        }

                        // Notify the adapter of changes
                        userAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void initUi(View view) {
        floatingActionButton = view.findViewById(R.id.fab_add_user);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void initListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateUserActivity.class);
                startActivity(intent);
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
        return userRole.equals("Admin");
    }

    // ReloadAfterEditUser(2) - Pass intent to MainActivity
    public void receiveFromAdapter(Intent intent) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.editStudent(intent);
            mainActivity.setReturnToUserManagerFragment(true);
        }
    }
}