package com.phule.mtstudentinformationmanagement.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.Certificate;

import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.ViewHolder>{
    private List<Certificate> certificateList;
    private String studentCode;
    public CertificateAdapter(List<Certificate> certificateList, String studentCode) {
        this.certificateList = certificateList;
        this.studentCode = studentCode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Certificate certificate = certificateList.get(position);
        holder.tvCertiName.setText(certificate.getCertiName());
        holder.tvCertiScore.setText(String.valueOf(certificate.getCertiScore()));
        holder.tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.tvOption);
                popupMenu.getMenuInflater().inflate(R.menu.menu_item_option, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.menu_edit) {
                            // Edit action

                        }
                        else if(menuItem.getItemId() == R.id.menu_remove) {
                            // Remove action
                            new AlertDialog.Builder(view.getContext())
                                    .setTitle(view.getContext().getString(R.string.confirm_removal_title))
                                    .setMessage(view.getContext().getString(R.string.confirm_removal_msg_certificate))
                                    .setIcon(view.getContext().getDrawable(R.drawable.baseline_warning_24))
                                    .setPositiveButton(view.getContext().getString(R.string.confirm_removal_accept), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                            firebaseFirestore.collection("Students").whereEqualTo("code", studentCode).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot studentDoc : task.getResult()) {
                                                                    DocumentReference studentRef = studentDoc.getReference();
                                                                    studentRef.collection("Certificates")
                                                                            .whereEqualTo("certiName", certificate.getCertiName())
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        for (QueryDocumentSnapshot certificateDoc : task.getResult()) {
                                                                                            certificateDoc.getReference().delete();
                                                                                            Toast.makeText(view.getContext(), "Certificate removal successfully", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("removeCertificate", "Error getting documents.", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            } else {
                                                                Log.d("removeCertificate", "Error getting documents.", task.getException());
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .setNegativeButton(view.getContext().getString(R.string.confirm_removal_deny), null)
                                    .show();

                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return certificateList != null ? certificateList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCertiName, tvCertiScore, tvOption;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCertiName = itemView.findViewById(R.id.item_certificate_name);
            tvCertiScore = itemView.findViewById(R.id.item_certificate_score);
            tvOption = itemView.findViewById(R.id.item_tv_option);
        }
    }

    public void updateCertificatesList(List<Certificate> certificates) {
        certificateList = certificates;
        notifyDataSetChanged();
    }
}
