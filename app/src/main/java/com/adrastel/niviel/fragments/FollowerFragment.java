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
import com.adrastel.niviel.adapters.FollowerAdapter;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.views.RecyclerViewCompat;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FollowerFragment extends BaseFragment {


    @BindView(R.id.recycler_view) RecyclerViewCompat recyclerView;
    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.empty_view) TextView emptyView;

    private Unbinder unbinder;

    @Override
    public int getStyle() {
        return R.style.AppTheme_Followers;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setEnabled(false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.initRecyclerViewCompat(swipeRefreshLayout, progressBar, emptyView);

        recyclerView.hideAll();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        long personal_id = preferences.getLong(getString(R.string.pref_personal_id), -1);

        DatabaseHelper db = DatabaseHelper.getInstance(getContext());
        ArrayList<Follower> followers = db.selectAllFollowers();
        Follower personalProfile = null;


        // Moves the personal profile at the top of the list
        for(Follower follower : followers) {
            if(follower._id() == personal_id) {
                personalProfile = follower;
            }
        }

        // Si l'utilisateur possède un profil personnel, le supprime de la liste
        if(personalProfile != null)
            followers.remove(personalProfile);

        Collections.sort(followers, new Follower.Comparator());

        FollowerAdapter adapter = new FollowerAdapter(getActivity(), followers);
        recyclerView.setAdapter(adapter);
        recyclerView.showRecycler();

        if(followers.size() == 0) {
            emptyView.setText(R.string.no_followers);
            recyclerView.showEmpty();
        }
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
