package com.example.kmmkitchentmanagement.viewmodelExtends


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kmmkitchentmanagement.Model.Items

class ItemsVM : ViewModel() {
    private val data: MutableLiveData<Items> = MutableLiveData()

    fun getData(): MutableLiveData<Items> {
        return data
    }

    fun setData(items: Items) {
        data.value = items
    }
}