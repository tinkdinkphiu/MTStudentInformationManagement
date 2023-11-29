package com.phule.mtstudentinformationmanagement.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.phule.mtstudentinformationmanagement.R;
import com.phule.mtstudentinformationmanagement.data.model.Student;
import com.phule.mtstudentinformationmanagement.adapter.StudentAdapter;
import com.phule.mtstudentinformationmanagement.ui.activity.CreateStudentActivity;
import com.phule.mtstudentinformationmanagement.ui.activity.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class StudentListFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String userRole;
    private RecyclerView recyclerView;
    private List<Student> studentList;
    private List<Student> originalStudentList;
    private StudentAdapter adapter;
    private FloatingActionButton floatingActionButton, floatingActionButtonExport;
    private SearchView searchView;
    private TextView tvOption;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        initUi(view);
        initFirebase();
        initListener();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getCurrentFirebaseUser();

        studentList = new ArrayList<>();
        originalStudentList = new ArrayList<>();

        // Add all students to originalStudentList
        for (Student student : studentList) {
            originalStudentList.add(student);
        }

        adapter = new StudentAdapter(studentList, this);
        recyclerView.setAdapter(adapter);

        EventChangeListener();

        return view;
    }


    private void EventChangeListener() {
        firebaseFirestore.collection("Students")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d("Firestore", "Firestore error");
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Student addedStudent = dc.getDocument().toObject(Student.class);
                                    studentList.add(addedStudent);
                                    originalStudentList.add(addedStudent);
                                    break;

                                default:
                                    // Handle other types if needed
                                    break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void initUi(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        floatingActionButton = view.findViewById(R.id.fab_add_student);
        floatingActionButtonExport = view.findViewById(R.id.fab_export);
        searchView = view.findViewById(R.id.search_view);
        tvOption = view.findViewById(R.id.item_tv_option);
    }

    private void initListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasAuthority()) {
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_add_student, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.menu_add_student_manual) {
                                // Add manually
                                Intent intent = new Intent(getActivity(), CreateStudentActivity.class);
                                startActivity(intent);
                            } else if (menuItem.getItemId() == R.id.menu_add_student_import) {
                                // Import from csv
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    if (Environment.isExternalStorageManager()) {
                                        // Choosing csv file
                                        Intent intent = new Intent();
                                        intent.setType("*/*");
                                        intent.putExtra(Intent.EXTRA_AUTO_LAUNCH_SINGLE_CHOICE, true);
                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(Intent.createChooser(intent, "Select CSV File "), 101);
                                    } else {
                                        // Getting permission from user
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        getActivity().startActivity(intent);
                                    }
                                } else {
                                    // For below android 11
                                    Intent intent = new Intent();
                                    intent.setType("*/*");
                                    intent.putExtra(Intent.EXTRA_AUTO_LAUNCH_SINGLE_CHOICE, true);
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                                    startActivityForResult(Intent.createChooser(intent, "Select CSV File "), 101);
                                }
                            }
                            return true;
                        }
                    });

                    popupMenu.show();
                } else {
                    Toast.makeText(getContext(), "You don't have the authority to do this action", Toast.LENGTH_SHORT).show();
                }
            }
        });
        floatingActionButtonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeToFile();
            }
        });
        tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_student_sort, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.menu_sort_code_asc) {
                            Collections.sort(studentList, Student.studentCodeASCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_code_desc) {
                            Collections.sort(studentList, Student.studentCodeDESCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_name_asc) {
                            Collections.sort(studentList, Student.studentNameASCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_name_desc) {
                            Collections.sort(studentList, Student.studentNameDESCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_birthday_asc) {
                            Collections.sort(studentList, Student.studentBirthdayASCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_birthday_desc) {
                            Collections.sort(studentList, Student.studentBirthdayDESCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_address_asc) {
                            Collections.sort(studentList, Student.studentAddressASCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_address_desc) {
                            Collections.sort(studentList, Student.studentAddressDESCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_gender_asc) {
                            Collections.sort(studentList, Student.studentGenderASCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_gender_desc) {
                            Collections.sort(studentList, Student.studentGenderDESCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_phone_asc) {
                            Collections.sort(studentList, Student.studentPhoneASCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_phone_desc) {
                            Collections.sort(studentList, Student.studentPhoneDESCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_enrollmentdate_asc) {
                            Collections.sort(studentList, Student.studentEnrollmentDateASCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_enrollmentdate_desc) {
                            Collections.sort(studentList, Student.studentEnrollmentDateDESCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_major_asc) {
                            Collections.sort(studentList, Student.studentMajorASCComparator);
                            adapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.menu_sort_major_desc) {
                            Collections.sort(studentList, Student.studentMajorDESCComparator);
                            adapter.notifyDataSetChanged();
                        }

                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchString) {
                if (searchString.isEmpty()) {
                    studentList.clear();
                    studentList.addAll(originalStudentList);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.getFilter().filter(searchString);
                }
                return false;
            }
        });
    }

    private void getCurrentFirebaseUser() {
        DocumentReference df = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userRole = documentSnapshot.getString("role");
                    Log.d("getUserRole", "Get user role Succeeded: " + userRole);
                } else {
                    Log.d("getUserRole", "Get user role Failed");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("getUserRole", "Document Reference failed");
            }
        });
    }

    public boolean hasAuthority() {
        return userRole.equals("Admin") || userRole.equals("Manager");
    }

    // ReloadAfterEditStudent(2) - Pass intent to MainActivity
    public void receiveFromAdapter(Intent intent) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.editStudent(intent);
        }
    }

    private Uri fileuri;
    private List<Student> importedStudentList;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && data != null) {
            Log.d("FileRead", "onActivityResult");
            fileuri = data.getData();
            importedStudentList = new ArrayList<>();
            importedStudentList = readCSVFile(getFilePathFromUri(fileuri));

            for (Student student : importedStudentList) {
                firebaseFirestore.collection("Students")
                        .add(student)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Firestore", "Student added with ID: " + documentReference.getId());
                                // Optionally, update UI or perform other actions on success
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Firestore", "Error adding student", e);
                                // Optionally, update UI or perform other actions on failure
                            }
                        });
            }
        }
    }

    // Getting file path from Uri
    public String getFilePathFromUri(Uri uri) {
        String filePath = null;
        String scheme = uri.getScheme();
        Log.d("FileRead", "scheme: " + scheme);
        if (scheme != null && scheme.equals("content")) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
                cursor.close();
            }
        } else if (scheme != null && scheme.equals("file")) {
            filePath = uri.getPath();
        }
        return filePath;
    }

    // Reading file data
    public List<Student> readCSVFile(String path) {
        List<Student> students = new ArrayList<>();
        if (path == null) {
            Log.e("FileRead", "File path is null");
            return students; // Return empty list
        }

        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file, "UTF-8");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splited = line.split(",");

                // Check if the split line has enough data for a Student object
                if (splited.length == 8) {
                    Student student = new Student();
                    student.setCode(splited[0]);
                    student.setName(splited[1]);
                    student.setBirthday(splited[2]);
                    student.setAddress(splited[3]);
                    student.setGender(splited[4]);
                    student.setPhone(splited[5]);
                    student.setEnrollmentDate(splited[6]);
                    student.setMajor(splited[7]);

                    students.add(student);
                } else {
                    Toast.makeText(getContext(), "Imported file format error", Toast.LENGTH_SHORT).show();
                    Log.e("FileRead", "Imported file format error");
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("FileRead", "File not found: " + path);
        }
        return students;
    }

    private void writeToFile() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "studentsOutput.csv");

        try {
            path.mkdirs();

            FileOutputStream stream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stream);

            outputStreamWriter.write("Code,Name,Birthday,Address,Gender,Phone,EnrollmentDate,Major\n");

            for(Student student : originalStudentList) {
                String[] row = {student.getCode(), student.getName(), student.getBirthday(), student.getAddress(),
                                student.getGender(), student.getPhone(), student.getEnrollmentDate(), student.getMajor()};

                String csvRow = TextUtils.join(",", row) + "\n";
                outputStreamWriter.write(csvRow);
            }

            outputStreamWriter.close();

            Toast.makeText(getContext(), "studentsOutput.csv written to DOWNLOAD successfully", Toast.LENGTH_SHORT).show();
            Log.d("CSVWriteFile", "Successfully writing to CSV file. File store at Download");
        } catch (IOException e) {
            Log.e("CSVWriteFile", "Error writing CSV file", e);
            Toast.makeText(getContext(), "Error writing CSV file", Toast.LENGTH_SHORT).show();
        }
    }
}