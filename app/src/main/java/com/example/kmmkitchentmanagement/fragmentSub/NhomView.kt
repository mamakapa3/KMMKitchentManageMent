package com.example.kmmkitchentmanagement.fragmentSub

import android.os.Environment.DIRECTORY_DOWNLOADS

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle

import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.Model.TacGia
import com.example.kmmkitchentmanagement.Model.historyObj
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.fragment_themND.ChinhNhom
import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NhomView : Fragment() {
    private lateinit var storageReference: StorageReference
    private lateinit var backBtn: ImageButton
    private lateinit var btnLuu: Button
    private lateinit var btnEdit: Button
    private lateinit var btnXoa: Button
    private lateinit var btnXemSau: Button
    private lateinit var title: TextView
    private lateinit var dateAdd: TextView
    private lateinit var linhVuc: TextView
    private lateinit var tacGia: TextView
    private lateinit var datePush: TextView
    private lateinit var moTa: TextView
    private lateinit var trichDan: TextView
    private lateinit var imageView: ImageView
    private val NhomVM: NhomVM by viewModels()
    private var Nhom: Nhom = Nhom()
    private var tacGiaClass: TacGia = TacGia()
    private val file = File(Environment.getExternalStoragePublicDirectory("//").absolutePath + "/QLNhom/")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cacnhom, container, false)
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
        dialogDelete()
    }

    private fun dataHandler() {
//        NhomVM = ViewModelProvider(requireParentFragment())[NhomVM::class.java]
        NhomVM.getData().observe(viewLifecycleOwner) { nhom: Nhom? ->
            if (nhom != null) {
                Nhom = nhom
            }
        }
    }

    private fun firebaseInit() {
        storageReference = FirebaseStorage.getInstance().getReference("/nhom/image")
    }
//    Hiển thị thông tin (Nhom) trên giao diện.
    private fun attachData() {
        btnXemSau.text = if (Nhom.marked) "Xóa khỏi danh sách xem sau" else "Lưu vào danh sách xem sau"
        title.text = Nhom.title
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateAdd.text = dateFormat.format(Nhom.publishedDate)
        linhVuc.text = Nhom.researchField
        tacGia.text = tacGiaClass.name
        moTa.text = Nhom.description
        trichDan.text = Nhom.citation
        Glide.with(requireContext())
            .load(storageReference.child("/${Nhom.id}.jpg"))
            .error(R.drawable.fail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
    }

    private fun addView(view: View) {
        btnXemSau = view.findViewById(R.id.xemsau)
        backBtn = view.findViewById(R.id.btnbacklvview)
        btnLuu = view.findViewById(R.id.btnsaveLVview)
        btnEdit = view.findViewById(R.id.btnSuaLVview)
        btnXoa = view.findViewById(R.id.btnDeleteLVview)
        title = view.findViewById(R.id.textTenLVview)
        dateAdd = view.findViewById(R.id.textngayupLVview)
        linhVuc = view.findViewById(R.id.textLinhVucLVview)
        tacGia = view.findViewById(R.id.texttacgiaLVview)
        datePush = view.findViewById(R.id.textngayxbLVview)
        moTa = view.findViewById(R.id.textmotaLVview)
        trichDan = view.findViewById(R.id.texttrichdanLVview)
        imageView = view.findViewById(R.id.imageLVview)
    }

    private fun eventHandler() {
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        // btn xem sau
//        btnXemSau.setOnClickListener {
//            val state = !Nhom.isMarked
//            Nhom.isMarked = state
//            btnXemSau.text = if (state) "Xóa khỏi danh sách xem sau" else "Lưu vào danh sách xem sau"
//            FirebaseFirestore.getInstance().collection("/nhom")
//                .document(Nhom.id)
//                .update("marked", state)
//        }
    }

//    private fun themDanhDau() {
//        btnLuu.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1023)
//            } else {
//                FirebaseStorage.getInstance().getReference("/nhom/document/${Nhom.id}.docx")
//                    .downloadUrl
//                    .addOnSuccessListener { uri ->
//                        downloadFile(requireContext(), Nhom.title, ".docx", Environment.DIRECTORY_DOWNLOADS, uri)
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
//                .replace(R.id.nhomview, ChinhNhom())
//                .addToBackStack("ToNhoms")
//                .commit()
//        }
//    }

    private fun dialogDelete() {
        btnXoa.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Thông báo")
                .setMessage("Bạn thật sự muốn xóa luận văn này?")
                .setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("OK") { _, _ ->
                    val history = historyObj(Date(), "Xóa 1 luận văn '${Nhom.title}'")
                    FirebaseFirestore.getInstance().collection("/lichsu").add(history)
                    FirebaseFirestore.getInstance().collection("/nhom")
                        .document(Nhom.id)
                        .delete()
                    parentFragmentManager.popBackStack()
                }
                .show()
        }
    }
}
