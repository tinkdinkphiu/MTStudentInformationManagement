package com.phule.mtstudentinformationmanagement.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.helper.DialogHelper;
import com.phule.mtstudentinformationmanagement.ui.activity.ChangeAvatarActivity;

public class ProfileFragment extends Fragment {
    private final int GALLERY_REQ_CODE = 1000;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private TextInputLayout ilEmail, ilName, ilAge, ilPhone, ilStatus, ilRole;
    private TextInputEditText etEmail, etName, etAge, etPhone, etStatus, etRole;
    private ImageView btnAvatar;
    private String originalEmail;
    private DialogHelper dialogHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==GALLERY_REQ_CODE){
                btnAvatar.setImageURI(data.getData());
            }
        }
    }

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
    }
    private void populateField(String email, String name, String age, String phone, String status, String role) {
        etEmail.setText(email);
        etName.setText(name);
        etAge.setText(age);
        etPhone.setText(phone);
        etStatus.setText(status);
        etRole.setText(role);
    }
    public static ProfileFragment newInstance(String email, String name, String age, String phone, String status, String role) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("name", name);
        bundle.putString("age", age);
        bundle.putString("phone", phone);
        bundle.putString("status", status);
        bundle.putString("role", role);

        fragment.setArguments(bundle);

        return fragment;
    }
}