package com.adrastel.niviel.models.readable.competition;

import android.os.Parcel;
import android.os.Parcelable;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

public class Title implements Parent<Competition>, Parcelable {

    private String title;
    private ArrayList<Competition> competitions;
    private boolean isExpanded = false;

    public Title(String title, ArrayList<Competition> competitions) {
        this.title = title;
        this.competitions = competitions;
    }

    protected Title(Parcel in) {
        title = in.readString();
        competitions = in.createTypedArrayList(Competition.CREATOR);
    }

    public static final Creator<Title> CREATOR = new Creator<Title>() {
        @Override
        public Title createFromParcel(Parcel in) {
            return new Title(in);
        }

        @Override
        public Title[] newArray(int size) {
            return new Title[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void expend() {
        isExpanded = true;
    }

    @Override
    public List<Competition> getChildList() {
        return competitions;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return isExpanded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeTypedList(competitions);
    }
}
