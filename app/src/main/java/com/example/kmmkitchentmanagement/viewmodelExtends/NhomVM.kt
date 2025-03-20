package com.example.kmmkitchentmanagement.viewmodelExtends

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kmmkitchentmanagement.Model.Nhom

class NhomVM : ViewModel() {
    companion object {
        val data: MutableLiveData<Nhom> = MutableLiveData()
    }

    fun getData(): MutableLiveData<Nhom> {
        return data
    }

    fun setData(Nhom: Nhom) {
        data.value = Nhom
    }
}
