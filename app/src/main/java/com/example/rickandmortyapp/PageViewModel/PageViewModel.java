package com.example.rickandmortyapp.PageViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class PageViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<String>> character_list = new MutableLiveData<>();

    public void setCharacterList(ArrayList<String> list) {
        character_list.setValue(list);
    }
    public LiveData<ArrayList<String>> getCharacterList() {
        return character_list;
    }
}