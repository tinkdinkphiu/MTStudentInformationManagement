package com.phule.mtstudentinformationmanagement.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.phule.mtstudentinformationmanagement.R;

public class ChangeAvatarActivity extends AppCompatActivity {
    Button btnCapture, btnGalery, btnHome;
    private final int GALLERY_REQ_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);
        btnHome = findViewById(R.id.btnHome);
        btnCapture = findViewById(R.id.btnCapture);
        btnGalery = findViewById(R.id.btnGalery);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangeAvatarActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*hehehehe*/
                Toast.makeText(ChangeAvatarActivity.this, "Capture", Toast.LENGTH_SHORT).show();
            }
        });

        btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent img = new Intent(Intent.ACTION_PICK);
                img.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(img, GALLERY_REQ_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==GALLERY_REQ_CODE){

            }
        }
    }
}