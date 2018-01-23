package com.adrastel.niviel.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.MainIntroActivity;
import com.adrastel.niviel.activities.SearchActivity;
import com.adrastel.niviel.dialogs.InfoDialog;
import com.adrastel.niviel.services.EditRecordService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RedirectFragment extends Fragment{

    @BindView(R.id.connect) Button connect;
    @BindView(R.id.visitor) Button visitor;
    @BindView(R.id.progress) ProgressBar progress;


    Unbinder unbinder;

    boolean isEnabled = true;
    private MainIntroActivity activity;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            enableButtons();

            if(intent.getIntExtra(EditRecordService.ACTION, EditRecordService.ADD_RECORD_FAILURE) == EditRecordService.ADD_RECORD_SUCCESS)
                activity.nextSlide();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (MainIntroActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_redirect, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoDialog introDialog = InfoDialog.newInstance(R.string.information, R.string.info_no_connection);
                introDialog.show(getFragmentManager(), "IntroDialog");

                introDialog.setOnClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.nextSlide();
                    }
                });

            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(EditRecordService.INTENT_FILTER));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == SearchActivity.SEARCH_SUCCESS) {

            final String name = data.getStringExtra(SearchActivity.NAME);
            final String wca_id = data.getStringExtra(SearchActivity.WCA_ID);

            Handler handler = new Handler(Looper.getMainLooper());

            Intent add = new Intent(getContext(), EditRecordService.class);

            add.putExtra(EditRecordService.ACTION, EditRecordService.ADD_FOLLOWER);
            add.putExtra(EditRecordService.USERNAME, name);
            add.putExtra(EditRecordService.WCA_ID, wca_id);
            add.putExtra(EditRecordService.IS_PERSONAL, true);
            add.putExtra(EditRecordService.FOLLOWS, false);

            getContext().startService(add);


            handler.post(new Runnable() {
                @Override
                public void run() {
                    disableButtons();
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isEnabled", isEnabled);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
            isEnabled = savedInstanceState.getBoolean("isEnabled", true);

        if(isEnabled)
            enableButtons();

        else
            disableButtons();
    }

    public void enableButtons() {
        connect.setEnabled(true);
        visitor.setEnabled(true);
        progress.setVisibility(View.INVISIBLE);
        isEnabled = true;
    }

    public void disableButtons() {
        connect.setEnabled(false);
        visitor.setEnabled(false);
        progress.setVisibility(View.VISIBLE);
        isEnabled = false;
    }
}
