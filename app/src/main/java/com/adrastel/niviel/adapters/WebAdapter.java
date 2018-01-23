package com.adrastel.niviel.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

import com.adrastel.niviel.models.BaseModel;

import java.util.ArrayList;

public abstract class WebAdapter<T extends RecyclerView.ViewHolder, M extends BaseModel> extends BaseAdapter<T> {

    private ArrayList<M> datas = new ArrayList<>();

    public WebAdapter(FragmentActivity activity) {
        super(activity);
    }


    public void refreshData(ArrayList<M> datas) {

        this.datas.clear();
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    public ArrayList<M> getDatas() {
        return datas;
    }
}
