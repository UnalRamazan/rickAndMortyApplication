package com.example.rickandmortyapp.Entity.LocationDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InfoForLocations {

    @SerializedName("count")
    @Expose
    private Integer count;

    @SerializedName("pages")
    @Expose
    private Integer pages;

    @SerializedName("next")
    @Expose
    private String next;

    @SerializedName("prev")
    @Expose
    private Object prev;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Object getPrev() {
        return prev;
    }

    public void setPrev(Object prev) {
        this.prev = prev;
    }
}