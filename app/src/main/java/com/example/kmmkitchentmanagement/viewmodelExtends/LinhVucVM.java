package com.example.kmmkitchentmanagement.viewmodelExtends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LinhVucVM extends ViewModel {
    MutableLiveData<String> data=new MutableLiveData<String>();

    public MutableLiveData<String> getData() {
        return data;
    }

    public void setData(String data) {
//        this.data.setValue(data);
    }
}
