package com.example.kmmkitchentmanagement.fragmenthome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.AppUtils.Utils
import com.example.kmmkitchentmanagement.Model.Items
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.ItemsAdapter
import com.example.kmmkitchentmanagement.fragmentSub.ItemsView
import com.example.kmmkitchentmanagement.viewmodelExtends.ItemsVM
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class frag_Items : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var listItems: ListView
    private lateinit var ItemsAdapter: ItemsAdapter
    private var listRecent: MutableList<Items> = mutableListOf()
    private lateinit var userViewModel: UserViewModel
    private val ItemsVM: ItemsVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }
    // layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_items, container, false)
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
        ItemsAdapter = ItemsAdapter(requireContext(), listRecent)
        firestore.collection("/Items")
            .orderBy("addTime", Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireActivity(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
                } else {
                    listRecent.clear()
                    value?.toObjects(Items::class.java)?.let { listRecent.addAll(it) }
                    ItemsAdapter.notifyDataSetChanged()
                    Utils.setListViewHeightBasedOnChildren(listItems)
                }
            }
    }

    private fun attackData() {
        listItems.adapter = ItemsAdapter
    }

    private fun addView(view: View) {
        listItems = view.findViewById(R.id.listItems)
    }

    private fun eventHandler() {
        listItems.setOnItemClickListener { _, _, i, _ ->
            ItemsVM.setData(listRecent[i])
            childFragmentManager.beginTransaction()
                .replace(R.id.CacItemsView, ItemsView())
                .addToBackStack(null)
                .commit()
        }
    }
}
