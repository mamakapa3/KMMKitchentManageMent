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

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.kmmkitchentmanagement.Model.Items
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.viewmodelExtends.ItemsVM
import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

import java.text.SimpleDateFormat
import java.util.Locale

class NhomView : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var backBtn: ImageButton
    private lateinit var increaseBtn: Button
    private lateinit var decreaseBtn: Button
    private lateinit var title: TextView
    private lateinit var addTime: TextView
    private lateinit var itemType: TextView
    private lateinit var number: TextView
    private lateinit var description: TextView
    private lateinit var supplier: TextView
    private lateinit var imageView: ImageView
    private val ItemsVM: ItemsVM by activityViewModels()
    private var Items: Items = Items()
    private val NhomVM: NhomVM by activityViewModels()

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

        ItemsVM.getData().observe(viewLifecycleOwner) { items: Items? ->
            Log.d("Debug", "ViewModel cập nhật dữ liệu: $items")
            if (items != null) {
                Items = items
                attachData()  // 🛠 Gọi lại UI khi có dữ liệu mới
            } else {
                Log.e("Debug", "Items vẫn NULL")
            }
        }
    }

    private fun firebaseInit() {
        if (!::firestore.isInitialized) {
            firestore = FirebaseFirestore.getInstance() // 🔹 Khởi tạo Firestore
        }
    }
    //    Hiển thị thông tin (Items) trên giao diện.
    private fun attachData() {
        title.text = Items.title
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        addTime.text = dateFormat.format(Items.addTime)
        itemType.text = Items.itemType
        number.text = Items.number.toString()
        description.text = Items.description
        supplier.text = Items.supplier
// 🔹 Load Image from Local Storage
        val imagePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "YourAppImages/items_${Items.id}.jpg"
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
        title = view.findViewById(R.id.textTenItemsview)
        number = view.findViewById(R.id.textNumberview)
        itemType = view.findViewById(R.id.textitemTypeview)
        addTime = view.findViewById(R.id.textaddTimeview)
        description = view.findViewById(R.id.textdescriptionview)
        supplier = view.findViewById(R.id.textsupplierview)
        imageView = view.findViewById(R.id.imageview)
        increaseBtn = view.findViewById(R.id.increaseBtn)
        decreaseBtn = view.findViewById(R.id.decreaseBtn)
    }

    private fun updateNumber(newNumber: Int) {
        val selectedNhom = NhomVM.getData().value // Lấy nhóm hiện tại từ ViewModel

        if (selectedNhom == null) {
            Log.e("Debug", "⚠ Không có nhóm nào được chọn, không thể cập nhật số lượng!")
            return
        }

        val firestoreRef = firestore.collection("Nhom")
            .document(selectedNhom.id)
            .collection("Items")
            .document(Items.id)  // 🔹 Đúng đường dẫn Firestore

        firestoreRef.update("number", newNumber)
            .addOnSuccessListener {
                Log.d("Debug", "✅ Cập nhật số lượng thành công: $newNumber")
                Items.number = newNumber // Cập nhật vào biến local
                number.text = newNumber.toString() // Cập nhật UI
            }
            .addOnFailureListener { e ->
                Log.e("Debug", "❌ Lỗi khi cập nhật số lượng", e)
                Toast.makeText(requireContext(), "Lỗi khi cập nhật số lượng", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eventHandler() {
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        // Xử lý nút tăng số lượng
        increaseBtn.setOnClickListener {
            updateNumber(Items.number + 1)
        }

        // Xử lý nút giảm số lượng (chỉ giảm khi number > 0)
        decreaseBtn.setOnClickListener {
            if (Items.number > 0) {
                updateNumber(Items.number - 1)
            } else {
                Toast.makeText(requireContext(), "Không thể giảm số lượng dưới 0", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
