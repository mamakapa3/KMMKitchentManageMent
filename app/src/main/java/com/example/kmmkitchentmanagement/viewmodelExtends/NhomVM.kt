package com.example.kmmkitchentmanagement.viewmodelExtends

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kmmkitchentmanagement.Model.Nhom

class NhomVM : ViewModel() {
    private val data: MutableLiveData<Nhom> = MutableLiveData()

    fun getData(): MutableLiveData<Nhom> {
        return data
    }

    fun setData(nhom: Nhom) {
        data.value = nhom
    }
}

