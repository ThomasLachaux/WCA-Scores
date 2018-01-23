package com.adrastel.niviel.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.models.readable.competition.Competition;
import com.adrastel.niviel.models.readable.competition.Title;
import com.adrastel.niviel.views.CircleView;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import java.util.ArrayList;

public class CompetitionAdapter extends BaseExpandableAdapter<Title, Competition, CompetitionAdapter.TitleViewHolder, CompetitionAdapter.CompetitionViewHolder> {

    private LayoutInflater inflater;

    public CompetitionAdapter(FragmentActivity activity, ArrayList<Title> datas) {
        super(activity, datas);

        inflater = activity.getLayoutInflater();
    }

    static class TitleViewHolder extends ParentViewHolder<Title, Competition> {

        public CircleView place;
        public TextView title;
        public TextView results;
        public ImageButton more;
        public ImageView cube;
        public LinearLayout root;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.place);
            title = (TextView) itemView.findViewById(R.id.first_line);
            results = (TextView) itemView.findViewById(R.id.second_line);
            more = (ImageButton) itemView.findViewById(R.id.more);
            cube = (ImageView) itemView.findViewById(R.id.cube_image);
            root = (LinearLayout) itemView.findViewById(R.id.root_layout);
        }
    }

    static class CompetitionViewHolder extends ChildViewHolder<Competition> {

        public TextView competition;
        public TextView location;
        public ImageButton more;
        public LinearLayout root;

        public CompetitionViewHolder(@NonNull View itemView) {
            super(itemView);

            competition = (TextView) itemView.findViewById(R.id.first_line);
            location = (TextView) itemView.findViewById(R.id.second_line);
            more = (ImageButton) itemView.findViewById(R.id.more);
            root = (LinearLayout) itemView.findViewById(R.id.root_layout);
        }
    }

    @NonNull
    @Override
    public TitleViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View parentView = inflater.inflate(R.layout.adapter_list_avatar, parentViewGroup, false);

        return new TitleViewHolder(parentView);
    }

    @NonNull
    @Override
    public CompetitionViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View childView = inflater.inflate(R.layout.adapter_list, childViewGroup, false);

        return new CompetitionViewHolder(childView);
    }

    @SuppressLint("PrivateResource")
    @SuppressWarnings("deprecation")
    @Override
    public void onBindParentViewHolder(@NonNull TitleViewHolder parentViewHolder, int parentPosition, @NonNull Title parent) {

        parentViewHolder.results.setVisibility(View.GONE);
        parentViewHolder.place.setVisibility(View.GONE);
        parentViewHolder.more.setVisibility(View.GONE);
        TextView title = parentViewHolder.title;
        title.setText(parent.getTitle());
        title.setPadding(Assets.dpToPx(getActivity(), 10), 10, 0, 0);
        title.setTextSize(Assets.spToPx(getActivity(), 12));
        title.setGravity(Gravity.CENTER_HORIZONTAL);

        title.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Large);

        ImageView cube = parentViewHolder.cube;
        cube.setVisibility(View.VISIBLE);
        cube.setImageResource(R.drawable.ic_competition_black);

    }

    @Override
    public void onBindChildViewHolder(@NonNull CompetitionViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull final Competition child) {

        childViewHolder.competition.setText(getActivity().getString(R.string.string_details_string, child.getCompetition(), child.getDate()));
        childViewHolder.location.setText(child.getCountry());

        childViewHolder.root.setBackgroundColor(Assets.getColor(getActivity(), R.color.background_child_expanded_recycler_view));


        childViewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMenu(view, child);
            }
        });

        childViewHolder.more.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(child.getPlace() != null)
                    Toast.makeText(getActivity(), child.getPlace(), Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    private void loadMenu(View view, final Competition child) {

        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.menu_pop_competition);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.goto_maps:
                        gotoMaps(child);
                        return true;

                    case R.id.goto_internet:
                        gotoInternet(child);
                        return true;
                }

                return false;
            }
        });

        popupMenu.show();

    }

    private void gotoMaps(Competition child) {
        String address = "";

        address += child.getPlace() != null ? child.getPlace() : "";
        address += child.getCountry() != null ? child.getCountry() : "";

        Uri uri = Uri.parse("geo:0,0?q=" + address);

        Intent goToMaps = new Intent(Intent.ACTION_VIEW, uri);

        getActivity().startActivity(goToMaps);
    }

    private void gotoInternet(Competition child) {

        Uri uri = new WcaUrl().competition(child.getCompetition_link()).toUri();
        Intent gotoInteret = new Intent(Intent.ACTION_VIEW, uri);

        if(gotoInteret.resolveActivity(getActivity().getPackageManager()) != null) {
            getActivity().startActivity(gotoInteret);
        }

    }
}
