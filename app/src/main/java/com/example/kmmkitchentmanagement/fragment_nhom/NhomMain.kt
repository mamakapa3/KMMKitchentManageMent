package com.example.kmmkitchentmanagement.fragment_nhom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.NhomAdapter
import com.example.kmmkitchentmanagement.fragmentSub.NhomView
import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.google.firebase.firestore.*

class NhomMain : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var NhomList: ArrayList<Nhom>
    private lateinit var backBtn: ImageButton
    private lateinit var NhomAdapter: NhomAdapter
    private lateinit var searchLV: SearchView
    private lateinit var listLV: ListView
    private lateinit var NhomVM: NhomVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nhom_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseInit()
        addView(view)
        dataHandler()
        attackData()
        eventHandler()
    }

    private fun addView(view: View) {
        backBtn = view.findViewById(R.id.btnBackListLV)
        listLV = view.findViewById(R.id.listLV)
        searchLV = view.findViewById(R.id.searchTG)
    }

    private fun firebaseInit() {
        firestore = FirebaseFirestore.getInstance()
    }

    private fun dataHandler() {
        NhomVM = ViewModelProvider(this)[NhomVM::class.java]
        NhomList = arrayListOf()
        NhomAdapter = NhomAdapter(requireContext(), NhomList)

        firestore.collection("/luanvan").addSnapshotListener { value, error ->
            if (error != null) {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Lỗi")
                    .setMessage("Tải xuống dữ liệu gặp lỗi")
                    .setNegativeButton("Thoát") { _, _ ->
                        parentFragmentManager.popBackStack()
                    }
                    .show()
            } else {
                NhomList.clear()
                value?.toObjects(Nhom::class.java)?.let {
                    NhomList.addAll(it)
                    NhomAdapter.reflectList.clear()
                    NhomAdapter.reflectList.addAll(it)
                    NhomAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun attackData() {
        listLV.adapter = NhomAdapter
    }

    private fun eventHandler() {
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        searchLV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                NhomAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                NhomAdapter.filter.filter(newText)
                return false
            }
        })

        listLV.setOnItemClickListener { _, _, i, _ ->
            Log.i("NhomMain", "Position: $i")
            NhomVM.setData(NhomList[i])
            childFragmentManager.beginTransaction()
                .replace(R.id.NhomMain, NhomView())
                .addToBackStack("toDetail")
                .commit()
        }
    }
}
