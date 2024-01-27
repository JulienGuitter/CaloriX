package com.jgtech.calories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.SharedPreferences;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;

    private final static String PREFS_SHARE = "com.jgtech.calories";
    private final static String PREF_SELECTED = "selected";

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    protected void allocateActivityTitle(String title){
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // show the selected layout in the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.nav_account_settings){
            // go to account setting activity
            startActivity(new Intent(this, AccountSettingActivity.class));
            overridePendingTransition(0, 0);
            finish();
        }else if(item.getItemId() == R.id.nav_calculate_calories) {
            // go to calculate calories activity
            startActivity(new Intent(this, CalculateCaloriesActivity.class));
            overridePendingTransition(0, 0);
            finish();
        }else if(item.getItemId() == R.id.nav_calendar){
            // go to calendar activity
            startActivity(new Intent(this, CalendarActivity.class));
            overridePendingTransition(0, 0);
            finish();
        }else if(item.getItemId() == R.id.nav_historique){
            // go to historique activity
            startActivity(new Intent(this, HistoriqueActivity.class));
            overridePendingTransition(0, 0);
            finish();
        }else if(item.getItemId() == R.id.nav_logout){
            // clear the selected shared preferences
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(PREF_SELECTED);
            editor.apply();
            // go to login activity
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(0, 0);
            finish();
        }
        return false;
    }
}