package com.adrastel.niviel.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.dialogs.RankingDetailsDialog;
import com.adrastel.niviel.fragments.ProfileFragment;
import com.adrastel.niviel.models.readable.Ranking;
import com.adrastel.niviel.services.EditRecordService;
import com.adrastel.niviel.views.CircleView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingAdapter extends WebAdapter<RankingAdapter.ViewHolder, Ranking> {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getIntExtra(EditRecordService.ACTION, EditRecordService.ADD_RECORD_FAILURE)) {

                case EditRecordService.ADD_RECORD_SUCCESS:
                    notifyDataSetChanged();
                    break;

                case EditRecordService.ADD_RECORD_FAILURE:
                    notifyDataSetChanged();
                    break;
            }

        }
    };

    private boolean isSingle = true;

    public RankingAdapter(FragmentActivity activity) {
        super(activity);
    }

    public void onResume() {
        LocalBroadcastManager.getInstance(getMainActivity()).registerReceiver(receiver, new IntentFilter(EditRecordService.INTENT_FILTER));
    }

    public void onPause() {
        LocalBroadcastManager.getInstance(getMainActivity()).unregisterReceiver(receiver);
    }

    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_ranking, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RankingAdapter.ViewHolder holder, int position) {


        final Ranking ranking = getDatas().get(position);

        final String rank = ranking.getRank();
        final String person = ranking.getPerson();
        final String result = ranking.getResult();
        final String details = ranking.getDetails();
        final String country = ranking.getCitizen();

        holder.rank.setText(rank);
        holder.person.setText(getString(R.string.string_details_string, person, country));

        if (isSingle) {
            holder.result.setText(result);
        } else {

            holder.result.setText(Assets.formatHtmlAverageDetails(result, details));

        }

        final boolean isFollowing = Assets.isFollowing(getMainActivity(), ranking.getWca_id());

        invalidateCircleView(holder.rank, isFollowing);


        holder.click_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getMainActivity(), R.string.goto_profile_tip, Toast.LENGTH_LONG).show();
            }
        });

        holder.click_area.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                gotoProfile(ranking);
                return true;
            }
        });

        // charge le menu
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMenu(view, holder, ranking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getDatas().size();
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    private void loadMenu(View view, final RankingAdapter.ViewHolder holder, final Ranking ranking) {
        final boolean isFollowing = Assets.isFollowing(getMainActivity(), ranking.getWca_id());

        PopupMenu popupMenu = new PopupMenu(getMainActivity(), view);
        popupMenu.inflate(R.menu.menu_pop_ranking);


        String followTitle = isFollowing ? getString(R.string.unfollow) : getString(R.string.follow);

        MenuItem followButton = popupMenu.getMenu().findItem(R.id.follow);
        followButton.setTitle(followTitle);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.details:
                        onDetails(ranking);
                        return true;

                    case R.id.share:
                        onShare(ranking);
                        return true;

                    case R.id.follow:
                        if(isFollowing) {
                            item.setTitle(getString(R.string.follow));
                            onUnfollow(ranking);
                            invalidateCircleView(holder.rank, false);
                        }

                        else {
                            item.setTitle(getString(R.string.unfollow));
                            onFollow(ranking, holder);
                        }
                        return true;

                }

                return false;
            }
        });
        popupMenu.show();
    }

    private void onFollow(Ranking ranking, ViewHolder holder) {

        holder.more.setEnabled(false);

        Intent intent = new Intent(getMainActivity(), EditRecordService.class);
        intent.putExtra(EditRecordService.WCA_ID, ranking.getWca_id());
        intent.putExtra(EditRecordService.USERNAME, ranking.getPerson());
        getMainActivity().startService(intent);

    }

    private void onUnfollow(Ranking ranking) {

        Intent intent = new Intent(getMainActivity(), EditRecordService.class);
        intent.putExtra(EditRecordService.ACTION, EditRecordService.DELETE_FOLLOWER);
        intent.putExtra(EditRecordService.WCA_ID, ranking.getWca_id());
        intent.putExtra(EditRecordService.USERNAME, ranking.getPerson());

        getMainActivity().startService(intent);

    }

    private void invalidateCircleView(CircleView circleView, boolean isFollowing) {
        int color = isFollowing ? Assets.getColor(getMainActivity(), R.color.green_300) : Assets.getColor(getMainActivity(), R.color.teal_300);
        circleView.setBackground(color);
    }

    private void gotoProfile(Ranking ranking) {
        ProfileFragment fragment = ProfileFragment.newInstance(ranking.getWca_id(), ranking.getPerson());
        getMainActivity().switchFragment(fragment);

    }

    private void onDetails(Ranking ranking) {

        DialogFragment rankingDialog = RankingDetailsDialog.newInstance(ranking);
        rankingDialog.show(getMainActivity().getSupportFragmentManager(), "details");

    }

    private void onShare(Ranking ranking) {

        String url = new WcaUrl()
                .profile(ranking.getWca_id())
                .toString();

        Intent intent = new Intent();

        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);

        Intent chooser = Intent.createChooser(intent, getString(R.string.share));

        getMainActivity().startActivity(chooser);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.place) CircleView rank;
        @BindView(R.id.first_line) TextView person;
        @BindView(R.id.second_line) TextView result;
        @BindView(R.id.more) ImageButton more;
        @BindView(R.id.root_layout) LinearLayout click_area;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
