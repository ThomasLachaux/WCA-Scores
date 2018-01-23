package com.adrastel.niviel.models.writeable;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.adrastel.niviel.R;
import com.adrastel.niviel.models.BaseModel;

public class OldNewRecord extends BaseModel implements Parcelable {

    public static int SINGLE = 0;
    public static int AVERAGE = 1;

    private Context context;
    private String event;
    private int type;
    private String oldTime;
    private String newTime;
    private String oldNr;
    private String oldCr;
    private String oldWr;
    private String newNr;
    private String newCr;
    private String newWr;

    public OldNewRecord(Context context, String event, int type, String oldTime, String newTime, long oldNr, long oldCr, long oldWr, String newNr, String newCr, String newWr) {
        this.context = context;
        this.event = event;
        this.type = type;
        this.oldTime = oldTime;
        this.newTime = newTime;
        this.oldNr = String.valueOf(oldNr);
        this.oldCr = String.valueOf(oldCr);
        this.oldWr = String.valueOf(oldWr);
        this.newNr = newNr;
        this.newCr = newCr;
        this.newWr = newWr;
    }

    protected OldNewRecord(Parcel in) {
        event = in.readString();
        type = in.readInt();
        oldTime = in.readString();
        newTime = in.readString();
        oldNr = in.readString();
        oldCr = in.readString();
        oldWr = in.readString();
        newNr = in.readString();
        newCr = in.readString();
        newWr = in.readString();
    }

    public static final Creator<OldNewRecord> CREATOR = new Creator<OldNewRecord>() {
        @Override
        public OldNewRecord createFromParcel(Parcel in) {
            return new OldNewRecord(in);
        }

        @Override
        public OldNewRecord[] newArray(int size) {
            return new OldNewRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(event);
        parcel.writeInt(type);
        parcel.writeString(oldTime);
        parcel.writeString(newTime);
        parcel.writeString(oldNr);
        parcel.writeString(oldCr);
        parcel.writeString(oldWr);
        parcel.writeString(newNr);
        parcel.writeString(newCr);
        parcel.writeString(newWr);
    }

    public String getEvent() {
        return event;
    }

    public String getType() {
        if(type == SINGLE) {
            return context.getString(R.string.single_colon);
        }

        else {
            return context.getString(R.string.average_colon);
        }
    }

    public String getOldTime() {
        return oldTime;
    }

    public String getNewTime() {
        return newTime;
    }

    public String getOldNr() {
        return oldNr;
    }

    public String getOldCr() {
        return oldCr;
    }

    public String getOldWr() {
        return oldWr;
    }

    public String getNewNr() {
        return newNr;
    }

    public String getNewCr() {
        return newCr;
    }

    public String getNewWr() {
        return newWr;
    }
}
