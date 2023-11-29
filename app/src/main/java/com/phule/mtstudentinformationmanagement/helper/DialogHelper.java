package com.phule.mtstudentinformationmanagement.helper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.phule.mtstudentinformationmanagement.R;

import java.util.Calendar;

public class DialogHelper {
    private Context context;

    public DialogHelper(Context context) {
        this.context = context;
    }

    public void openDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(context, R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                editText.setText(String.format("%d/%d/%d", day, month + 1, year));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        // Set the maximum date to the current date
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.green_primary));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.green_primary));
            }
        });
        dialog.show();
    }

    public void openGenderDialog(final TextInputEditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Gender");
        final String[] genderOptions = {"Male", "Female"};
        builder.setItems(genderOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.setText(genderOptions[which]);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void openMajorDialog(final TextInputEditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Major");
        final String[] majorOptions = {
                "IT",
                "Marketing",
                "Finance",
                "Engineering",
                "Healthcare",
                "Education",
                "Business",
                "Art and Design",
                "Social Sciences",
                "Science",
                "Architecture",
                "Culinary Arts",
                "Environmental Science",
                "Psychology",
                "Communication",
                "Nursing",
                "Music",
                "Mathematics",
                "History"
        };
        builder.setItems(majorOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.setText(majorOptions[which]);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void openStatusDialog(final TextInputEditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Status");
        final String[] statusOptions = {"Normal", "Locked"};
        builder.setItems(statusOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.setText(statusOptions[which]);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public void openRoleDialog(final TextInputEditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Role");
        final String[] statusOptions = {"Admin", "Manager", "Employee"};
        builder.setItems(statusOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.setText(statusOptions[which]);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child(DialogHelper.currentUserID());
    }
    private static String currentUserID() {
        return FirebaseAuth.getInstance().getUid();
    }
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserID());
    }
}

