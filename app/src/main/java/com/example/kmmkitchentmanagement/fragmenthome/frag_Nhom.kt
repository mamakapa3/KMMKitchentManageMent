package com.example.kmmkitchentmanagement.fragmenthome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    private lateinit var listNhom: ListView
    private lateinit var NhomAdapter: NhomAdapter
    private var listRecent: MutableList<Nhom> = mutableListOf()
    private lateinit var userViewModel: UserViewModel
    private val NhomVM: NhomVM by activityViewModels()
    private lateinit var btnAddNhom: ImageView
    private lateinit var btnJoinNhom: ImageView

    override fun onResume() {
        super.onResume()
        NhomAdapter.notifyDataSetChanged() // Cập nhật dữ liệu trong adapter
    }

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

        // ✅ Khởi tạo adapter trước khi dùng
        NhomAdapter = NhomAdapter(requireContext(), listRecent)
        listNhom.adapter = NhomAdapter


        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user?.userId != null) { // Lấy userId thay vì email
                Log.d("frag_Nhom", "📡 Nhận được userId: ${user.userId}")
                dataHandler(user.userId) // Gọi dataHandler với userId

                // Kiểm tra quyền hạn của người dùng
                if (user.getRole() == "Quản trị viên") {
                    btnAddNhom.visibility = View.VISIBLE // Hiển thị nút thêm nhóm
                } else {
                    btnAddNhom.visibility = View.GONE // Ẩn nút thêm nhóm
                }
            } else {
                Log.e("frag_Nhom", "⚠ Không thể xác định userId!")
                Toast.makeText(requireActivity(), "Không thể xác định userId", Toast.LENGTH_SHORT).show()
            }
        }
        eventHandler()
    }


    private fun firebaseInit() {
        firestore = FirebaseFirestore.getInstance()
    }

    private fun dataHandler(currentUserId: String) {
        firestore.collection("Nhom")
            .whereArrayContains("members", currentUserId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("frag_Nhom", "❌ Lỗi khi tải dữ liệu: ${error.message}")
                    Toast.makeText(requireActivity(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
                } else {
                    listRecent.clear()
                    value?.toObjects(Nhom::class.java)?.let {
                        listRecent.addAll(it)
                    }
                    Log.d("frag_Nhom", "📜 User is in ${listRecent.size} groups!") // Kiểm tra số lượng nhóm

                    if (listRecent.isEmpty()) {
                        Log.w("frag_Nhom", "⚠ Danh sách nhóm rỗng!")
                    }

                    NhomAdapter.notifyDataSetChanged()
                    Utils.setListViewHeightBasedOnChildren(listNhom)
                }
            }
    }



    private fun addView(view: View) {
        listNhom = view.findViewById(R.id.listNhom)
        btnAddNhom = view.findViewById(R.id.btnAddNhom)as ImageView
        btnJoinNhom = view.findViewById(R.id.btnJoinNhom)as ImageView
    }
// nhom item

    private fun eventHandler() {
        listNhom.setOnItemClickListener { _, _, i, _ ->
            val selectedNhom = listRecent[i]
            Log.d("frag_Nhom", "📌 Cập nhật nhóm vào ViewModel: ${selectedNhom.id}")
            NhomVM.setData(selectedNhom) // Cập nhật ViewModel

            parentFragmentManager.beginTransaction().run {
                replace(R.id.CacNhomView, frag_Items())
                addToBackStack(null)
                commit()
            }
        }
        btnAddNhom.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.CacNhomView, frag_ThemNhom()) // Thay đổi sang frag_ThemItems
                .addToBackStack(null) // Để có thể quay lại bằng nút back
                .commit()
        }
        btnJoinNhom.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.CacNhomView, frag_JoinNhom()) // Thay đổi sang frag_ThemItems
                .addToBackStack(null) // Để có thể quay lại bằng nút back
                .commit()
        }
    }
}
