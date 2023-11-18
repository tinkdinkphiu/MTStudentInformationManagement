package com.phule.mtstudentinformationmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Fragment currentFragment;
    private boolean returnToUserManagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.layout_drawer);
        navigationView = findViewById(R.id.nav_view);

        // Set up the ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Load StudentListFragment by default
        changeFragment(new StudentListFragment());

        // Set up navigation view listener
        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle item clicks
            if (item.getItemId() == R.id.menu_nav_profile) {
                changeFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.menu_nav_student_manager) {
                changeFragment(new StudentListFragment());
            } else if (item.getItemId() == R.id.menu_nav_account_manager) {
                changeFragment(new UserManagerFragment());
            } else if (item.getItemId() == R.id.menu_nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            // Highlight the selected item
            item.setChecked(true);

            // Close nav drawer
            drawerLayout.closeDrawers();
            return true;
        });

        // Set "Student List" as checked by default
        navigationView.setCheckedItem(R.id.menu_nav_student_manager);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Open nav drawer on drawer toggle touch
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Check if nav drawer is open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // If open, back button close nav drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // If not default back button
        else {
            super.onBackPressed();
        }
    }

    private void changeFragment(Fragment fragment) {
        // Begin new fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Replace current fragment
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    // ReloadAfterEditStudent(3) - Pass intent extra to EditStudentActivity
    public void editStudent(Intent intent) {
        startActivityForResult(intent, 98765);
    }

    // ReloadAfterEditStudent(3) - Pass intent extra to EditUserActivity
    public void editUser(Intent intent) {
        startActivityForResult(intent, 12345);
    }

    // ReloadAfterEditStudent(5) - Receive from EditStudent EditUser and reload
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 98765 && resultCode == Activity.RESULT_OK) {
            if (returnToUserManagerFragment) {
                changeFragment(new UserManagerFragment());
                returnToUserManagerFragment = false;
            } else {
                changeFragment(new StudentListFragment());
            }
        } else if (requestCode == 12345 && resultCode == Activity.RESULT_OK) {
            changeFragment(new UserManagerFragment());
        }
    }
    public void setReturnToUserManagerFragment(boolean returnToUserManagerFragment) {
        this.returnToUserManagerFragment = returnToUserManagerFragment;
    }
}