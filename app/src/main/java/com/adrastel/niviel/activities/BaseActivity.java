package com.adrastel.niviel.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import com.adrastel.niviel.R;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String WCA_ID = "wca_id";
    public static final String ID = "id";
    public static final String USERNAME = "username";

    public void setDayNightTheme(boolean isDark) {
        if(isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if(intent.resolveActivity(getPackageManager()) != null)
            super.startActivity(intent);

        else
            Toast.makeText(this, R.string.error_activity, Toast.LENGTH_LONG).show();

    }
}
