package com.adrastel.niviel.database;

import android.support.annotation.NonNull;

import com.adrastel.niviel.RecordModel;
import com.adrastel.niviel.models.writeable.BufferRecord;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Record implements RecordModel {

    public static final Factory<Record> FACTORY = new Factory<>(new Creator<Record>() {
        @Override
        public Record create(long _id, long follower, @NonNull String event, String single, long nr_single, long cr_single, long wr_single, String average, long nr_average, long cr_average, long wr_average) {
            return new AutoValue_Record(_id, follower, event, single, nr_single, cr_single, wr_single, average, nr_average, cr_average, wr_average);
        }
    });

    public static final RowMapper<Record> SELECT_FROM_FOLLOWER_MAPPER = FACTORY.select_from_followerMapper();

    public static class Comparator implements java.util.Comparator<Record> {

        @Override
        public int compare(Record record, Record t1) {
            return record.event().compareTo(t1.event());
        }
    }

    public com.adrastel.niviel.models.readable.Record toRecordModel() {

        BufferRecord record = new BufferRecord();
        record.setEvent(event());
        record.setSingle(single());
        record.setNr_single(String.valueOf(nr_single()));
        record.setCr_single(String.valueOf(cr_single()));
        record.setWr_single(String.valueOf(wr_single()));
        record.setAverage(average());
        record.setNr_average(String.valueOf(nr_average()));
        record.setCr_average(String.valueOf(cr_average()));
        record.setWr_average(String.valueOf(wr_average()));

        return record;
    }

}
