package com.example.rickandmortyapp.Entity.LocationDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultForLocation {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("dimension")
    @Expose
    private String dimension;

    @SerializedName("residents")
    @Expose
    private ArrayList<String> residents;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("created")
    @Expose
    private String created;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public ArrayList<String> getResidents() {
        return residents;
    }

    public void setResidents(ArrayList<String> residents) {
        this.residents = residents;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}