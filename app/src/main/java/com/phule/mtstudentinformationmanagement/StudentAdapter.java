package com.phule.mtstudentinformationmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    private List<Student> studentList;
    public StudentAdapter(List<Student> studentList) {

        this.studentList = studentList;
    }
    @NonNull
    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.studentId.setText(student.getId());
        holder.studentName.setText(student.getName());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView studentId;
        public TextView studentName;
        public ViewHolder(View itemView) {
            super(itemView);
            studentId = itemView.findViewById(R.id.item_student_id);
            studentName = itemView.findViewById(R.id.item_student_name);
        }
    }
}
