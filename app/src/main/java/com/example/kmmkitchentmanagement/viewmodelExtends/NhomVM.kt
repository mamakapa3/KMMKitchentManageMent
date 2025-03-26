package com.example.kmmkitchentmanagement.viewmodelExtends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kmmkitchentmanagement.Model.Nhom

class NhomVM : ViewModel() {
    private val _selectedNhom = MutableLiveData<Nhom?>()
    val selectedNhom: LiveData<Nhom?> get() = _selectedNhom

    fun setData(nhom: Nhom) {
        Log.d("NhomVM", "✅ Đã lưu nhóm vào ViewModel: ${nhom.id}")
        _selectedNhom.value = nhom
    }

    fun getData(): LiveData<Nhom?> {
        return selectedNhom
    }
}


