package com.adrastel.niviel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.adapters.CompetitionAdapter;
import com.adrastel.niviel.assets.Cubes;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.dialogs.RankingChooseCubeDialog;
import com.adrastel.niviel.dialogs.RankingSwitchCountryDialog;
import com.adrastel.niviel.models.readable.competition.Competition;
import com.adrastel.niviel.models.readable.competition.Title;
import com.adrastel.niviel.providers.CompetitionProvider;
import com.adrastel.niviel.views.RecyclerViewCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class CompetitionFragment extends BaseFragment {

    private Unbinder unbinder;
    private CompetitionAdapter adapter;

    public static final String TITLES = "titles";

    private int countryPosition = 0;
    private boolean[] eventSelected = RankingChooseCubeDialog.getDefaultSelectedItems();

    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view) RecyclerViewCompat recyclerView;
    @BindView(R.id.empty_view) TextView emptyView;

    public static CompetitionFragment newInstance() {
        return new CompetitionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        adapter = new CompetitionAdapter(getActivity(), new ArrayList<Title>());
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
            ArrayList<Title> titles = savedInstanceState.getParcelableArrayList(TITLES);
            adapter.refreshData(titles);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TITLES, adapter.getDatas());
        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ranking, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.switch_cube:

                RankingChooseCubeDialog switchCube = RankingChooseCubeDialog.newInstance(eventSelected);
                switchCube.show(getFragmentManager(), "cubeSwitch");

                switchCube.setListener(new RankingChooseCubeDialog.RankingChooseCubeListener() {
                    @Override
                    public void onClick(boolean[] selectedItems) {
                        eventSelected = selectedItems;
                        callData();
                    }
                });

                return true;

            case R.id.switch_country:

                RankingSwitchCountryDialog switchCountry = RankingSwitchCountryDialog.newInstance(countryPosition);
                switchCountry.show(getFragmentManager(), "countrySwitch");

                switchCountry.setListener(new RankingSwitchCountryDialog.RankingSwitchCountryListener() {
                    @Override
                    public void onClick(int position) {
                        countryPosition = position;
                        callData();
                    }
                });

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_Competitions;
    }

    public void callData() {

        ArrayList<String> event_ids = new ArrayList<>();

        for(int i = 0; i < eventSelected.length; i++) {
            if(eventSelected[i]) {
                event_ids.add(Cubes.getCubeId(i));
            }
        }

        HttpUrl url = new WcaUrl()
                .competition(event_ids, getResources().getStringArray(R.array.competitions_countries_id)[countryPosition])
                .build();


        recyclerView.callData(url, new RecyclerViewCompat.SuccessCallback() {
            @Override
            public void onSuccess(String response) {

                Document document = Jsoup.parse(response);

                final ArrayList<Title> titles = new ArrayList<>();

                // In progress
                Title inProgress = treatData(document, CompetitionProvider.IN_PROGRESS, R.plurals.in_progress_competitions);

                // Upcoming
                Title upcoming = treatData(document, CompetitionProvider.UPCOMING_COMPS, R.plurals.upcoming_competitions);

                if(inProgress != null) {
                    titles.add(inProgress);
                }

                if(upcoming != null) {
                    titles.add(upcoming);
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Opens the first tab
                        expendFirst(titles);
                        adapter.refreshData(titles);
                    }
                });

            }
        });
    }

    /**
     * Treats the data to regroup them
     *
     * Afin de résoudre le bug des Ressources, vérifie bien si le fragment est attaché à l'activité
     * @param document HTML code
     * @param tag To reconize a record
     * @param titleRes Group title
     * @return Group
     */
    private Title treatData(Document document, String tag, @PluralsRes int titleRes) {
        final ArrayList<Competition> competitions = CompetitionProvider.getCompetition(document, tag);

        if (competitions.size() != 0) {

            if(isAdded()) {

                String title = getResources().getQuantityString(titleRes, competitions.size(), competitions.size());
                return new Title(title, competitions);

            }

            return new Title(null, competitions);
        }

        return null;
    }

    /**
     * Opens the first tab
     */
    private void expendFirst(ArrayList<Title> titles) {
        if(titles.size() != 0)
            titles.get(0).expend();
    }
}
