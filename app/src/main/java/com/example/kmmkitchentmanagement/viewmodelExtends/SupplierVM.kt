package com.example.kmmkitchentmanagement.viewmodelExtends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kmmkitchentmanagement.Model.Supplier

class SupplierVM : ViewModel() {
    private val _selectedSupplier = MutableLiveData<Supplier?>()
    val selectedSupplier: LiveData<Supplier?> get() = _selectedSupplier

    fun setData(supplier: Supplier) {
        Log.d("SupplierVM", "✅ Đã lưu Supplier vào ViewModel: ${supplier.id}")
        _selectedSupplier.value = supplier
    }

    fun getData(): LiveData<Supplier?> {
        return selectedSupplier
    }
}


