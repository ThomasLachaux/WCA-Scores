package com.adrastel.niviel.models.writeable;

import com.adrastel.niviel.models.readable.history.History;

public class BufferHistory extends History {

    public BufferHistory() {
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setBest(String best) {
        this.best = best;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public void setResult_details(String result_details) {
        this.result_details = result_details;
    }
}
