package com.adrastel.niviel.models.readable;

import android.os.Parcel;
import android.os.Parcelable;

import com.adrastel.niviel.models.BaseModel;

public class Ranking extends BaseModel implements Parcelable {

    protected String wca_id;
    protected String rank;
    protected String person;
    protected String result;
    protected String citizen;
    protected String competition;
    protected String details;

    public Ranking() {}

    protected Ranking(Parcel in) {
        wca_id = in.readString();
        rank = in.readString();
        person = in.readString();
        result = in.readString();
        citizen = in.readString();
        competition = in.readString();
        details = in.readString();
    }

    public static final Creator<Ranking> CREATOR = new Creator<Ranking>() {
        @Override
        public Ranking createFromParcel(Parcel in) {
            return new Ranking(in);
        }

        @Override
        public Ranking[] newArray(int size) {
            return new Ranking[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(wca_id);
        parcel.writeString(rank);
        parcel.writeString(person);
        parcel.writeString(result);
        parcel.writeString(citizen);
        parcel.writeString(competition);
        parcel.writeString(details);
    }

    public String getWca_id() {
        return wca_id;
    }

    public String getRank() {
        return rank;
    }


    public String getPerson() {
        return person;
    }

    public String getResult() {
        return result;
    }

    public String getCitizen() {
        return citizen;
    }

    public String getCompetition() {
        return competition;
    }

    public String getDetails() {
        return details;
    }
}
