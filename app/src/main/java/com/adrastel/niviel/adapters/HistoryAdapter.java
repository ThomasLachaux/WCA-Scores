package com.adrastel.niviel.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Cubes;
import com.adrastel.niviel.dialogs.HistoryDialog;
import com.adrastel.niviel.models.readable.history.Event;
import com.adrastel.niviel.models.readable.history.History;
import com.adrastel.niviel.views.CircleView;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryAdapter extends BaseExpandableAdapter<Event, History, HistoryAdapter.EventViewHolder, HistoryAdapter.HistoryViewHolder> {

    private LayoutInflater inflater;
    private boolean sortByEvent = true;

    public HistoryAdapter(FragmentActivity activity, ArrayList<Event> events) {
        super(activity, events);

        inflater = LayoutInflater.from(activity);
    }

    @SuppressWarnings("WeakerAccess")
    static class EventViewHolder extends ParentViewHolder<Event, History> {

        public CircleView place;
        public TextView title;
        public TextView results;
        public ImageButton more;
        public ImageView cube;
        public LinearLayout root;

        public EventViewHolder(View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.place);
            title = (TextView) itemView.findViewById(R.id.first_line);
            results = (TextView) itemView.findViewById(R.id.second_line);
            more = (ImageButton) itemView.findViewById(R.id.more);
            cube = (ImageView) itemView.findViewById(R.id.cube_image);
            root = (LinearLayout) itemView.findViewById(R.id.root_layout);
        }
    }

    @SuppressWarnings("WeakerAccess")
    static class HistoryViewHolder extends ChildViewHolder<History> {

        public CircleView place;
        public TextView competition;
        public TextView results;
        public ImageButton more;
        public LinearLayout root;

        public HistoryViewHolder(View itemView) {
            super(itemView);

            place = (CircleView) itemView.findViewById(R.id.place);
            competition = (TextView) itemView.findViewById(R.id.first_line);
            results = (TextView) itemView.findViewById(R.id.second_line);
            more = (ImageButton) itemView.findViewById(R.id.more);
            root = (LinearLayout) itemView.findViewById(R.id.root_layout);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View parentView = inflater.inflate(R.layout.adapter_list_avatar, parentViewGroup, false);

        return new EventViewHolder(parentView);
    }

    /**
     * Inflate le layout enfant
     * @param childViewGroup Vue parent
     * @param viewType Type de la vue
     * @return Vue enfant
     */
    @NonNull
    @Override
    public HistoryViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View childView = inflater.inflate(R.layout.adapter_list_avatar, childViewGroup, false);

        return new HistoryViewHolder(childView);
    }

    @Override
    @SuppressLint("PrivateResource")
    @SuppressWarnings("deprecation")
    public void onBindParentViewHolder(@NonNull final EventViewHolder parentViewHolder, int parentPosition, @NonNull Event parent) {

        // Comme tous les parents possède le meme sortByEvent, on peut mettre cette variable en global
        sortByEvent = parent.isSortByEvent();

        parentViewHolder.results.setVisibility(View.GONE);
        parentViewHolder.place.setVisibility(View.GONE);
        parentViewHolder.more.setVisibility(View.GONE);

        TextView title = parentViewHolder.title;
        title.setText(getActivity().getString(R.string.string_details_number, parent.getTitle(), parent.getChildList().size()));
        title.setPadding(Assets.dpToPx(getActivity(), 20), 0, 0, 0);
        title.setTextSize(Assets.spToPx(getActivity(), 12));

        title.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Large);

        ImageView cube = parentViewHolder.cube;



        if(sortByEvent) {
            cube.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(Cubes.getImage(parent.getTitle()))
                    .fit()
                    .centerInside()
                    .into(cube);
        } else {
            cube.setVisibility(View.GONE);
        }


    }

    @Override
    public void onBindChildViewHolder(@NonNull HistoryViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull final History child) {

        String event = child.getEvent();
        String round = child.getRound();
        String competition = child.getCompetition();
        String place = child.getPlace();
        String average = child.getAverage();
        String result_details = child.getResult_details();

        childViewHolder.more.setVisibility(View.GONE);

        childViewHolder.root.setBackgroundColor(Assets.getColor(getActivity(), R.color.background_child_expanded_recycler_view));

        String title = getActivity().getString(R.string.string_details_string, sortByEvent ? competition : event, round);
        childViewHolder.competition.setText(title);

        childViewHolder.place.setText(place);
        childViewHolder.results.setText(Assets.formatHtmlAverageDetails(average, result_details));

        childViewHolder.place.setBackground(Assets.getColor(getActivity(), R.color.indigo_300));


        childViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDetails(getActivity().getSupportFragmentManager(), child);
            }
        });
    }

    private void onDetails(FragmentManager fragmentManager, History history) {

        if(fragmentManager != null) {

            DialogFragment historyDialog = HistoryDialog.newInstance(history);

            historyDialog.show(fragmentManager, "historyDetailsDialog");

        }

    }

    @Override
    public void refreshData(ArrayList<Event> datas) {
        Collections.sort(datas, new Event.ComparatorByEvent());

        super.refreshData(datas);
    }
}