package com.adrastel.niviel.models.readable.competition;

import android.os.Parcel;
import android.os.Parcelable;

import com.adrastel.niviel.models.BaseModel;

public class Competition extends BaseModel implements Parcelable {


    protected String date;
    protected String competition;
    protected String competition_link;
    protected String country;
    protected String place;

    protected Competition() {
    }

    private Competition(Parcel in) {
        date = in.readString();
        competition = in.readString();
        competition_link = in.readString();
        country = in.readString();
        place = in.readString();
    }

    public static final Creator<Competition> CREATOR = new Creator<Competition>() {
        @Override
        public Competition createFromParcel(Parcel in) {
            return new Competition(in);
        }

        @Override
        public Competition[] newArray(int size) {
            return new Competition[size];
        }
    };

    public String getDate() {
        return date;
    }

    public String getCompetition() {
        return competition;
    }

    public String getCompetition_link() {
        return competition_link;
    }

    public String getCountry() {
        return country;
    }

    public String getPlace() {
        return place;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(competition);
        parcel.writeString(competition_link);
        parcel.writeString(country);
        parcel.writeString(place);
    }
}
