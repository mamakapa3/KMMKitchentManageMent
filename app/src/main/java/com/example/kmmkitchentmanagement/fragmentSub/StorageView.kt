package com.example.kmmkitchentmanagement.fragmentSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.google.firebase.storage.StorageReference

class StorageView : Fragment() {
    private lateinit var storageReference: StorageReference
    private val NhomVM: NhomVM by viewModels()
    private var Nhom: Nhom = Nhom()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_items, container, false)
    }
}