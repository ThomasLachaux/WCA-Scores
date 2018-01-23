package com.adrastel.niviel.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Cubes;
import com.adrastel.niviel.assets.DetailsMaker;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.dialogs.RecordDialog;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.models.readable.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordAdapter extends WebAdapter<RecordAdapter.ViewHolder, Record> {

    private User user;

    public RecordAdapter(FragmentActivity activity) {
        super(activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.event) TextView event;
        @BindView(R.id.record) TextView single;
        @BindView(R.id.cube_image) ImageView image;
        @BindView(R.id.more_info) Button more_info;
        @BindView(R.id.share) Button share;
        @BindView(R.id.card) CardView card;

        // view holder
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(getMainActivity());

        View view = inflater.inflate(R.layout.adapter_record, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Si il s'agit du header ou non
        if(isHeader(position)) {
            if(user != null) {

                Resources resources = getMainActivity().getResources();

                if(user.getPicture() != null) {

                    holder.image.setVisibility(View.VISIBLE);
                    Uri uri = new WcaUrl()
                            .localeUrl(user.getPicture())
                            .toUri();

                    Picasso.with(getMainActivity())
                            .load(uri)
                            .fit()
                            .centerInside()
                            .into(holder.image);


                } else
                    holder.image.setVisibility(View.GONE);

                holder.event.setText(getMainActivity().getString(R.string.two_infos, user.getName(), user.getWca_id()));
                holder.event.setTextSize(Assets.spToPx(getMainActivity(), 9));

                holder.more_info.setText(R.string.online);
                holder.more_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Uri url = new WcaUrl()
                                .profile(user.getWca_id())
                                .toUri();

                        Intent viewOnWebSite = new Intent(Intent.ACTION_VIEW);
                        viewOnWebSite.setData(url);

                        getMainActivity().startActivity(viewOnWebSite);
                    }
                });

                DetailsMaker detailsMaker = new DetailsMaker(getMainActivity());

                try {
                    int competitions = Integer.parseInt(user.getCompetitions());
                    detailsMaker.add(resources.getQuantityString(R.plurals.competitions, competitions, competitions));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                detailsMaker.br();
                detailsMaker.add(R.string.country, user.getCountry());

                // Traduction du gendre
                String gender = user.getGender();

                if(gender.equalsIgnoreCase("Male"))
                    detailsMaker.add(R.string.gender, R.string.male);

                else if(gender.equalsIgnoreCase("Female"))
                    detailsMaker.add(R.string.gender, R.string.female);

                else
                    detailsMaker.add(R.string.gender, gender);


                holder.single.setText(detailsMaker.build());

                holder.share.setVisibility(View.VISIBLE);
                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = new WcaUrl()
                                .profile(user.getWca_id())
                                .toString();

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, url);

                        getMainActivity().startActivity(Intent.createChooser(share, getString(R.string.share)));
                    }
                });
            }

        }
        else {
            final int cubePosition = position - 1;
            final Record record = getDatas().get(cubePosition);

            final String event = record.getEvent();
            final String single = record.getSingle();
            final String average = record.getAverage();
            final int image_resource = Cubes.getImage(event);

            holder.event.setText(event);
            holder.event.setGravity(Gravity.LEFT);
            holder.event.setTextSize(Assets.spToPx(getMainActivity(), 18));
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setClickable(false);
            holder.image.setOnClickListener(null);
            holder.more_info.setText(R.string.more_info);
            holder.share.setVisibility(View.GONE);

            DetailsMaker detailsMaker = new DetailsMaker(getMainActivity());
            
            detailsMaker.add(R.string.single, single);

            if (average != null && !average.equals("")) {
                detailsMaker.add(R.string.average, average);
            }

            // Space
            detailsMaker.br();

            // Choose the ranking type to display for single
            try {

                int wr = Integer.parseInt(record.getWr_single());

                if(wr < 100) {
                    detailsMaker.add(getString(R.string.single_format, getString(R.string.record_wr)), record.getWr_single());
                }

                else {

                    int cr = Integer.parseInt(record.getCr_single());

                    if(cr < 100) {
                        detailsMaker.add(getString(R.string.single_format, getString(R.string.record_cr)), record.getCr_single());
                    }

                    else {
                        detailsMaker.add(getString(R.string.single_format, getString(R.string.record_nr)), record.getNr_single());
                    }
                }

            }

            catch (Exception e) {
                e.printStackTrace();
                detailsMaker.add(getString(R.string.single_format, getString(R.string.record_nr)), record.getNr_single());
            }

            // Choose the ranking type to display for average
            if(average != null) {
                try {

                    int wr = Integer.parseInt(record.getWr_average());

                    if (wr < 100) {
                        detailsMaker.add(getString(R.string.average_format, getString(R.string.record_wr)), record.getWr_average());
                    } else {

                        int cr = Integer.parseInt(record.getCr_average());

                        if (cr < 100) {
                            detailsMaker.add(getString(R.string.average_format, getString(R.string.record_cr)), record.getCr_average());
                        } else {
                            detailsMaker.add(getString(R.string.average_format, getString(R.string.record_nr)), record.getNr_average());
                        }
                    }

                } catch (Exception e) {
                    Log.w("Average non pris en compte sur " + event);
                }

            }

            holder.single.setText(detailsMaker.build(), TextView.BufferType.SPANNABLE);
            Picasso.with(getMainActivity())
                    .load(image_resource)
                    .fit()
                    .centerInside()
                    .into(holder.image);


            holder.more_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMoreInfoDialog(getMainActivity().getSupportFragmentManager(), record);
                }
            });

            holder.card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showMoreInfoDialog(getMainActivity().getSupportFragmentManager(), record);
                    return true;
                }
            });
        }


    }

    private boolean isHeader(int position) {
        return position == 0;
    }

    /**
     * If there isn't any records, doesn't create an header
     */
    @Override
    public int getItemCount() {
        int size = getDatas().size();

        return size != 0 ? size + 1 : 0;
    }

    public boolean refreshData(User user, ArrayList<Record> datas) {
        super.refreshData(datas);

        this.user = user;
        notifyItemChanged(0);

        return datas.size() != 0;
    }

    public User getUser() {
        return user;
    }

    private void showMoreInfoDialog(FragmentManager manager, Record record) {

        if(manager != null) {
            DialogFragment recordDialog = RecordDialog.newInstance(record);

            recordDialog.show(manager, "more_infos");

        }
    }
}
