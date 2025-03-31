package com.example.kmmkitchentmanagement.fragmentSub


import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.util.Log

import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

class ItemsView : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var backBtn: ImageButton
    private lateinit var editItemBtn: Button
    private lateinit var CancelEditItemBtn: Button
    private lateinit var btnNhomDelete: ImageButton

    private lateinit var title: TextView
    private lateinit var addTime: TextView
    private lateinit var itemType: TextView
    private lateinit var number: TextView
    private lateinit var description: TextView
    private lateinit var supplier: TextView

    private lateinit var editTitle: EditText
    private lateinit var editNumber: EditText
    private lateinit var editItemType: EditText
//    private lateinit var editAddTime: EditText
    private lateinit var editDescription: EditText
    private lateinit var editSupplier: EditText

    private var isEditing = false

    private lateinit var imageView: ImageView
    private val ItemsVM: ItemsVM by activityViewModels()
    private var Items: Items = Items()
    private val NhomVM: NhomVM by activityViewModels()
    val role = 2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cacitems, container, false)
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
        btnNhomDelete = view.findViewById(R.id.btnNhomDelete)
        title = view.findViewById(R.id.textTenItemsview)
        number = view.findViewById(R.id.textNumberview)
        itemType = view.findViewById(R.id.textitemTypeview)
        addTime = view.findViewById(R.id.textaddTimeview)
        description = view.findViewById(R.id.textdescriptionview)
        supplier = view.findViewById(R.id.textsupplierview)
        imageView = view.findViewById(R.id.imageview)
        editItemBtn = view.findViewById(R.id.editItemBtn)
        CancelEditItemBtn = view.findViewById(R.id.CancelEditItemBtn)

        // Thêm EditText
        editTitle = view.findViewById(R.id.editTenItemsview)
        editNumber = view.findViewById(R.id.editNumberview)
        editItemType = view.findViewById(R.id.edititemTypeview)
//        editAddTime = view.findViewById(R.id.editaddTimeview)
        editDescription = view.findViewById(R.id.editdescriptionview)
        editSupplier = view.findViewById(R.id.editsupplierview)
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
    private fun deleteItem() {
        if (role != 1) {  // 🔥 Nếu không phải Admin, chặn xóa
            Toast.makeText(requireContext(), "Bạn không có quyền xóa", Toast.LENGTH_SHORT).show()
            return
        }

        val itemRef = firestore.collection("Items").document(Items.id)
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa mục này không?")
            .setPositiveButton("Xóa") { _, _ ->
                itemRef.delete()
                    .addOnSuccessListener {
                        Log.d("Debug", "✅ Xóa thành công")
                        Toast.makeText(requireContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack() // Quay lại màn hình trước
                    }
                    .addOnFailureListener { e ->
                        Log.e("Debug", "❌ Lỗi khi xóa", e)
                        Toast.makeText(requireContext(), "Lỗi khi xóa", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }


    private fun toggleEditMode(editing: Boolean) {
        isEditing = editing
         // 🔹 Lấy role từ ViewModel hoặc SharedPreferences

        if (editing) {
            editNumber.setText(number.text) // Người dùng luôn có thể chỉnh sửa số lượng

            if (role == 1) { // Admin chỉnh sửa tất cả
                editTitle.setText(title.text)
                editItemType.setText(itemType.text)
                editDescription.setText(description.text)
                editSupplier.setText(supplier.text)

                editTitle.visibility = View.VISIBLE
                editItemType.visibility = View.VISIBLE
                editDescription.visibility = View.VISIBLE
                editSupplier.visibility = View.VISIBLE

                // Ẩn TextView tương ứng
                title.visibility = View.GONE
                itemType.visibility = View.GONE
                description.visibility = View.GONE
                supplier.visibility = View.GONE
            }

            editNumber.visibility = View.VISIBLE
            number.visibility = View.GONE

            editItemBtn.text = "Lưu"
            CancelEditItemBtn.text = "Hủy"
        } else {
            // 🔹 Khi HỦY, đảm bảo tất cả các TextView đều hiện lại
            title.visibility = View.VISIBLE
            number.visibility = View.VISIBLE
            itemType.visibility = View.VISIBLE
            description.visibility = View.VISIBLE
            supplier.visibility = View.VISIBLE

            // Ẩn EditText
            editTitle.visibility = View.GONE
            editNumber.visibility = View.GONE
            editItemType.visibility = View.GONE
            editDescription.visibility = View.GONE
            editSupplier.visibility = View.GONE

            editItemBtn.text = "Chỉnh sửa"
            CancelEditItemBtn.text = "Hủy chỉnh"
        }
    }


    private fun saveChanges() {
        val role = 2 // 🔹 Thay bằng cách lấy role từ ViewModel hoặc SharedPreferences
        val updatedData = mutableMapOf<String, Any>()

        if (role == 1) { // Admin cập nhật tất cả
            updatedData["title"] = editTitle.text.toString().trim()
            updatedData["itemType"] = editItemType.text.toString().trim()
            updatedData["description"] = editDescription.text.toString().trim()
            updatedData["supplier"] = editSupplier.text.toString().trim()
        }

        updatedData["number"] = editNumber.text.toString().toIntOrNull() ?: Items.number

        val firestoreRef = firestore.collection("Items").document(Items.id)

        firestoreRef.update(updatedData)
            .addOnSuccessListener {
                Log.d("Debug", "✅ Cập nhật thành công")
                Items = Items.copy(
                    title = updatedData["title"] as? String ?: Items.title,
                    number = updatedData["number"] as Int,
                    itemType = updatedData["itemType"] as? String ?: Items.itemType,
                    description = updatedData["description"] as? String ?: Items.description,
                    supplier = updatedData["supplier"] as? String ?: Items.supplier
                )
                attachData() // Cập nhật giao diện
                toggleEditMode(false) // Thoát chế độ chỉnh sửa
            }
            .addOnFailureListener { e ->
                Log.e("Debug", "❌ Lỗi khi cập nhật dữ liệu: ${e.message}", e)
                Toast.makeText(requireContext(), "Lỗi cập nhật: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun eventHandler() {
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnNhomDelete.setOnClickListener {
            deleteItem()
        }

        // Khi bấm "Chỉnh sửa"
        editItemBtn.setOnClickListener {
            if (isEditing) {
                saveChanges() // Gọi hàm lưu vào Firestore
            } else {
                toggleEditMode(true) // Chuyển sang chế độ chỉnh sửa
            }
        }

        // Khi bấm "Hủy"
        CancelEditItemBtn.setOnClickListener {
            toggleEditMode(false) // Quay lại trạng thái ban đầu
        }
    }

}
