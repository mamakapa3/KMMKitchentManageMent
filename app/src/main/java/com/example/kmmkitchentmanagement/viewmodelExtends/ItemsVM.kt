package com.example.kmmkitchentmanagement.viewmodelExtends


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kmmkitchentmanagement.Model.Items

class ItemsVM : ViewModel() {
    private val data = MutableLiveData<Items>()

    fun setData(item: Items) {
        data.value = item
    }

    fun getData(): LiveData<Items> = data
}