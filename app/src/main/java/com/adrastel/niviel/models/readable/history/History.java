package com.adrastel.niviel.models.readable.history;

import android.os.Parcel;
import android.os.Parcelable;

import com.adrastel.niviel.models.BaseModel;

import java.util.Comparator;

public class History extends BaseModel implements Parcelable {

    /**
     * Le type de cube
     */
    protected String event;

    /**
     * Le nom de la competition
     */
    protected String competition;

    /**
     * Le round (premier ou dexieme
     */
    protected String round;

    /**
     * Classement
     */
    protected String place;

    /**
     * Meilleur temps
     */
    protected String best;

    /**
     * temps en moyenne
     */
    protected String average;

    /**
     * tous les temps
     */
    protected String result_details;

    // Constructors

    /**
     * Contrcteur vide
     */
    public History() {}

    public History(History history) {
        event = history.getEvent();
        competition = history.getCompetition();
        round = history.getRound();
        place = history.getPlace();
        best = history.getBest();
        average = history.getAverage();
        result_details = history.getResult_details();
    }

    protected History(Parcel in) {
        event = in.readString();
        competition = in.readString();
        round = in.readString();
        place = in.readString();
        best = in.readString();
        average = in.readString();
        result_details = in.readString();
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(event);
        parcel.writeString(competition);
        parcel.writeString(round);
        parcel.writeString(place);
        parcel.writeString(best);
        parcel.writeString(average);
        parcel.writeString(result_details);
    }

    public String getEvent() {
        return event;
    }

    public String getCompetition() {
        return competition;
    }

    public String getRound() {
        return round;
    }

    public String getPlace() {
        return place;
    }

    public String getBest() {
        return best;
    }

    public String getAverage() {
        return average;
    }

    public String getResult_details() {
        return result_details;
    }

    public static class ComparatorByCompetition implements Comparator<History> {
        @Override
        public int compare(History history, History t1) {
            return history.getCompetition().compareTo(t1.getCompetition());
        }
    }


}
