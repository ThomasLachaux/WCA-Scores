package com.adrastel.niviel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.BaseActivity;
import com.adrastel.niviel.adapters.RecordAdapter;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.models.readable.User;
import com.adrastel.niviel.providers.RecordProvider;
import com.adrastel.niviel.providers.UserProvider;
import com.adrastel.niviel.views.RecyclerViewCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class RecordFragment extends BaseFragment {


    public static final String RECORDS = "records";
    public static final String USER = "user";

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view)
    RecyclerViewCompat recyclerView;
    @BindView(R.id.empty_view) TextView emptyView;


    private Unbinder unbinder;

    private RecordAdapter adapter;

    private String wca_id;
    private long follower_id = -1;

    public static RecordFragment newInstance(long id) {

        RecordFragment instance = new RecordFragment();

        Bundle args = new Bundle();
        args.putLong(BaseActivity.ID , id);

        instance.setArguments(args);

        return instance;

    }

    public static RecordFragment newInstance(String wca_id) {

        RecordFragment instance = new RecordFragment();

        Bundle args = new Bundle();
        args.putString(BaseActivity.WCA_ID ,wca_id);

        instance.setArguments(args);

        return instance;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new RecordAdapter(getActivity());

        wca_id = null;


        Bundle arguments = getArguments();

        if(arguments != null) {
            follower_id = arguments.getLong(BaseActivity.ID, -1);

            wca_id = follower_id == -1 ? arguments.getString(BaseActivity.WCA_ID, null) : null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);


        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.records_columns), StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.initRecyclerViewCompat(swipeRefresh, progressBar, emptyView);

        recyclerView.showProgress();


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            /**
             * Checks if the stored data aren't empty
             * Shows an error if it's the case
             */
            if(adapter.refreshData(loadUserData(savedInstanceState), loadRecordData(savedInstanceState)))
                recyclerView.showRecycler();
            else
                recyclerView.showEmpty();

            if(follower_id != -1) {
                DatabaseHelper database = DatabaseHelper.getInstance(getContext());

                Follower follower = database.selectFollowerFromId(follower_id);

                this.wca_id = follower.wca_id();
            }
        }

        else if(follower_id != -1) {

            DatabaseHelper database = DatabaseHelper.getInstance(getContext());

            Follower follower = database.selectFollowerFromId(follower_id);

            this.wca_id = follower.wca_id();

            ArrayList<com.adrastel.niviel.database.Record> localRecords = database.selectRecordsFromFollower(follower._id());
            ArrayList<Record> records = new ArrayList<>();

            for(com.adrastel.niviel.database.Record record : localRecords) {
                records.add(record.toRecordModel());
            }

            adapter.refreshData(new User(follower), records);
            recyclerView.showRecycler();


        }
        else if (isConnected()) {
            callData();
        }

        else {
            recyclerView.showEmpty();
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callData();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(RECORDS, adapter.getDatas());
        outState.putParcelable(USER, adapter.getUser());
        setTargetFragment(null, -1);
        super.onSaveInstanceState(outState);
    }

    protected ArrayList<Record> loadRecordData(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            return savedInstanceState.getParcelableArrayList(RECORDS);
        }

        return new ArrayList<>();
    }

    protected User loadUserData(Bundle savedInstanceState) {
        if(savedInstanceState != null) {

            return savedInstanceState.getParcelable(USER);
        }

        return null;
    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_Records;
    }

    public void callData() {

        HttpUrl url = new WcaUrl()
                .profile(wca_id)
                .build();
        recyclerView.callData(url, new RecyclerViewCompat.SuccessCallback() {
            @Override
            public void onSuccess(String response) {

                Document document = Jsoup.parse(response);

                final ArrayList<Record> records = RecordProvider.getRecord(getActivity(), document);
                final User user = UserProvider.getUser(document);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(user, records);
                    }
                });


            }
        });
    }

}