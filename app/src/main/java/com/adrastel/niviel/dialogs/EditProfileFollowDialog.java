package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.BaseActivity;

public class EditProfileFollowDialog extends DialogFragment {

    DialogProfileListener listener;
    String username = null;

    public static EditProfileFollowDialog newInstance(String name) {
        EditProfileFollowDialog instance = new EditProfileFollowDialog();

        Bundle args = new Bundle();
        args.putString(BaseActivity.USERNAME, name);

        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString(BaseActivity.USERNAME, null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        setTargetFragment(null, -1);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_edit_profile_follow_title);
        builder.setMessage(getString(R.string.dialog_edit_profile_follow_message, username));

        builder.setPositiveButton(R.string.follow, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listener != null) {
                    listener.onFollow();
                }
            }
        });

        builder.setNeutralButton(R.string.edit_profile, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(listener != null) {
                    listener.onEdit();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    public void setOnDialogClickListener(DialogProfileListener listener) {
        this.listener = listener;
    }

    public interface DialogProfileListener {
        void onFollow();
        void onEdit();
    }
}
