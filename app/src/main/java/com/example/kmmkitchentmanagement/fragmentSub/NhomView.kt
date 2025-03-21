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
    private lateinit var title: TextView
    private lateinit var dateAdd: TextView
    private lateinit var imageView: ImageView
    private val NhomVM: NhomVM by viewModels()
    private var Nhom: Nhom = Nhom()

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
        eventHandler()
        attachData()
        addView(view)
//        dialogDelete()
    }

    private fun dataHandler() {
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
        title.text = Nhom.title
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateAdd.text = dateFormat.format(Nhom.addTime)
        Glide.with(requireContext())
            .load(storageReference.child("/${Nhom.id}.jpg"))
            .error(R.drawable.fail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
    }

    private fun eventHandler() {
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    private fun addView(view: View) {
//        btnXemSau = view.findViewById(R.id.xemsau)
        backBtn = view.findViewById(R.id.btnbacklvview)
//        btnLuu = view.findViewById(R.id.btnsaveLVview)
//        btnEdit = view.findViewById(R.id.btnSuaLVview)
//        btnXoa = view.findViewById(R.id.btnDeleteLVview)
        title = view.findViewById(R.id.textTenLVview)
        dateAdd = view.findViewById(R.id.textngayupLVview)
        imageView = view.findViewById(R.id.imageLVview)
    }


//    private fun dialogDelete() {
//        btnXoa.setOnClickListener {
//            AlertDialog.Builder(requireContext())
//                .setTitle("Thông báo")
//                .setMessage("Bạn thật sự muốn xóa Items này?")
//                .setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
//                .setPositiveButton("OK") { _, _ ->
//                    val history = historyObj(Date(), "Xóa 1 Items '${Nhom.title}'")
//                    FirebaseFirestore.getInstance().collection("/lichsu").add(history)
//                    FirebaseFirestore.getInstance().collection("/nhom")
//                        .document(Nhom.id)
//                        .delete()
//                    parentFragmentManager.popBackStack()
//                }
//                .show()
//        }
//    }
}
