package com.example.kmmkitchentmanagement.fragmenthome

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.customdialog.UploadTaskDialog
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.Date

class frag_ThemNhom : Fragment(){
    private lateinit var collectionReference: CollectionReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userViewModel: UserViewModel
    private lateinit var themBtn: Button
    private lateinit var mImageButton:ImageButton
    private lateinit var mEditTen: EditText
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var Thumbnail: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_themnhom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseInit()
//        dataHandler()
        addView(view)
        AddImage(view)
        ActivityResult(view)
        addNhom(view)
    }
    private fun firebaseInit() {
        firestore = FirebaseFirestore.getInstance()
        collectionReference = firestore.collection("Nhom")
        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference("Nhom")
    }
    private fun addView(view: View) {
        mImageButton = view.findViewById(R.id.btnAvaLV)
        mEditTen = view.findViewById<EditText>(R.id.editTitleLV)
        themBtn = view.findViewById(R.id.btnAddLV)
    }
    fun AddImage(view: View) {
        mImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            activityResultLauncher.launch(intent)
        }
    }
    fun ActivityResult(view: View) {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            data?.data?.let { temp ->
                if (temp.toString().lowercase().contains("image")) {
                    Thumbnail = temp
                    mImageButton.setImageURI(Thumbnail)
                } 
            }
        }
    }

    fun addNhom(view: View) {
        themBtn.setOnClickListener {
            val generatedID = collectionReference.document().id
            val tieuDe = mEditTen.text.toString()

            if (tieuDe.isEmpty()) {
                Toast.makeText(view.context, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show()
            }
            val tempNhom = Nhom().apply {
                id = generatedID
                title = tieuDe
                addTime = Date()
            }
                Log.i("Nhóm", tempNhom.toString())
            collectionReference.document(generatedID).set(tempNhom)
                .addOnSuccessListener {
                        val uploadTasks = mutableListOf<UploadTask>()

                        // them anh
                    Thumbnail?.let { uri ->
                        uploadTasks.add(storageReference.child("/image/${tempNhom.id}.jpg").putFile(uri))
                    }
                        if (uploadTasks.isNotEmpty()) {
                            val dialog = UploadTaskDialog(uploadTasks)
                            dialog.show(parentFragmentManager, "uploading")
                        } else {
                            AlertDialog.Builder(view.context)
                                .setIcon(R.drawable.check)
                                .setMessage("Thêm nhóm mới thành công")
                                .setPositiveButton("Ok") { dialogInterface, _ -> dialogInterface.dismiss() }
                                .show()
                        }
//                        val history = historyObj(Date(), "Thêm 1 nhóm mới '$tieuDe'")
//                        firestore.collection("/lichsu").add(history)
                    }
            }
        }
}