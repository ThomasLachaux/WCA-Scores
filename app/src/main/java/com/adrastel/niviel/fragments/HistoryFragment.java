package com.adrastel.niviel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.BaseActivity;
import com.adrastel.niviel.adapters.HistoryAdapter;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.models.readable.history.Event;
import com.adrastel.niviel.models.readable.history.History;
import com.adrastel.niviel.providers.HistoryProvider;
import com.adrastel.niviel.views.RecyclerViewCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class HistoryFragment extends BaseFragment {

    public static final String EVENTS = "events";
    public static final String SORT = "order";

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerViewCompat recyclerView;
    @BindView(R.id.empty_view) TextView emptyView;

    private Unbinder unbinder;
    private HistoryAdapter adapter;

    private String wca_id = null;
    private long follower_id = -1;

    // If sortByEvent is true, sorts according to the event, otherwise, sorts according to the competition
    private boolean sortByEvent = true;

    private boolean alphabeticalOrderHist = false;
    private boolean alphabeticalOrderComp = false;


    public static HistoryFragment newInstance(long follower_id, boolean sort) {
        HistoryFragment instance = new HistoryFragment();

        Bundle args = new Bundle();
        args.putLong(BaseActivity.ID, follower_id);
        args.putBoolean(SORT, sort);

        instance.setArguments(args);
        return instance;
    }

    public static HistoryFragment newInstance(String wca_id, boolean sort) {
        HistoryFragment instance = new HistoryFragment();

        Bundle args = new Bundle();
        args.putString(BaseActivity.WCA_ID, wca_id);
        args.putBoolean(SORT, sort);

        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        if(arguments != null) {
            sortByEvent = arguments.getBoolean(SORT);

            follower_id = arguments.getLong(BaseActivity.ID, -1);
            wca_id = follower_id == -1 ? arguments.getString(BaseActivity.WCA_ID, null) : null;
        }


        adapter = new HistoryAdapter(getActivity(), new ArrayList<Event>());

        alphabeticalOrderHist = preferences.getBoolean(getString(R.string.pref_alphabetical_hist_order), false);
        alphabeticalOrderComp = preferences.getBoolean(getString(R.string.pref_alphabetical_comp_order), false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        unbinder = ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        recyclerView.initRecyclerViewCompat(swipeRefresh, progressBar, emptyView);

        recyclerView.showProgress();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if(savedInstanceState != null) {

            ArrayList<Event> histories = savedInstanceState.getParcelableArrayList(EVENTS);

            if(histories != null && histories.size() != 0) {
                adapter.refreshData(histories);
                recyclerView.showRecycler();
            } else {
                recyclerView.showEmpty();
            }
            if(follower_id != -1) {
                DatabaseHelper database = DatabaseHelper.getInstance(getContext());

                Follower follower = database.selectFollowerFromId(follower_id);
                wca_id = follower.wca_id();
            }
        }

        else if(follower_id != -1) {

            DatabaseHelper database = DatabaseHelper.getInstance(getContext());

            Follower follower = database.selectFollowerFromId(follower_id);
            wca_id = follower.wca_id();

            ArrayList<com.adrastel.niviel.database.History> localHistories = database.selectHistoriesFromFollower(follower_id);
            ArrayList<History> histories = new ArrayList<>();

            for(com.adrastel.niviel.database.History history : localHistories) {
                histories.add(history.toHistoryModel());
            }

            adapter.refreshData(makeExpandedArrayList(histories));
            recyclerView.showRecycler();
        }

        else if(isConnected()) {
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

        outState.putParcelableArrayList(EVENTS, adapter.getDatas());
        setTargetFragment(null, -1);

        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    public void callData() {

        HttpUrl url = new WcaUrl()
                .profile(wca_id)
                .build();

        recyclerView.callData(url, new RecyclerViewCompat.SuccessCallback() {
            @Override
            public void onSuccess(String response) {
                Document document = Jsoup.parse(response);

                final ArrayList<History> histories = HistoryProvider.getHistory(document);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(makeExpandedArrayList(histories));
                    }
                });
            }
        });

    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_History;
    }

    /**
     * Transforme un arraylist d'histoires en arraylist d'arraylist
     * Make an Histories ArrayList to a ArrayList Arraylist
     */
    public ArrayList<Event> makeExpandedArrayList(ArrayList<History> histories) {

        LinkedHashMap<String, ArrayList<History>> hashmap = new LinkedHashMap<>();
        ArrayList<Event> events = new ArrayList<>();

        // For each history, sort in a HashTable with the event as the key
        for(History history : histories) {

            // Adds the competiton if it exists
            if(hashmap.containsKey(sortByEvent ? history.getEvent() : history.getCompetition())) {

                ArrayList<History> oldHistory = hashmap.get(sortByEvent ? history.getEvent() : history.getCompetition());
                oldHistory.add(history);

                hashmap.put(sortByEvent ? history.getEvent() : history.getCompetition(), oldHistory);

            }

            // Otherwise, creates a group
            else {

                ArrayList<History> oldHistories = new ArrayList<>();
                oldHistories.add(history);

                hashmap.put(sortByEvent ? history.getEvent() : history.getCompetition(), oldHistories);
            }
        }


        // Converts the HashMap to Events
        Set<String> keys = hashmap.keySet();

        for(String key : keys) {

            Event event = new Event(key, sortByEvent, hashmap.get(key));
            events.add(event);
        }

        // Sort options

        // In the history tab
        if(sortByEvent && alphabeticalOrderHist) {
            for(Event event : events)
                Collections.sort(event.getChildList(), new History.ComparatorByCompetition());
        }

        // In the competition tab
        else if(!sortByEvent && alphabeticalOrderComp)
            Collections.sort(events, new Event.ComparatorByName());

        return events;
    }

}
