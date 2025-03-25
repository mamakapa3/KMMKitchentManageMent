package com.example.kmmkitchentmanagement.fragmenthome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.AppUtils.Utils
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.NhomAdapter
import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class frag_Nhom : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var listGanDay: ListView
    private lateinit var NhomAdapter: NhomAdapter
    private var listRecent: MutableList<Nhom> = mutableListOf()
    private lateinit var userViewModel: UserViewModel
    private val NhomVM: NhomVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }
// layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_nhom, container, false)
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
        NhomAdapter = NhomAdapter(requireContext(), listRecent)
        firestore.collection("/Nhom")
            .orderBy("addTime", Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireActivity(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
                } else {
                    listRecent.clear()
                    value?.toObjects(Nhom::class.java)?.let { listRecent.addAll(it) }
                    NhomAdapter.notifyDataSetChanged()
                    Utils.setListViewHeightBasedOnChildren(listGanDay)
                }
            }
    }

    private fun attackData() {
        listGanDay.adapter = NhomAdapter
    }

    private fun addView(view: View) {
        listGanDay = view.findViewById(R.id.listGanDay)
    }
// nhom item

    private fun eventHandler() {
        listGanDay.setOnItemClickListener { _, _, i, _ ->
            NhomVM.setData(listRecent[i])
            childFragmentManager.beginTransaction()
                .replace(R.id.CacNhomView, frag_Items())
                .addToBackStack(null)
                .commit()
        }
    }
}
