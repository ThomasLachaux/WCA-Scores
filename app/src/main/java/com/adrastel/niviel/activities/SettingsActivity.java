package com.adrastel.niviel.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    private boolean hasToRestart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)  {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isDark = Assets.isDark(preferences.getString(getString(R.string.pref_isdark), "0"));
        setDayNightTheme(isDark);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new SettingsFragment())
                .commit();
    }

    public void hasToRestart() {
        hasToRestart = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        if(hasToRestart)
            setResult(MainActivity.RESTART_ACTIVITY);
        else
            setResult(Activity.RESULT_CANCELED);
        super.finish();
    }
}
