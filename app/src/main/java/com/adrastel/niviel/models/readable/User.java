package com.adrastel.niviel.models.readable;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.models.BaseModel;

public class User extends BaseModel implements Parcelable {

    private String name;
    private String country;
    private String wca_id;
    private String gender;
    private String competitions;

    @Nullable
    private String picture;

    public User(String name, String country, String wca_id, String gender, String competitions, @Nullable String picture) {
        this.name = name;
        this.country = country;
        this.wca_id = wca_id;
        this.gender = gender;
        this.competitions = competitions;
        this.picture = picture;
    }

    public User(Follower follower) {
        this.name = follower.name();
        this.country = follower.country();
        this.wca_id = follower.wca_id();
        this.gender = follower.gender();
        this.competitions = follower.competitions();
    }

    protected User(Parcel in) {
        name = in.readString();
        country = in.readString();
        wca_id = in.readString();
        gender = in.readString();
        competitions = in.readString();
        picture = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getWca_id() {
        return wca_id;
    }

    public String getGender() {
        return gender;
    }

    public String getCompetitions() {
        return competitions;
    }

    @Nullable
    public String getPicture() {
        return picture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(country);
        parcel.writeString(wca_id);
        parcel.writeString(gender);
        parcel.writeString(competitions);
        parcel.writeString(picture);
    }
}
