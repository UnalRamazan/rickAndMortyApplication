package com.example.rickandmortyapp.Entity.LocationDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Location {
    @SerializedName("info")
    @Expose
    private InfoForLocations info;

    @SerializedName("results")
    @Expose
    private ArrayList<ResultForLocation> resultForLocations;

    public InfoForLocations getInfo() {
        return info;
    }

    public void setInfo(InfoForLocations info) {
        this.info = info;
    }

    public ArrayList<ResultForLocation> getResults() {
        return resultForLocations;
    }

    public void setResults(ArrayList<ResultForLocation> resultForLocations) {
        this.resultForLocations = resultForLocations;
    }
}