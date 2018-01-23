package com.adrastel.niviel.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.models.readable.Record;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecordDialog extends DialogFragment {

    public static final String RECORDS = "records";
    Unbinder unbinder;
    @BindView(R.id.layout_single) LinearLayout layout_single;
    @BindView(R.id.single) TextView single;
    @BindView(R.id.single_nr) TextView nr_single;
    @BindView(R.id.single_cr) TextView cr_single;
    @BindView(R.id.single_wr) TextView wr_single;

    @BindView(R.id.layout_average) LinearLayout layout_average;
    @BindView(R.id.average) TextView average;
    @BindView(R.id.average_nr) TextView nr_average;
    @BindView(R.id.average_cr) TextView cr_average;
    @BindView(R.id.average_wr) TextView wr_average;

    public static RecordDialog newInstance(Record record) {

        Bundle args = new Bundle();
        args.putParcelable(RECORDS, record);

        RecordDialog instance = new RecordDialog();
        instance.setArguments(args);
        return instance;
    }

    @NonNull
    @Override
    @SuppressWarnings("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Record record = getArguments().getParcelable(RECORDS);

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View view = inflater.inflate(R.layout.dialog_record_info, null);
        unbinder = ButterKnife.bind(this, view);


        if(record != null) {

            if(record.getSingle() != null && !record.getSingle().trim().equals("")) {
                layout_single.setVisibility(View.VISIBLE);
                single.setText(Assets.fromHtml(getString(R.string.record_time, Assets.wrapStrong(record.getSingle()))));
                nr_single.setText(Assets.fromHtml(getString(R.string.record_nr_format, Assets.wrapStrong(record.getNr_single()))));
                cr_single.setText(Assets.fromHtml(getString(R.string.record_cr_format, Assets.wrapStrong(record.getCr_single()))));
                wr_single.setText(Assets.fromHtml(getString(R.string.record_wr_format, Assets.wrapStrong(record.getWr_single()))));
            }

            if(record.getAverage() != null && !record.getAverage().trim().equals("")) {
                Log.d("average", record.getAverage());
                layout_average.setVisibility(View.VISIBLE);
                average.setText(Assets.fromHtml(getString(R.string.record_time, Assets.wrapStrong(record.getAverage()))));
                nr_average.setText(Assets.fromHtml(getString(R.string.record_nr_format, Assets.wrapStrong(record.getNr_average()))));
                cr_average.setText(Assets.fromHtml(getString(R.string.record_cr_format, Assets.wrapStrong(record.getCr_average()))));
                wr_average.setText(Assets.fromHtml(getString(R.string.record_wr_format, Assets.wrapStrong(record.getWr_average()))));
            }
        }

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.more_info);

        builder.setView(view);

        builder.setPositiveButton(R.string.ok, null);

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        setTargetFragment(null, -1);
        super.onSaveInstanceState(outState);
    }
}
