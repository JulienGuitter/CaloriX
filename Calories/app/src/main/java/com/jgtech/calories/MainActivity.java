package com.jgtech.calories;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final static String PREFS_SHARE = "com.jgtech.calories";
    private final static String PREF_SELECTED = "selected";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if user is connected or not and redirect to the right activity

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
        String user = sharedPreferences.getString(PREF_SELECTED, "");

        Intent intent;
        if(!user.isEmpty()){
            intent = new Intent(this, AccountSettingActivity.class);
        }else{
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);

        finish();

    }
}
