package com.adrastel.niviel.adapters;

import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.assets.Log;

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {


    private FragmentActivity activity;
    private MainActivity mainActivity;

    public BaseAdapter(FragmentActivity activity) {

        this.activity = activity;

        try {
            this.mainActivity = (MainActivity) activity;
        }
        catch (ClassCastException e) {
            Log.e("MainActivity hasn't been cast");
        }
    }

    protected FragmentActivity getActivity() {
        return activity;
    }

    protected MainActivity getMainActivity() {
        return mainActivity;
    }

    protected String getString(@StringRes int resId) {
        return getActivity().getString(resId);
    }

    protected String getString(@StringRes int resId, Object... args) {
        return getActivity().getString(resId, args);
    }
}
