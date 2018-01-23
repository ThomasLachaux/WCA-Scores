package com.adrastel.niviel.preferences;

import android.content.Context;
import android.content.Intent;
import android.preference.ListPreference;
import android.util.AttributeSet;

import com.adrastel.niviel.activities.SettingsActivity;
import com.adrastel.niviel.assets.Assets;

public class ThemePreference extends ListPreference {
    public ThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if(positiveResult) {

            boolean isDark = Assets.isDark(getPersistedString("0"));

            SettingsActivity activity = (SettingsActivity) getContext();

            activity.setDayNightTheme(isDark);

            activity.finish();
            activity.startActivity(new Intent(activity, SettingsActivity.class));

        }

    }
}
