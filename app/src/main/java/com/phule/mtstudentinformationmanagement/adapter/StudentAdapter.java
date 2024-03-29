package com.phule.mtstudentinformationmanagement.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.Student;
import com.phule.mtstudentinformationmanagement.ui.activity.DetailsStudentActivity;
import com.phule.mtstudentinformationmanagement.ui.activity.EditStudentActivity;
import com.phule.mtstudentinformationmanagement.ui.fragment.StudentListFragment;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> implements Filterable {
    private static StudentAdapter instance;
    private List<Student> studentList;
    private StudentListFragment studentListFragment;

    public StudentAdapter(List<Student> studentList, StudentListFragment studentListFragment) {

        this.studentList = studentList;
        this.studentListFragment = studentListFragment;
        instance = this;
    }

    public static StudentAdapter getInstance() {
        return instance;
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
        holder.studentCode.setText(student.getCode());
        holder.studentName.setText(student.getName());
        holder.tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.tvOption);
                popupMenu.getMenuInflater().inflate(R.menu.menu_item_student_option, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.menu_details) {
                            // See details action
                            Intent intent = new Intent(view.getContext(), DetailsStudentActivity.class);
                            intent.putExtra("code", student.getCode());
                            intent.putExtra("name", student.getName());
                            intent.putExtra("birthday", student.getBirthday());
                            intent.putExtra("address", student.getAddress());
                            intent.putExtra("gender", student.getGender());
                            intent.putExtra("phone", student.getPhone());
                            intent.putExtra("enrollmentDate", student.getEnrollmentDate());
                            intent.putExtra("major", student.getMajor());
                            view.getContext().startActivity(intent);
                        }
                        else if (menuItem.getItemId() == R.id.menu_edit) {
                            if (studentListFragment.hasAuthority()) {
                                // Edit action
                                Intent intent = new Intent(view.getContext(), EditStudentActivity.class);
                                intent.putExtra("code", student.getCode());
                                intent.putExtra("name", student.getName());
                                intent.putExtra("birthday", student.getBirthday());
                                intent.putExtra("address", student.getAddress());
                                intent.putExtra("gender", student.getGender());
                                intent.putExtra("phone", student.getPhone());
                                intent.putExtra("enrollmentDate", student.getEnrollmentDate());
                                intent.putExtra("major", student.getMajor());
                                // ReloadAfterEditStudent(1) - Pass intent to StudentListFragment
                                studentListFragment.receiveFromAdapter(intent);
                            } else {
                                Toast.makeText(view.getContext(), "You don't have the authority to do this action", Toast.LENGTH_SHORT).show();
                            }
                        } else if (menuItem.getItemId() == R.id.menu_remove) {
                            if (studentListFragment.hasAuthority()) {
                                // Remove action
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle(view.getContext().getString(R.string.confirm_removal_title))
                                        .setMessage(view.getContext().getString(R.string.confirm_removal_msg))
                                        .setIcon(view.getContext().getDrawable(R.drawable.baseline_warning_24))
                                        .setPositiveButton(view.getContext().getString(R.string.confirm_removal_accept), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // Remove student from Firestore
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("Students").whereEqualTo("code", student.getCode()).get()
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                if (task.getResult().isEmpty()) {
                                                                    Log.d("removeStudent", "No matching documents found");
                                                                    Toast.makeText(view.getContext(), "No matching documents found", Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    String documentId = document.getId();

                                                                    db.collection("Students").document(documentId)
                                                                            .delete()
                                                                            .addOnSuccessListener(aVoid -> {
                                                                                int position = holder.getAdapterPosition();
                                                                                studentList.remove(position);
                                                                                notifyItemRemoved(position);
                                                                                Toast.makeText(view.getContext(), "Student removed", Toast.LENGTH_SHORT).show();
                                                                                Log.d("removeStudent", "Student removed successfully: " + student.getCode());
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                // Handle Firestore deletion failure
                                                                                Toast.makeText(view.getContext(), "Error removing student", Toast.LENGTH_SHORT).show();
                                                                                Log.d("removeStudent", "Student removed failed: " + student.getCode());
                                                                            });
                                                                }
                                                            } else {
                                                                Log.d("removeStudent", "Failed to find code");
                                                                Toast.makeText(view.getContext(), "Failed to find student code", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(e -> {
                                                            Toast.makeText(view.getContext(), "Error fetching documents", Toast.LENGTH_SHORT).show();
                                                            Log.d("removeStudent", "Failed to fetch documents: " + e.getMessage());
                                                        });
                                            }
                                        })
                                        .setNegativeButton(view.getContext().getString(R.string.confirm_removal_deny), null)
                                        .show();
                            } else {
                                Toast.makeText(view.getContext(), "You don't have the authority to do this action", Toast.LENGTH_SHORT).show();
                            }
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
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView studentCode, studentName, tvOption;

        public ViewHolder(View itemView) {
            super(itemView);
            studentCode = itemView.findViewById(R.id.item_student_id);
            studentName = itemView.findViewById(R.id.item_student_name);
            tvOption = itemView.findViewById(R.id.item_tv_option);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Student> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    // Return all students if == null or empty
                    filteredList.addAll(studentList);
                } else {
                    // Perform search
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Student item : studentList) {
                        if (item.getCode().toLowerCase().contains(filterPattern) ||
                                item.getName().toLowerCase().contains(filterPattern) ||
                                item.getBirthday().toLowerCase().contains(filterPattern) ||
                                item.getAddress().toLowerCase().contains(filterPattern) ||
                                item.getGender().toLowerCase().contains(filterPattern) ||
                                item.getPhone().toLowerCase().contains(filterPattern) ||
                                item.getEnrollmentDate().toLowerCase().contains(filterPattern) ||
                                item.getMajor().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                studentList.clear();
                studentList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
