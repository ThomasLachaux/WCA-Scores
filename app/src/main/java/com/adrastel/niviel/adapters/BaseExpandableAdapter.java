package com.adrastel.niviel.adapters;

import android.support.v4.app.FragmentActivity;

import com.adrastel.niviel.activities.MainActivity;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;

public abstract class BaseExpandableAdapter<P extends Parent<C>, C, PVH extends ParentViewHolder<P, C>, CVH extends ChildViewHolder<C>> extends ExpandableRecyclerAdapter<P, C, PVH, CVH> {

    private MainActivity activity;

    public BaseExpandableAdapter(FragmentActivity activity, ArrayList<P> datas) {
        super(datas);

        try {
            this.activity = (MainActivity) activity;
        }

        catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    protected MainActivity getActivity() {
        return activity;
    }

    public ArrayList<P> getDatas() {
        return (ArrayList<P>) getParentList();
    }

    public void refreshData(ArrayList<P> datas) {

        getDatas().clear();
        getDatas().addAll(datas);
        notifyParentDataSetChanged(true);
    }
}