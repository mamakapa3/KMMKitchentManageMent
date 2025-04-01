package com.example.kmmkitchentmanagement.fragmenthome

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.ThongBaoAdapter
import com.google.firebase.firestore.*

class frag_ThongBao : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var thongBaoAdapter: ThongBaoAdapter
    private val thongBaoList = mutableListOf<String>()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var btnClearAll: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_thong_bao, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerViewThongBao)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        thongBaoAdapter = ThongBaoAdapter(thongBaoList)
        recyclerView.adapter = thongBaoAdapter

        firestore = FirebaseFirestore.getInstance()
        listenForChanges()

        btnClearAll = view.findViewById(R.id.btnClearAll)

        btnClearAll.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Xóa tất cả thông báo")
                .setMessage("Bạn có chắc muốn xóa toàn bộ thông báo không?")
                .setPositiveButton("Xóa") { _, _ ->
                    firestore.collection("ThongBao")
                        .get()
                        .addOnSuccessListener { documents ->
                            val batch = firestore.batch()
                            for (document in documents) {
                                batch.delete(document.reference)
                            }
                            batch.commit().addOnSuccessListener {
                                thongBaoList.clear()
                                thongBaoAdapter.notifyDataSetChanged()
                                Toast.makeText(requireContext(), "Đã xóa tất cả thông báo!", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    private fun listenForChanges() {
        firestore.collection("Items")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("Firestore", "Lỗi khi lắng nghe thay đổi", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val message = "📌 Thêm items mới: ${dc.document.getString("title")}"
                            thongBaoList.add(0, message)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val message = "✏️ Items cập nhật: ${dc.document.getString("title")}"
                            thongBaoList.add(0, message)
                        }
                        DocumentChange.Type.REMOVED -> {
                            val message = "🗑 Xóa items: ${dc.document.getString("title")}"
                            thongBaoList.add(0, message)
                        }
                    }
                }
                thongBaoAdapter.notifyDataSetChanged()
            }
        firestore.collection("Nhom")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("Firestore", "Lỗi khi lắng nghe thay đổi trong Nhom", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val message = "📂 Nhóm mới: ${dc.document.getString("title")}"
                            thongBaoList.add(0, message)
                        }
                        DocumentChange.Type.MODIFIED -> { // 🔥 Thêm trường hợp này
                            val message = "🔄 Nhóm cập nhật: ${dc.document.getString("title")}"
                            thongBaoList.add(0, message)
                        }
                        DocumentChange.Type.REMOVED -> {
                            val message = "🚮 Nhóm bị xóa: ${dc.document.getString("title")}"
                            thongBaoList.add(0, message)
                        }
                    }
                }
                thongBaoAdapter.notifyDataSetChanged()
            }

    }
}
