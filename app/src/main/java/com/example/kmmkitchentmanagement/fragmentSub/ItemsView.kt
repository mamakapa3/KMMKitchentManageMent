package com.example.kmmkitchentmanagement.fragmentSub


import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.kmmkitchentmanagement.Model.Items
import com.example.kmmkitchentmanagement.R
//import com.example.kmmkitchentmanagement.fragment_themND.ChinhItems
import com.example.kmmkitchentmanagement.viewmodelExtends.ItemsVM
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.text.SimpleDateFormat
import java.util.Locale

class ItemsView : Fragment() {
    private lateinit var storageReference: StorageReference
    private lateinit var backBtn: ImageButton
    //    private lateinit var btnLuu: Button
//    private lateinit var btnEdit: Button
//    private lateinit var btnXoa: Button
//    private lateinit var btnXemSau: Button
    private lateinit var title: TextView
    private lateinit var dateAdd: TextView
    private lateinit var itemType: TextView
    //    private lateinit var tacGia: TextView
    private lateinit var datePush: TextView
    private lateinit var moTa: TextView
    private lateinit var supplier: TextView
    private lateinit var imageView: ImageView
    private val ItemsVM: ItemsVM by viewModels()
    private var Items: Items = Items()

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
//        themDanhDau()
//        edit()
//        dialogDelete()
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
        storageReference = FirebaseStorage.getInstance().getReference("/items/image")
    }
    //    Hiển thị thông tin (Items) trên giao diện.
    private fun attachData() {
//        btnXemSau.text = if (Items.marked) "Xóa khỏi danh sách xem sau" else "Lưu vào danh sách xem sau"
        title.text = Items.title
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateAdd.text = dateFormat.format(Items.addTime)
        itemType.text = Items.itemType
//        tacGia.text = tacGiaClass.name
        moTa.text = Items.description
        supplier.text = Items.supplier
        Glide.with(requireContext())
            .load(storageReference.child("/${Items.id}.jpg"))
            .error(R.drawable.fail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
    }

    private fun addView(view: View) {
//        btnXemSau = view.findViewById(R.id.xemsau)
        backBtn = view.findViewById(R.id.btnbacklvviewi)
//        btnLuu = view.findViewById(R.id.btnsaveLVview)
//        btnEdit = view.findViewById(R.id.btnSuaLVview)
//        btnXoa = view.findViewById(R.id.btnDeleteLVview)
        title = view.findViewById(R.id.textTenLVviewi)
        dateAdd = view.findViewById(R.id.textngayupLVviewi)
        itemType = view.findViewById(R.id.textitemTypeLVviewi)
//        tacGia = view.findViewById(R.id.texttacgiaLVview)
        moTa = view.findViewById(R.id.textmotaLVviewi)
        supplier = view.findViewById(R.id.texttrichdanLVviewi)
        imageView = view.findViewById(R.id.imageLVviewi)
    }

    private fun eventHandler() {
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        // btn xem sau
//        btnXemSau.setOnClickListener {
//            val state = !Items.isMarked
//            Items.isMarked = state
//            btnXemSau.text = if (state) "Xóa khỏi danh sách xem sau" else "Lưu vào danh sách xem sau"
//            FirebaseFirestore.getInstance().collection("/items")
//                .document(Items.id)
//                .update("marked", state)
//        }
    }

//    private fun themDanhDau() {
//        btnLuu.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1023)
//            } else {
//                FirebaseStorage.getInstance().getReference("/items/document/${Items.id}.docx")
//                    .downloadUrl
//                    .addOnSuccessListener { uri ->
//                        downloadFile(requireContext(), Items.title, ".docx", Environment.DIRECTORY_DOWNLOADS, uri)
//                    }
//                    .addOnFailureListener {
//                        AlertDialog.Builder(requireActivity())
//                            .setTitle("Lỗi tải xuống")
//                            .setMessage("Lưu trữ số của luận văn này không tồn tại trên hệ thống")
//                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//                            .setIcon(R.drawable.error)
//                            .show()
//                    }
//            }
//        }
//    }

//    private fun downloadFile(context: Context, fileName: String, fileExtension: String, destinationDirectory: String, uri: Uri) {
//        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        val request = DownloadManager.Request(uri)
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            .setDestinationInExternalPublicDir(destinationDirectory, "$fileName$fileExtension")
//        downloadManager.enqueue(request)
//    }

//    chinh sua
//    private fun edit() {
//        btnEdit.setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.itemsview, ChinhItems())
//                .addToBackStack("ToItemss")
//                .commit()
//        }
//    }

//    private fun dialogDelete() {
//        btnXoa.setOnClickListener {
//            AlertDialog.Builder(requireContext())
//                .setTitle("Thông báo")
//                .setMessage("Bạn thật sự muốn xóa luận văn này?")
//                .setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
//                .setPositiveButton("OK") { _, _ ->
//                    val history = historyObj(Date(), "Xóa 1 luận văn '${Items.title}'")
//                    FirebaseFirestore.getInstance().collection("/lichsu").add(history)
//                    FirebaseFirestore.getInstance().collection("/items")
//                        .document(Items.id)
//                        .delete()
//                    parentFragmentManager.popBackStack()
//                }
//                .show()
//        }
//    }
}
