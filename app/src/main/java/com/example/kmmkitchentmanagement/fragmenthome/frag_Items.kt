package com.example.kmmkitchentmanagement.fragmenthome

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.AppUtils.Utils
import com.example.kmmkitchentmanagement.Model.Items
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.ItemsAdapter
import com.example.kmmkitchentmanagement.fragmentSub.ItemsView
import com.example.kmmkitchentmanagement.viewmodelExtends.ItemsVM
import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
class frag_Items : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var listItems: ListView
    private lateinit var ItemsAdapter: ItemsAdapter
    private var listRecent: MutableList<Items> = mutableListOf()
    private lateinit var userViewModel: UserViewModel
    private val NhomVM: NhomVM by activityViewModels()
    private val ItemsVM: ItemsVM by activityViewModels()
    private lateinit var backBtn: ImageButton
    private lateinit var btnAddItem: ImageView
    private lateinit var btnNhomDetails: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseInit()
        addView(view)
        // ✅ Đảm bảo ItemsAdapter được khởi tạo trước khi sử dụng
        ItemsAdapter = ItemsAdapter(requireContext(), listRecent)
        listItems.adapter = ItemsAdapter
        Log.d("frag_Items", "🧐 Trước khi observe - Nhóm hiện tại trong ViewModel: ${NhomVM.getData().value}")

        NhomVM.getData().observe(viewLifecycleOwner) { selectedNhom ->
            if (selectedNhom != null) {
                Log.d("frag_Items", "📡 Nhận được nhóm từ ViewModel: ${selectedNhom.id}")
                dataHandler(selectedNhom)
            } else {
                Log.e("frag_Items", "⚠ Không có nhóm nào được chọn!")
            }
        }

        attackData()
        eventHandler()
    }



    private fun firebaseInit() {
        firestore = FirebaseFirestore.getInstance()
    }

    private fun dataHandler(selectedNhom: Nhom) {
        ItemsAdapter = ItemsAdapter(requireContext(), listRecent)

        firestore.collection("Nhom").document(selectedNhom.id).collection("Items")
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
        Log.d("frag_Items", "Adapter set with ${listRecent.size} items")
    }

    private fun addView(view: View) {
        listItems = view.findViewById(R.id.listItems)
        backBtn = view.findViewById(R.id.btnbackview)
        btnAddItem = view.findViewById(R.id.btnAddItem)as ImageView
    }

    private fun eventHandler() {
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnAddItem.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.CacItemsView, frag_ThemItems()) // Thay đổi sang frag_ThemItems
                .addToBackStack(null) // Để có thể quay lại bằng nút back
                .commit()
        }
        listItems.setOnItemClickListener { _, _, i, _ ->
            ItemsVM.setData(listRecent[i])
            childFragmentManager.beginTransaction()
                .replace(R.id.CacItemsView, ItemsView())
                .addToBackStack(null)
                .commit()
        }
    }
}
