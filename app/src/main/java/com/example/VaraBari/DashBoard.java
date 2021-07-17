package com.example.VaraBari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private View headerView;
    public TextView navUserFullName;
    private ImageView navUserPic;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String uuid;
    private String _profileImageLink, _fullName, _phoneNo, _address, _email;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dash_board);

        firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Users");
        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_dashboard);
        navigationView = (NavigationView) findViewById(R.id.nav_view_dashboard);
        headerView = navigationView.getHeaderView(0);
        navUserPic = (ImageView)headerView.findViewById(R.id.nav_user_pic);
        navUserFullName = (TextView)headerView.findViewById(R.id.nav_user_full_name);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _fullName = dataSnapshot.child(uuid).child("fullName").getValue(String.class);
                _profileImageLink = dataSnapshot.child(uuid).child("profileImageLink").getValue(String.class);
                if(!_profileImageLink.isEmpty()) {
                    Picasso.get().load(_profileImageLink).into(navUserPic);
                }
                if(!_fullName.isEmpty())navUserFullName.setText(_fullName);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(DashBoard.this, "Error!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                break;
            case R.id.nav_favourites:
//                Go to the list of favourites
                break;
            case R.id.nav_ads:
//                Go to the list of ads published by user
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(DashBoard.this, UserProfile.class);
                startActivity(intent);
                break;
            case R.id.nav_help:
//                Go to user-manual page
                break;
            case R.id.nav_log_out:
                firebaseAuth.signOut();
                SharedPreferences sharedPreferences = getSharedPreferences(LogInScreenActivity.prefName, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("hasLoggedIn", false);
                editor.commit();
                startActivity(new Intent(getApplicationContext(), LogInScreenActivity.class));
                break;
        }
        return true;
    }
}