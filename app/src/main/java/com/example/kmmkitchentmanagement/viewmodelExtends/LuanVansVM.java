package com.example.kmmkitchentmanagement.viewmodelExtends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kmmkitchentmanagement.Model.LuanVan;

import java.util.ArrayList;

public class LuanVansVM extends ViewModel {
    MutableLiveData<ArrayList<LuanVan>> data = new MutableLiveData<ArrayList<LuanVan>>();
    public MutableLiveData<ArrayList<LuanVan>> getData() {
        return this.data;
    }
    public void setData(ArrayList<LuanVan> data) {
//        this.data.setValue(data);
    }
}
