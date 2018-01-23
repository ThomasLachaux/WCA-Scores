package com.adrastel.niviel.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.adrastel.niviel.R;
import com.adrastel.niviel.services.EditRecordService;

public class ResetPersIdPreference extends DialogPreference {

    private static final long DEFAULT_VALUE = -1;

    public ResetPersIdPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogIcon(null);
        setPositiveButtonText(R.string.yes);
        setNegativeButtonText(R.string.no);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult) {

            long prefId = getPersistedLong(DEFAULT_VALUE);

            Intent delete = new Intent(getContext(), EditRecordService.class);
            delete.putExtra(EditRecordService.ACTION, EditRecordService.DELETE_FOLLOWER);
            delete.putExtra(EditRecordService.ID, prefId);
            delete.putExtra(EditRecordService.FOLLOWS, false);

            getContext().startService(delete);

            persistLong(DEFAULT_VALUE);

        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, (int) DEFAULT_VALUE);
    }
}
