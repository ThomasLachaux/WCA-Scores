package com.adrastel.niviel.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.adrastel.niviel.adapters.RankingAdapter;
import com.adrastel.niviel.assets.Cubes;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.dialogs.RankingSwitchCountryDialog;
import com.adrastel.niviel.dialogs.RankingSwitchCubeDialog;
import com.adrastel.niviel.models.readable.Ranking;
import com.adrastel.niviel.providers.RankingProvider;
import com.adrastel.niviel.views.RecyclerViewCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.HttpUrl;

public class RankingFragment extends BaseFragment {

    public static final String ISSINGLE = "issingle";
    public static final String RANKING = "ranking";
    public static final String CUBE_POSITION = "cube_position";
    public static final String SUBTITLE = "subtitle";
    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerViewCompat recyclerView;
    @BindView(R.id.empty_view) TextView emptyView;

    private Unbinder unbinder;
    private RankingAdapter adapter;

    // Identifies the event
    private int cubePosition = 0;

    // Identifies the country
    private int countryPosition = 0;

    // identifies the mode (single or average)
    private boolean isSingle = true;
    private String subtitle = null;


    public static RankingFragment newInstance() {
        return new RankingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        adapter = new RankingAdapter(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        recyclerView.initRecyclerViewCompat(swipeRefreshLayout, progressBar, emptyView);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            cubePosition = savedInstanceState.getInt(CUBE_POSITION, 0);
            isSingle = savedInstanceState.getBoolean(ISSINGLE, true);

            subtitle = savedInstanceState.getString(SUBTITLE, null);
            activity.setSubtitle(subtitle);

            ArrayList<Ranking> rankings = savedInstanceState.getParcelableArrayList(RANKING);

            adapter.setSingle(isSingle);

            adapter.refreshData(rankings);

            recyclerView.showRecycler();

        }


        else if(isConnected()) {
            callData();
        }

        else {
            recyclerView.showEmpty();
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callData();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        adapter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(CUBE_POSITION, cubePosition);
        outState.putBoolean(ISSINGLE, isSingle);
        outState.putString(SUBTITLE, subtitle);
        outState.putParcelableArrayList(RANKING, adapter.getDatas());

        setTargetFragment(null, -1);

        super.onSaveInstanceState(outState);
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
                RankingSwitchCubeDialog cubeSwitch = RankingSwitchCubeDialog.newInstance(cubePosition);
                cubeSwitch.show(getFragmentManager(), "cubeSwitch");

                cubeSwitch.setListener(new RankingSwitchCubeDialog.RankingSwitchCubeListener() {
                    @Override
                    public void onClick(int position, boolean isSingle) {
                        RankingFragment.this.cubePosition = position;
                        RankingFragment.this.isSingle = isSingle;
                        callData();
                    }
                });

                return true;

            case R.id.switch_country:

                RankingSwitchCountryDialog countrySwitch = RankingSwitchCountryDialog.newInstance(countryPosition);
                countrySwitch.show(getFragmentManager(), "countrySwitch");

                countrySwitch.setListener(new RankingSwitchCountryDialog.RankingSwitchCountryListener() {
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

    public void callData() {

        HttpUrl url = new WcaUrl()
                .ranking(Cubes.getCubeId(cubePosition), getResources().getStringArray(R.array.countries_id)[countryPosition], isSingle)
                .build();

        adapter.setSingle(isSingle);

        String[] cubes = getResources().getStringArray(R.array.cubes);
        String mode = isSingle ? getString(R.string.single) : getString(R.string.average);
        subtitle = getString(R.string.two_infos, cubes[cubePosition], mode);

        activity.setSubtitle(subtitle);

        recyclerView.callData(url, new RecyclerViewCompat.SuccessCallback() {
            @Override
            public void onSuccess(String response) {
                Document document = Jsoup.parse(response);
                final ArrayList<Ranking> rankings = RankingProvider.getRanking(document);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshData(rankings);
                    }
                });
            }
        });
    }

    @Override
    public int getStyle() {
        return R.style.AppTheme_Ranking;
    }


}