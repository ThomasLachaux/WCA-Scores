package com.adrastel.niviel.preferences;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import com.adrastel.niviel.R;

import de.psdev.licensesdialog.LicensesDialog;

public class LicensePreference extends Preference {

    public LicensePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        new LicensesDialog.Builder(getContext())
                .setNotices(R.raw.notices)
                .build()
                .show();
    }
}
