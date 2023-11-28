package com.phule.mtstudentinformationmanagement.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.User;
import com.phule.mtstudentinformationmanagement.helper.DialogHelper;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
/*import com.phule.mtstudentinformationmanagement.ui.activity.ChangeAvatarActivity;*/

public class ProfileFragment extends Fragment {
    private final int GALLERY_REQ_CODE = 1000;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private TextInputLayout ilEmail, ilName, ilAge, ilPhone, ilStatus, ilRole;
    private TextInputEditText etEmail, etName, etAge, etPhone, etStatus, etRole;
    private ImageView btnAvatar;
    private AppCompatButton btnDone;
    private String originalEmail;
    private DialogHelper dialogHelper;
    ActivityResultLauncher<Intent>  imagePickLaucher;
    Uri selectedImageUri;
    User currentUser;
    ProgressBar progressBar;

    public ProfileFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLaucher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            selectedImageUri = data.getData();
                            DialogHelper.setProfilePic(getContext(), selectedImageUri, btnAvatar);
                        }
                    }
                }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dialogHelper = new DialogHelper(requireContext());
        initialFirebase();
        initUi(view);

        Bundle args = getArguments();
        if (args != null) {
            originalEmail = args.getString("email", "1");
            String name = args.getString("name", "2");
            String age = args.getString("age", "3");
            String phone = args.getString("phone", "4");
            String status = args.getString("status", "5");
            String role = args.getString("role", "6");

            populateField(originalEmail, name, age, phone, status, role);
        }
        initUi(view);
        btnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent img = new Intent(Intent.ACTION_PICK);
                img.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(img, GALLERY_REQ_CODE);
            }
        });

        btnAvatar.setOnClickListener((view1 -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLaucher.launch(intent);
                            return null;
                        }
                    });
        }));

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedImageUri != null){
                    DialogHelper.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                            .addOnCompleteListener(task -> {
                               updateToFirestore();
                            });
                }else{
                    updateToFirestore();
                }
            }
        });

        return view;
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==GALLERY_REQ_CODE){
                btnAvatar.setImageURI(data.getData());
            }
        }
    }*/

    private void initialFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
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
    private void updateToFirestore(){
        DialogHelper.currentUserDetails().set(currentUser)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Thanh cong", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "That bai", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

private void getUserData(){
        DialogHelper.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        DialogHelper.setProfilePic(getContext(), uri, btnAvatar);
                    }
                });
}
}