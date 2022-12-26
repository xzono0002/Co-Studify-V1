package com.june.costudify.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.june.costudify.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    Toolbar toolbar;
    TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_CoStudify);
        setContentView(R.layout.activity_main);

        header = findViewById(R.id.header);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AllNotesFragment()).commit();
                    header.setText("NOTES");
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private NavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.allTask_btn:
                        selectedFragment = new AllNotesFragment();
                        header.setVisibility(View.VISIBLE);
                        header.setText("NOTES");
                        break;
                    case R.id.aboutUs:
                        selectedFragment = new SettingsFragment();
                        header.setVisibility(View.GONE);
                        break;
                    case R.id.privacy_policy:
                        selectedFragment = new PolicyFragment();
                        header.setVisibility(View.GONE);
                        break;
                    case R.id.contact_us:
                        selectedFragment = new ContactFragment();
                        header.setVisibility(View.GONE);
                        break;

                }

                assert selectedFragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            };
}