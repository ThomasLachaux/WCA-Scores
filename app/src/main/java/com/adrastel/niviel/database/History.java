package com.adrastel.niviel.database;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.adrastel.niviel.HistoryModel;
import com.adrastel.niviel.models.writeable.BufferHistory;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class History implements HistoryModel {

    public static final Factory<History> FACTORY = new Factory<>(new Creator<History>() {
        @Override
        public History create(long _id, long follower, @NonNull String event, @NonNull String competition, @NonNull String round, @NonNull String place, @Nullable String best, @Nullable String average, @Nullable String result_details) {
            return new AutoValue_History(_id, follower, event, competition, round, place, best, average, result_details);
        }
    });

    public static final RowMapper<History> SELECT_ALL_MAPPER = FACTORY.select_from_followerMapper();

    public com.adrastel.niviel.models.readable.history.History toHistoryModel() {
        BufferHistory history = new BufferHistory();

        history.setEvent(event());
        history.setCompetition(competition());
        history.setRound(round());
        history.setPlace(place());
        history.setBest(best());
        history.setAverage(average());
        history.setResult_details(result_details());

        return history;
    }

}
