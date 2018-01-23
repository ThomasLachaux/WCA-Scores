package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.adrastel.niviel.R;

public class RankingChooseCubeDialog extends DialogFragment {

    public static final String SELECTED_ITEMS = "selected_items";

    RankingChooseCubeListener listener;

    private boolean[] selectedItems = getDefaultSelectedItems();

    public static RankingChooseCubeDialog newInstance(boolean[] selectedItems) {

        Bundle args = new Bundle();
        args.putBooleanArray(SELECTED_ITEMS, selectedItems);

        RankingChooseCubeDialog instance = new RankingChooseCubeDialog();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedItems = getArguments().getBooleanArray(SELECTED_ITEMS);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.switch_cube);
        builder.setIcon(R.drawable.ic_swap);

        builder.setMultiChoiceItems(R.array.cubes, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                selectedItems[position] = isChecked;
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onClick(selectedItems);
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        return builder.create();
    }


    public void setListener(RankingChooseCubeListener listener) {
        this.listener = listener;
    }


    public interface RankingChooseCubeListener {
        void onClick(boolean[] selectedItems);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        setTargetFragment(null, -1);
        super.onSaveInstanceState(outState);
    }

    public static boolean[] getDefaultSelectedItems() {
        return new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    }

}
