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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.User;
import com.phule.mtstudentinformationmanagement.helper.DialogHelper;
import com.squareup.picasso.Picasso;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
/*import com.phule.mtstudentinformationmanagement.ui.activity.ChangeAvatarActivity;*/

public class ProfileFragment extends Fragment {
    private final int GALLERY_REQUEST_CODE = 1000;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private TextInputLayout ilEmail, ilName, ilAge, ilPhone, ilStatus, ilRole;
    private TextInputEditText etEmail, etName, etAge, etPhone, etStatus, etRole;
    private ImageView btnAvatar;
    private Button btnDone;
    private String originalEmail;
    private DialogHelper dialogHelper;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dialogHelper = new DialogHelper(requireContext());
        initialFirebase();
        initUi(view);

        initListener();

        getUserProfileImage(btnAvatar);

        return view;
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
    }

    private void initUi(View view) {
        ilEmail = view.findViewById(R.id.il_email);
        ilName = view.findViewById(R.id.il_name);
        ilAge = view.findViewById(R.id.il_age);
        ilPhone = view.findViewById(R.id.il_phone);
        ilStatus = view.findViewById(R.id.il_status);
        ilRole = view.findViewById(R.id.il_role);

        etEmail = view.findViewById(R.id.et_email);
        etName = view.findViewById(R.id.et_name);
        etAge = view.findViewById(R.id.et_age);
        etPhone = view.findViewById(R.id.et_phone);
        etStatus = view.findViewById(R.id.et_status);
        etRole = view.findViewById(R.id.et_role);

        btnAvatar = view.findViewById(R.id.btnAvatar);
        btnDone = view.findViewById(R.id.btnDone);
    }

    private void populateField(String email, String name, String age, String phone, String status, String role) {
        etEmail.setText(email);
        etName.setText(name);
        etAge.setText(age);
        etPhone.setText(phone);
        etStatus.setText(status);
        etRole.setText(role);
    }

    public void getUserProfileImage(ImageView imageView) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + firebaseUser.getUid() + "/profile_picture.jpg");
        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // The download URL of the image
                        String imageUrl = uri.toString();

                        // Load the image into the CircleImageView
                        Picasso.get().load(imageUrl).into(imageView);
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
}