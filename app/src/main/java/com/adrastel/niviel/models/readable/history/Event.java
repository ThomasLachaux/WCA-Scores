package com.adrastel.niviel.models.readable.history;

import android.os.Parcel;
import android.os.Parcelable;

import com.adrastel.niviel.assets.Cubes;
import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Event implements Parent<History>, Parcelable {

    private String title;
    private boolean sortByEvent;
    private ArrayList<History> histories;

    public Event(String title, boolean sortByEvent, ArrayList<History> histories) {
        this.title = title;
        this.sortByEvent = sortByEvent;
        this.histories = histories;
    }

    protected Event(Parcel in) {
        title = in.readString();
        sortByEvent = in.readByte() != 0;
        histories = in.createTypedArrayList(History.CREATOR);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public List<History> getChildList() {
        return histories;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSortByEvent() {
        return sortByEvent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeByte((byte) (sortByEvent ? 1 : 0));
        parcel.writeTypedList(histories);
    }

    public static class ComparatorByEvent implements Comparator<Event> {

        @Override
        public int compare(Event event, Event t1) {
            return Cubes.getCubeId(event.getTitle()) - Cubes.getCubeId(t1.getTitle());
        }
    }

    public static class ComparatorByName implements Comparator<Event> {
        @Override
        public int compare(Event event, Event t1) {
            return event.getTitle().compareTo(t1.getTitle());
        }
    }

}