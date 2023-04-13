package com.example.rickandmortyapp.Entity.CharacterDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Character {
    @SerializedName("info")
    @Expose
    private InfoForCharacter info;

    @SerializedName("results")
    @Expose
    private ArrayList<ResultForCharacter> resultForCharacters;

    public InfoForCharacter getInfo() {
        return info;
    }

    public void setInfo(InfoForCharacter info) {
        this.info = info;
    }

    public ArrayList<ResultForCharacter> getResults() {
        return resultForCharacters;
    }

    public void setResults(ArrayList<ResultForCharacter> resultForCharacters) {
        this.resultForCharacters = resultForCharacters;
    }
}