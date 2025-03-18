package com.example.kmmkitchentmanagement.viewmodelExtends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kmmkitchentmanagement.Model.LuanVan;

public class LuanVanVM extends ViewModel {
    public static MutableLiveData<LuanVan> data = new MutableLiveData<LuanVan>();
    public  MutableLiveData<LuanVan> getData(){
        return this.data;
    }
    public void setData(LuanVan luanVan) {
//        data.setValue(luanVan);
    }
}
