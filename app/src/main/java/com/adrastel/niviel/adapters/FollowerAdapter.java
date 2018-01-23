package com.adrastel.niviel.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
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
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.fragments.ProfileFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowerAdapter extends BaseAdapter<FollowerAdapter.ViewHolder> {

    private ArrayList<Follower> followers;

    public FollowerAdapter(FragmentActivity activity, ArrayList<Follower> followers) {
        super(activity);
        this.followers = followers;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.first_line) TextView firstLine;
        @BindView(R.id.second_line) TextView secondLine;
        @BindView(R.id.more) ImageButton more;
        @BindView(R.id.root_layout) LinearLayout click_area;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public FollowerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.adapter_follower, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FollowerAdapter.ViewHolder holder, int position) {

        final Follower follower = followers.get(position);

        holder.firstLine.setText(follower.name());
        holder.secondLine.setText(follower.wca_id());


        holder.click_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getMainActivity(), R.string.goto_profile_tip, Toast.LENGTH_LONG).show();
            }
        });

        holder.click_area.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ProfileFragment fragment = ProfileFragment.newInstance(follower._id());
                getMainActivity().switchFragment(fragment);
                return true;
            }
        });

        loadMenu(holder, follower);
    }

    private void loadMenu(ViewHolder holder, final Follower follower) {

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                PopupMenu menu = new PopupMenu(view.getContext(), view);

                menu.inflate(R.menu.menu_pop_follower);

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.unfollow:
                                onUnfollow(view.getContext(), follower);
                                return true;
                        }

                        return false;
                    }
                });

                menu.show();

            }
        });

    }

    private void onUnfollow(Context context, Follower follower) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        helper.deleteFollower(follower._id());
        followers.remove(follower);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }
}
