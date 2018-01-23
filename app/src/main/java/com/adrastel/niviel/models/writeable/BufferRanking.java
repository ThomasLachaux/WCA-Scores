package com.adrastel.niviel.models.writeable;

import com.adrastel.niviel.models.readable.Ranking;

public class BufferRanking extends Ranking {

    public void setWca_id(String wca_id) {
        this.wca_id = wca_id;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setCitizen(String citizen) {
        this.citizen = citizen;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
