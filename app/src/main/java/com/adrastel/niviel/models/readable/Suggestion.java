package com.adrastel.niviel.models.readable;

import com.adrastel.niviel.models.BaseModel;

public class Suggestion extends BaseModel {

    private String name;
    private String wca_id;

    public Suggestion(String name, String wca_id) {
        this.name = name;
        this.wca_id = wca_id;
    }

    public String getName() {
        return name;
    }

    public String getWca_id() {
        return wca_id;
    }
}
