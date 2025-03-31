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
import com.example.kmmkitchentmanagement.Model.Supplier
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.ItemsAdapter
import com.example.kmmkitchentmanagement.fragmentSub.ItemsView
import com.example.kmmkitchentmanagement.viewmodelExtends.ItemsVM
import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.example.kmmkitchentmanagement.viewmodelExtends.SupplierVM
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
class frag_SupplierItems : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var listItems: ListView
    private lateinit var ItemsAdapter: ItemsAdapter
    private var listRecent: MutableList<Items> = mutableListOf()
    private lateinit var userViewModel: UserViewModel
    private val SupplierVM: SupplierVM by activityViewModels()
    private val ItemsVM: ItemsVM by activityViewModels()
    private lateinit var backBtn: ImageButton
    private lateinit var btnAddItem: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supplier_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseInit()
        addView(view)
        // ✅ Đảm bảo ItemsAdapter được khởi tạo trước khi sử dụng
        ItemsAdapter = ItemsAdapter(requireContext(), listRecent)
        listItems.adapter = ItemsAdapter
        SupplierVM.getData().observe(viewLifecycleOwner) { selectedSupplier ->
            if (selectedSupplier != null) {
                Log.d("frag_Items", "📡 Nhận được Supplier từ ViewModel: ${selectedSupplier.id}")
                if (selectedSupplier.id.isNotEmpty()) {
                    dataHandler(selectedSupplier)
                } else {
                    Log.e("frag_Items", "⚠ Supplier ID rỗng, không thể lấy dữ liệu!")
                }
            } else {
                Log.e("frag_Items", "⚠ Không có Supplier nào được chọn!")
            }
        }
        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user?.userId != null) { // Kiểm tra userId
                Log.d("frag_Items", "📡 Nhận được userId: ${user.userId}")

                // Kiểm tra vai trò người dùng
                if (user.getRole() == "Quản trị viên") {
                    btnAddItem.visibility = View.VISIBLE // Hiển thị nút thêm nhóm
                } else {
                    btnAddItem.visibility = View.GONE // Ẩn nút thêm nhóm
                }
            } else {
                Log.e("frag_Items", "⚠ Không thể xác định userId!")
                Toast.makeText(requireActivity(), "Không thể xác định userId", Toast.LENGTH_SHORT).show()
            }
        }
        eventHandler()
    }



    private fun firebaseInit() {
        firestore = FirebaseFirestore.getInstance()
    }

    private fun dataHandler(selectedSupplier: Supplier) {
        firestore.collection("Items")
            .whereEqualTo("supplierId", selectedSupplier.id)
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





    private fun addView(view: View) {
        listItems = view.findViewById(R.id.listItems)
        backBtn = view.findViewById(R.id.btnbackview)
        btnAddItem = view.findViewById(R.id.btnAddItem)
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
