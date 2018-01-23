package com.adrastel.niviel.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.assets.Assets;

public abstract class BaseFragment extends Fragment {
    public abstract int getStyle();

    protected ConnectivityManager connectivityManager;
    protected SharedPreferences preferences;
    protected MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        try {
            activity = (MainActivity) getActivity();
        }

        catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    protected boolean isConnected() {
        return Assets.isConnected(connectivityManager);
    }

}
