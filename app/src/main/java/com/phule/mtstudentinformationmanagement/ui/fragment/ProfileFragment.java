package com.phule.mtstudentinformationmanagement.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.User;
import com.phule.mtstudentinformationmanagement.helper.DialogHelper;
import com.phule.mtstudentinformationmanagement.helper.FieldValidator;
import com.phule.mtstudentinformationmanagement.ui.activity.EditUserActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
/*import com.phule.mtstudentinformationmanagement.ui.activity.ChangeAvatarActivity;*/

public class ProfileFragment extends Fragment {
    private final int GALLERY_REQUEST_CODE = 1000;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private TextInputEditText etEmail, etName, etAge, etPhone, etStatus, etRole;
    private ImageView btnAvatar;
    private Button btnSave;
    private FieldValidator fieldValidator;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initialFirebase();
        initUi(view);
        initListener();

        getUserInfo();
        getUserProfileImage();

        fieldValidator = new FieldValidator(getContext());

        return view;
    }
    private void initUi(View view) {
        etEmail = view.findViewById(R.id.et_email);
        etName = view.findViewById(R.id.et_name);
        etAge = view.findViewById(R.id.et_age);
        etPhone = view.findViewById(R.id.et_phone);
        etStatus = view.findViewById(R.id.et_status);
        etRole = view.findViewById(R.id.et_role);

        btnAvatar = view.findViewById(R.id.btnAvatar);
        btnSave = view.findViewById(R.id.btn_save);
    }
    private void initialFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initListener() {
        btnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidField()) {
                    onSaveClick();
                }
            }
        });
    }
    private void onSaveClick() {
        String userEmail = firebaseUser.getEmail();
        String editedName = etName.getText().toString();
        String editedAge = etAge.getText().toString();
        String editedPhone = etPhone.getText().toString();
        String status = etStatus.getText().toString();
        String role = etRole.getText().toString();

        firebaseFirestore.collection("Users").whereEqualTo("email", userEmail).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();

                            Map<String, Object> updatedUser = new HashMap<>();
                            updatedUser.put("email", userEmail);
                            updatedUser.put("name", editedName);
                            updatedUser.put("age", editedAge);
                            updatedUser.put("phone", editedPhone);
                            updatedUser.put("status", status);
                            updatedUser.put("role", role);

                            firebaseFirestore.collection("Users").document(documentId)
                                    .update(updatedUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Information updated", Toast.LENGTH_SHORT).show();
                                        Log.d("updateUser", "User updated successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error updating information", Toast.LENGTH_SHORT).show();
                                        Log.d("updateUser", "Error updating information");
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to find user", Toast.LENGTH_SHORT).show();
                        Log.d("updateUser", "Failed to find user");
                    }
                });
    }
    private void getUserInfo() {
        if(firebaseUser != null) {
            String userEmail = firebaseUser.getEmail();
            firebaseFirestore.collection("Users").whereEqualTo("email", userEmail).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty()) {
                                User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                                populateField(user.getEmail(), user.getName(), user.getAge(), user.getPhone(), user.getStatus(), user.getRole());
                            }
                            else {
                                Log.d("fetchingUser", "No user found");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("fetchingUser", "Error getting user details: ", e);
                        }
                    });
        }
    }

    private void populateField(String email, String name, String age, String phone, String status, String role) {
        etEmail.setText(email);
        etName.setText(name);
        etAge.setText(age);
        etPhone.setText(phone);
        etStatus.setText(status);
        etRole.setText(role);
    }

    public void getUserProfileImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + firebaseUser.getUid() + "/profile_picture.jpg");
        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // The download URL of the image
                        String imageUrl = uri.toString();

                        // Load the image into the CircleImageView
                        Picasso.get().load(imageUrl).into(btnAvatar);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            if (firebaseUser != null) {
                String userId = firebaseUser.getUid();

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                StorageReference fileRef = storageRef.child("images/" + userId + "/profile_picture.jpg");

                fileRef.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get the download URL of the uploaded file
                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getContext()).load(uri).into(btnAvatar);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
    private boolean isValidField() {
        if (!fieldValidator.isValidName(etName.getText().toString())) {
            Toast.makeText(getContext(), "Invalid name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidIntegerField(etAge.getText().toString())) {
            Toast.makeText(getContext(), "Invalid age", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!fieldValidator.isValidPhone(etPhone.getText().toString())) {
            Toast.makeText(getContext(), "Phone must be number and 10 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}