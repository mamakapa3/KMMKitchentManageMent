package com.example.kmmkitchentmanagement.fragmenthome

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.AppUtils.Utils
import com.example.kmmkitchentmanagement.AppUtils.serviceModule.ImageSave
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.Model.Supplier
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.customdialog.UploadTaskDialog
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.UploadTask
import java.util.Date

class frag_ThemNhom : Fragment(){
    private lateinit var collectionReference: CollectionReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var themBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var mImageButton:ImageButton
    private lateinit var mEditTen: EditText
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var Thumbnail: Uri? = null
    private var generatedID: String = ""
    private lateinit var spinnerSupplier: Spinner
    private var supplierList: MutableList<Supplier> = mutableListOf()
    private var selectedSupplierId: String? = null

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

        generatedID = collectionReference.document().id
    }
    private fun firebaseInit() {
        firestore = FirebaseFirestore.getInstance()
        collectionReference = firestore.collection("Nhom")
    }
    private fun addView(view: View) {
        spinnerSupplier = view.findViewById(R.id.spinnerSupplier)
        fetchSuppliers()
        mImageButton = view.findViewById(R.id.btnAvaNhom)
        mEditTen = view.findViewById(R.id.editTenNhom)
        themBtn = view.findViewById(R.id.btnAddNhom)
        backBtn = view.findViewById(R.id.btnbackview)
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    private fun fetchSuppliers() {
        firestore.collection("Supplier").get()
            .addOnSuccessListener { documents ->
                supplierList.clear()
                for (document in documents) {
                    val supplier = document.toObject(Supplier::class.java)
                    supplierList.add(supplier)
                }
                updateSupplierSpinner()
            }
    }
    private fun updateSupplierSpinner() {
        val supplierNames = supplierList.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, supplierNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSupplier.adapter = adapter

        spinnerSupplier.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSupplierId = supplierList[position].id
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedSupplierId = null
            }
        }
    }
    fun AddImage(view: View) {
        mImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            activityResultLauncher.launch(intent)
        }
    }


    private fun checkPermissions(context: Context) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        Log.d("PermissionCheck", "Quyền đọc ảnh: $isGranted")

        if (!isGranted) {
            requestPermissions(arrayOf(permission), 1001)
        }
    }


    fun ActivityResult(view: View) {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            data?.data?.let { uri ->
                Log.d("ActivityResult", "URI nhận được: $uri")
                Thumbnail = uri
                mImageButton.setImageURI(Thumbnail)

                // Lưu ảnh vào bộ nhớ
                val imageSave = ImageSave()
                val savedPath = imageSave.saveImageToLocalStorage(requireContext(), uri, "nhom_${generatedID}")

                if (savedPath != null) {
                    Log.d("ImageSave", "Image saved at: $savedPath")
                } else {
                    Log.e("ImageSave", "Lưu ảnh thất bại!")
                }
            }
        }
    }
    private fun linkSupplierItemsToNhom(supplierId: String, nhomId: String) {
        firestore.collection("Items")
            .whereEqualTo("supplierId", supplierId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.update("nhomId", nhomId)
                }
                Log.d("UpdateItems", "Đã liên kết Items từ Supplier vào nhóm!")
            }
    }
    private fun resetForm() {
        generatedID = collectionReference.document().id // Tạo ID mới
        Thumbnail = null
        mImageButton.setImageResource(android.R.drawable.ic_menu_add) // Ảnh mặc định
        mEditTen.setText("")
        spinnerSupplier.setSelection(0) // Reset chọn Supplier về đầu
    }


    private fun addNhom(view: View) {
        themBtn.setOnClickListener {
            val tieuDe = mEditTen.text.toString().trim()

            if (tieuDe.isEmpty()) {
                Toast.makeText(view.context, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // Dừng lại nếu tên bị trống
            }
            val userId = ViewModelProvider(requireActivity())[UserViewModel::class.java].getUser().value?.userId
            if (userId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tempNhom = Nhom().apply {
                id = generatedID
                title = tieuDe
                addTime = Date()
                creatorId = userId // 🛠 Lưu ID người tạo nhóm
                members = mutableListOf(userId) // 🛠 Người tạo nhóm là thành viên đầu tiên
                memNumb = 1 // 🛠 Ban đầu chỉ có 1 thành viên
            }
            val selectedSupplier = selectedSupplierId
            if (selectedSupplier == null) {
                Toast.makeText(view.context, "Vui lòng chọn nhà cung cấp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.i("Nhóm", tempNhom.toString())

            collectionReference.document(generatedID).set(tempNhom)
                .addOnSuccessListener {
                    linkSupplierItemsToNhom(selectedSupplier, generatedID) // 🔥 Liên kết Items
                    Toast.makeText(view.context, "Thêm nhóm thành công!", Toast.LENGTH_SHORT).show()
                    resetForm()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Lỗi khi thêm nhóm: ${e.message}")
                    Toast.makeText(view.context, "Thêm nhóm thất bại!", Toast.LENGTH_SHORT).show()
                }
        }
    }

}