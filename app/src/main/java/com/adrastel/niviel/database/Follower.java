package com.adrastel.niviel.database;

import android.support.annotation.NonNull;

import com.adrastel.niviel.FollowerModel;
import com.adrastel.niviel.models.BaseModel;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Follower extends BaseModel implements FollowerModel {

    public static final Factory<Follower> FACTORY = new Factory<>(new Creator<Follower>() {
        @Override
        public Follower create(long _id, @NonNull String name, @NonNull String wca_id, @NonNull String country, @NonNull String gender, @NonNull String competitions) {
            return new AutoValue_Follower(_id, name, wca_id, country, gender, competitions);
        }
    });

    public static final RowMapper<Follower> SELECT_ALL_MAPPER = FACTORY.select_allMapper();
    public static final RowMapper<Long> SELECT_ID_FROM_WCA_MAPPER = FACTORY.select_id_from_wcaMapper();
    public static final RowMapper<Follower> SELECT_FOLLOWER_FROM_ID_MAPPER = FACTORY.select_follower_from_idMapper();

    public static class Comparator implements java.util.Comparator<Follower> {

        @Override
        public int compare(Follower follower, Follower t1) {
            return follower.name().compareTo(t1.name());
        }
    }
}
