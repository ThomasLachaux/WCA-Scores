package com.adrastel.niviel.models.writeable;

import com.adrastel.niviel.models.readable.Record;

public class BufferRecord extends Record {

    public BufferRecord() {
    }


    public void setEvent(String event) {
        this.event = event;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public void setNr_single(String nr_single) {
        this.nr_single = nr_single;
    }

    public void setCr_single(String cr_single) {
        this.cr_single = cr_single;
    }

    public void setWr_single(String wr_single) {
        this.wr_single = wr_single;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public void setNr_average(String nr_average) {
        this.nr_average = nr_average;
    }

    public void setCr_average(String cr_average) {
        this.cr_average = cr_average;
    }

    public void setWr_average(String wr_average) {
        this.wr_average = wr_average;
    }


}
