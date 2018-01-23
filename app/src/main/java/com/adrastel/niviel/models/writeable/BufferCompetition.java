package com.adrastel.niviel.models.writeable;

import com.adrastel.niviel.models.readable.competition.Competition;

public class BufferCompetition extends Competition {


    public BufferCompetition() {
    }

    public BufferCompetition setDate(String date) {
        this.date = date;
        return this;
    }

    public BufferCompetition setCompetition(String name) {
        this.competition = name;
        return this;
    }

    public BufferCompetition setCompetition_link(String competition_link) {
        this.competition_link = competition_link;
        return this;
    }

    public BufferCompetition setCountry(String country) {
        this.country = country;
        return this;
    }

    public BufferCompetition setPlace(String place) {
        this.place = place;
        return this;
    }

}
