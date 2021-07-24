package com.example.VaraBari.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.VaraBari.Adapters.DashboardAdapter;
import com.example.VaraBari.Objects.House;
import com.example.VaraBari.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class FavouritesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private View headerView;
    private RecyclerView recyclerView;
    public EditText searchView;
    public SwipeRefreshLayout swipeRefreshLayout;
    CharSequence search = "";
    public TextView navUserFullName;
    private ImageView navUserPic;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String uuid;
    private String _profileImageLink, _fullName;

    DashboardAdapter dashboardAdapter;
    ArrayList<House> list;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;
    private DatabaseReference houseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Users");
        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        houseRef = FirebaseDatabase.getInstance().getReference("favouriteList").child(uuid);

        swipeRefreshLayout = findViewById(R.id.favourites_swipeRefreshLayout);
        recyclerView = findViewById(R.id.favourites_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        dashboardAdapter = new DashboardAdapter(this, list);
        recyclerView.setAdapter(dashboardAdapter);
        dashboardAdapter.setOnHouseClickListener(new DashboardAdapter.OnHouseClickListener() {
            @Override
            public void onHouseClick(int position) {
//                Log.d(TAG, position + "is clicked");
                Intent intent = new Intent(FavouritesActivity.this, OverViewPage.class);
                intent.putExtra("House", list.get(position));
                startActivity(intent);
            }
        });

        houseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    House house = dataSnapshot.getValue(House.class);
                    list.add(house);
                }
                Collections.shuffle(list, new Random(System.currentTimeMillis()));
                dashboardAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(FavouritesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        // Searchbar
        searchView = (EditText)findViewById(R.id.favourites_searchview);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                dashboardAdapter.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ////////////////////////////////////

        // swiperefresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });
        ////////////////////////////////////

        // navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_favourites);
        navigationView = (NavigationView) findViewById(R.id.nav_view_favourites);
        headerView = navigationView.getHeaderView(0);
        navUserPic = (ImageView) headerView.findViewById(R.id.nav_user_pic);
        navUserFullName = (TextView) headerView.findViewById(R.id.nav_user_full_name);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favourites");

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        ///////////////////////////////////////////////////////////////////

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _fullName = dataSnapshot.child(uuid).child("fullName").getValue(String.class);
                _profileImageLink = dataSnapshot.child(uuid).child("profileImageLink").getValue(String.class);
                if (!_profileImageLink.isEmpty()) {
                    Picasso.get().load(_profileImageLink).into(navUserPic);
                }
                if (!_fullName.isEmpty()) navUserFullName.setText(_fullName);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(FavouritesActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void RearrangeItems() {
        Collections.shuffle(list, new Random(System.currentTimeMillis()));
//        DashboardAdapter adapter = new DashboardAdapter(DashBoard.this, list);
//        recyclerView.setAdapter(adapter);
        dashboardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    // navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                Intent dashboard = new Intent(FavouritesActivity.this, DashBoard.class);
                startActivity(dashboard);
                break;
            case R.id.nav_favourites:
//                Go to the list of favourites
                Intent favourites = new Intent(FavouritesActivity.this, FavouritesActivity.class);
                startActivity(favourites);
                break;
            case R.id.nav_ads:
//                Go to the list of ads published by user
                Intent myHouse = new Intent(FavouritesActivity.this, MyHouses.class);
                startActivity(myHouse);
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(FavouritesActivity.this, UserProfile.class);
                startActivity(intent);
                break;
            case R.id.nav_publish:
                Intent newAd = new Intent(FavouritesActivity.this, NewAdForm.class);
                startActivity(newAd);
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