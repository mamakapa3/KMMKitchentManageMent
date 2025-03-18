package com.example.kmmkitchentmanagement.viewmodelExtends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kmmkitchentmanagement.Model.TacGia;

public class  TacGiaVM extends ViewModel {
    MutableLiveData<TacGia> data=new MutableLiveData<TacGia>();
    public MutableLiveData<TacGia> getData() {
        return this.data;
    }
   public void setData(TacGia tacGia){
//            data.setValue(tacGia);
    }
}
