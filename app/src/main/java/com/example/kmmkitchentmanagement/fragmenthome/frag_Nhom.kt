package com.example.kmmkitchentmanagement.fragmenthome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.AppUtils.Utils
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.Model.NguoiDung
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.NhomAdapter
import com.example.kmmkitchentmanagement.adapter.ClickableItemsAdapter
import com.example.kmmkitchentmanagement.fragmentSub.NhomView
import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class frag_Nhom : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var NhomVM: NhomVM
    private lateinit var catalogView: ListView
    private lateinit var listGanDay: ListView
    private lateinit var NhomAdapter: NhomAdapter
    private lateinit var catalogAdapter: ClickableItemsAdapter
    private var listRecent: MutableList<Nhom> = mutableListOf()
    private lateinit var userViewModel: UserViewModel

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
        attackView()
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
        catalogView = view.findViewById(R.id.listNhom)
        listGanDay = view.findViewById(R.id.listGanDay)
    }
// nhom item
    private fun attackView() {
    val catalogs = arrayListOf(
        intArrayOf(R.drawable.ic_luan_van, R.string.NhomList),
        intArrayOf(R.drawable.ic_tac_gia, R.string.TacGia),
        intArrayOf(R.drawable.ic_chu_de, R.string.LinhVuc),
        intArrayOf(R.drawable.ic_luu, R.string.Saved)
    )
        catalogAdapter = ClickableItemsAdapter(requireContext(), catalogs)
        catalogView.adapter = catalogAdapter
        Utils.setListViewHeightBasedOnChildren(catalogView)
    }

    private fun eventHandler() {
        listGanDay.setOnItemClickListener { _, _, i, _ ->
            NhomVM.setData(listRecent[i])
            childFragmentManager.beginTransaction()
                .replace(R.id.CacNhomView, NhomView())
                .addToBackStack(null)
                .commit()
        }
    }
}
