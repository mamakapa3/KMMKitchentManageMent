package com.example.kmmkitchentmanagement.fragmenthome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.ThongBaoAdapter
import com.google.firebase.firestore.*

class ThongBaoFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var thongBaoAdapter: ThongBaoAdapter
    private val thongBaoList = mutableListOf<String>()
    private lateinit var firestore: FirebaseFirestore

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
    }

    private fun listenForChanges() {
        firestore.collection("Items")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("Firestore", "❌ Lỗi khi lắng nghe thay đổi", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val message = "📌 Thêm mới: ${dc.document.getString("title")}"
                            thongBaoList.add(0, message)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val message = "✏️ Cập nhật: ${dc.document.getString("title")}"
                            thongBaoList.add(0, message)
                        }
                        DocumentChange.Type.REMOVED -> {
                            val message = "🗑 Xóa: ${dc.document.getString("title")}"
                            thongBaoList.add(0, message)
                        }
                    }
                }

                // Cập nhật giao diện
                thongBaoAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "🔔 Dữ liệu đã cập nhật!", Toast.LENGTH_SHORT).show()
            }
    }
}
