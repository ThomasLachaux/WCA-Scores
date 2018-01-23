package com.adrastel.niviel.models.readable;

import android.os.Parcel;
import android.os.Parcelable;

import com.adrastel.niviel.models.BaseModel;


public class Record extends BaseModel implements Parcelable {

    public static class Comparator implements java.util.Comparator<Record> {

        @Override
        public int compare(Record record, Record t1) {
            return record.getEvent().compareTo(t1.getEvent());
        }
    }

    protected String event;
    protected String single;
    protected String nr_single;
    protected String cr_single;
    protected String wr_single;
    protected String average;
    protected String nr_average;
    protected String cr_average;
    protected String wr_average;
    public Record() {}

    protected Record(Parcel in) {
        event = in.readString();
        single = in.readString();
        nr_single = in.readString();
        cr_single = in.readString();
        wr_single = in.readString();
        average = in.readString();
        nr_average = in.readString();
        cr_average = in.readString();
        wr_average = in.readString();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    public String getEvent() {
        return event;
    }

    public String getSingle() {
        return single;
    }

    public String getNr_single() {
        return nr_single;
    }

    public String getCr_single() {
        return cr_single;
    }

    public String getWr_single() {
        return wr_single;
    }

    public String getAverage() {
        return average;
    }

    public String getNr_average() {
        return nr_average;
    }

    public String getCr_average() {
        return cr_average;
    }

    public String getWr_average() {
        return wr_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(event);
        parcel.writeString(single);
        parcel.writeString(nr_single);
        parcel.writeString(cr_single);
        parcel.writeString(wr_single);
        parcel.writeString(average);
        parcel.writeString(nr_average);
        parcel.writeString(cr_average);
        parcel.writeString(wr_average);
    }
}
