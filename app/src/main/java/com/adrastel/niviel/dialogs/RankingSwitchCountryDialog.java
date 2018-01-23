package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.adrastel.niviel.R;


public class RankingSwitchCountryDialog extends DialogFragment {

    public static final String COUNTRY_POSITION = "country_position";
    int position = -1;
    RankingSwitchCountryListener listener;

    public static RankingSwitchCountryDialog newInstance(int position) {

        RankingSwitchCountryDialog instance = new RankingSwitchCountryDialog();

        Bundle args = new Bundle();
        args.putInt(COUNTRY_POSITION, position);

        instance.setArguments(args);
        return instance;

    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        position = getArguments() != null ? getArguments().getInt(COUNTRY_POSITION, -1) : null;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())

                .setTitle(getString(R.string.switch_country))
                .setIcon(R.drawable.ic_flag)
                .setSingleChoiceItems(R.array.countries, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        position = i;
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(listener != null) {
                            listener.onClick(position);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    public void setListener(RankingSwitchCountryListener listener) {
        this.listener = listener;
    }

    public interface RankingSwitchCountryListener {
        void onClick(int position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        setTargetFragment(null, -1);
        super.onSaveInstanceState(outState);
    }
}
