package com.adrastel.niviel.adapters;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.SearchActivity;
import com.adrastel.niviel.models.readable.Suggestion;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAdapter extends WebAdapter<SearchAdapter.ViewHolder, Suggestion> {

    public SearchAdapter(FragmentActivity activity) {
        super(activity);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_search, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {

            final Suggestion suggestion = getDatas().get(position);

            holder.name.setText(suggestion.getName());
            holder.wca_id.setText(suggestion.getWca_id());

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra(SearchActivity.NAME, suggestion.getName());
                    intent.putExtra(SearchActivity.WCA_ID, suggestion.getWca_id());

                    getActivity().setResult(SearchActivity.SEARCH_SUCCESS, intent);
                    getActivity().finish();
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return getDatas().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_layout) LinearLayout root;
        @BindView(R.id.first_line) TextView name;
        @BindView(R.id.second_line) TextView wca_id;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
