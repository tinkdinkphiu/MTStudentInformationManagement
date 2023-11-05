package com.phule.mtstudentinformationmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
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
            }
            else if (item.getItemId() == R.id.menu_nav_student_list) {
                changeFragment(new StudentListFragment());
            }
            else if(item.getItemId() == R.id.menu_nav_logout) {
//                Log out here
            }
            // Close nav drawer
            drawerLayout.closeDrawers();
            return true;
        });
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
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // If open, back button close nav drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // If not default back button
        else {
            super.onBackPressed();
        }
    }

    private void changeFragment(Fragment fragment){
        // Begin new fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Replace current fragment
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}