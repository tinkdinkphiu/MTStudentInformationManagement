package com.phule.mtstudentinformationmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.Certificate;

import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.ViewHolder>{
    private List<Certificate> certificateList;
    public CertificateAdapter(List<Certificate> certificateList) {
        this.certificateList = certificateList;
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
    }

    @Override
    public int getItemCount() {
        return certificateList != null ? certificateList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCertiName, tvCertiScore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCertiName = itemView.findViewById(R.id.item_certificate_name);
            tvCertiScore = itemView.findViewById(R.id.item_certificate_score);
        }
    }

    public void updateCertificatesList(List<Certificate> certificates) {
        certificateList = certificates;
        notifyDataSetChanged();
    }
}
