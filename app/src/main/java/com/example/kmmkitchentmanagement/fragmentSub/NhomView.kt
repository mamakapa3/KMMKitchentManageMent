package com.example.kmmkitchentmanagement.fragmentSub


import android.os.Bundle
import android.os.Environment
import android.util.Log

import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R

import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

import java.text.SimpleDateFormat
import java.util.Locale

class NhomView : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var backBtn: ImageButton
    private lateinit var btnJoin: Button
    private lateinit var title: TextView
    private lateinit var memNumber: TextView
    private lateinit var imageView: ImageView
    private val NhomVM: NhomVM by activityViewModels()
    private var Nhom: Nhom = Nhom()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chitietnhom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseInit()
        dataHandler()
        addView(view)
        eventHandler()
        attachData()
    }

    private fun dataHandler() {
        Log.d("Debug", "dataHandler() ĐÃ ĐƯỢC GỌI")

        NhomVM.getData().observe(viewLifecycleOwner) { nhom: Nhom? ->
            Log.d("Debug", "ViewModel cập nhật dữ liệu: $nhom")
            if (nhom != null) {
                Nhom = nhom
                attachData()  // 🛠 Gọi lại UI khi có dữ liệu mới
            } else {
                Log.e("Debug", "Nhom vẫn NULL")
            }
        }
    }

    private fun firebaseInit() {
        if (!::firestore.isInitialized) {
            firestore = FirebaseFirestore.getInstance() // 🔹 Khởi tạo Firestore
        }
    }
    //    Hiển thị thông tin (Nhom) trên giao diện.
    private fun attachData() {
        title.text = Nhom.title
        memNumber.text = Nhom.memNumb.toString()
// 🔹 Load Image from Local Storage
        val imagePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "YourAppImages/nhom_${Nhom.id}.jpg"
        )

        if (imagePath.exists()) {
            Log.d("ImageCheck", "✅ Ảnh tồn tại: ${imagePath.absolutePath}")
        } else {
            Log.e("ImageCheck", "❌ Ảnh KHÔNG tồn tại: ${imagePath.absolutePath}")
        }

        // 🖼 Load ảnh bằng Glide (nếu có thì load, không thì dùng ảnh mặc định)
        Glide.with(requireContext())
            .load(if (imagePath.exists()) imagePath else R.drawable.fail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)  // Không dùng cache để load ảnh mới nhất
            .skipMemoryCache(true)
            .into(imageView)
    }

    private fun addView(view: View) {
        backBtn = view.findViewById(R.id.btnbackview)
        btnJoin = view.findViewById(R.id.btnJoin)
        title = view.findViewById(R.id.textTenNhomview)
        memNumber = view.findViewById(R.id.textMemNumb)
        imageView = view.findViewById(R.id.imageview)
    }

    private fun eventHandler() {
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnJoin.setOnClickListener {
            val userId = ViewModelProvider(requireActivity())[UserViewModel::class.java].getUser().value?.userId

            if (userId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nhomRef = firestore.collection("Nhom").document(Nhom.id)
            nhomRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("Firestore", "🔥 Dữ liệu nhóm: ${document.data}")
                    val members = document.get("members") as? MutableList<String> ?: mutableListOf()
                    Log.d("Firestore", "📌 Danh sách thành viên: $members") // Debug danh sách thành viên
                    if (!members.contains(userId)) {
                        members.add(userId)
                        val newMemNumb = members.size // 🔹 Cập nhật số lượng thành viên

                        nhomRef.update(mapOf(
                            "members" to members,
                            "memNumb" to newMemNumb
                        ))
                            .addOnSuccessListener {
                                Log.d("Firestore", "✅ Người dùng đã tham gia nhóm!")
                                Toast.makeText(requireContext(), "Bạn đã tham gia nhóm!", Toast.LENGTH_SHORT).show()
                                memNumber.text = newMemNumb.toString() // Cập nhật UI
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "❌ Lỗi khi cập nhật nhóm: ${e.message}")
                                Toast.makeText(requireContext(), "Lỗi khi tham gia nhóm!", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(requireContext(), "Bạn đã tham gia nhóm này rồi!", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("Firestore", "❌ Không lấy được nhóm: ${e.message}")
                Toast.makeText(requireContext(), "Lỗi khi lấy thông tin nhóm!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
