package com.example.kmmkitchentmanagement.fragmenthome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.AppUtils.Utils
import com.example.kmmkitchentmanagement.Model.Supplier
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.SupplierAdapter
import com.example.kmmkitchentmanagement.viewmodelExtends.SupplierVM
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class frag_Supplier : Fragment(){
    private lateinit var firestore: FirebaseFirestore
    private lateinit var listSupplier: ListView
    private lateinit var SupplierAdapter: SupplierAdapter
    private var listRecent: MutableList<Supplier> = mutableListOf()
    private lateinit var userViewModel: UserViewModel
    private val SupplierVM: SupplierVM by activityViewModels()
//    private lateinit var btnAddNhom: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }
    // layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_supplier, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseInit()
        addView(view)
        dataHandler()
        attackData()
        eventHandler()
    }

    private fun firebaseInit() {
        firestore = FirebaseFirestore.getInstance()
    }

    private fun dataHandler() {
        SupplierAdapter = SupplierAdapter(requireContext(), listRecent)
        firestore.collection("Supplier")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireActivity(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
                } else {
                    listRecent.clear()
                    value?.toObjects(Supplier::class.java)?.let { listRecent.addAll(it) }
                    SupplierAdapter.notifyDataSetChanged()
                    Utils.setListViewHeightBasedOnChildren(listSupplier)
                }
            }
    }

    private fun attackData() {
        listSupplier.adapter = SupplierAdapter
    }

    private fun addView(view: View) {
        listSupplier = view.findViewById(R.id.listSupplier)

//        btnAddSupplier = view.findViewById(R.id.btnAddSupplier)as ImageView
    }

    private fun eventHandler() {
        listSupplier.setOnItemClickListener { _, _, i, _ ->
            val selectedSupplier = listRecent[i]
            SupplierVM.setData(selectedSupplier) // Cập nhật ViewModel

            parentFragmentManager.beginTransaction().run {
                replace(R.id.CacSuppliersView, frag_SupplierItems())
                addToBackStack(null)
                commit()
            }
        }
    }
}