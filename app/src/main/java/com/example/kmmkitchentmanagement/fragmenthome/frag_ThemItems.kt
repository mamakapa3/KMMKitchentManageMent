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
import androidx.fragment.app.activityViewModels
import com.example.kmmkitchentmanagement.AppUtils.Utils
import com.example.kmmkitchentmanagement.AppUtils.serviceModule.ImageSave
import com.example.kmmkitchentmanagement.Model.Items
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.customdialog.UploadTaskDialog
import com.example.kmmkitchentmanagement.fragmentSub.ItemsView
import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.example.kmmkitchentmanagement.viewmodelExtends.SupplierVM
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.UploadTask
import java.util.Date

class frag_ThemItems : Fragment(){
    private lateinit var collectionReference: CollectionReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var themBtn: Button
    private lateinit var mImageButton:ImageButton
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var Thumbnail: Uri? = null
    private var generatedID: String = ""
    private lateinit var backBtn: ImageButton
    private lateinit var editTenItems: EditText
    private lateinit var editNumber: EditText
    private lateinit var editDescription: EditText

    private lateinit var spinnerItemsType: Spinner
    private lateinit var spinnerSupplier: Spinner

    private val NhomVM: NhomVM by activityViewModels()
    private val SupplierVM: SupplierVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_themitems, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addView(view)
        firebaseInit()
        AddImage(view)
        ActivityResult(view)
        eventHandler()
        addItems(view)

        spinnerItemsType = view.findViewById(R.id.spinnerItemsType)
        spinnerSupplier = view.findViewById(R.id.spinnerSupplier)

        val itemsTypeList = listOf("Thịt", "Rau", "Hoa quả", "Dụng cụ", "Gia vị")
        val supplierList = listOf(
            "Meat supplier",
            "Vegetable supplier",
            "Fruit supplier",
            "Kitchen Utensils supplier",
            "Spice Supplier"
        )

        // Tạo adapter cho spinner
        val itemsTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, itemsTypeList)
        val supplierAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, supplierList)

        // Gán adapter vào spinner
        spinnerItemsType.adapter = itemsTypeAdapter
        spinnerSupplier.adapter = supplierAdapter
    }

    private fun firebaseInit() {
        firestore = FirebaseFirestore.getInstance()
        NhomVM.getData().observe(viewLifecycleOwner) { selectedNhom ->
            SupplierVM.getData().observe(viewLifecycleOwner) { selectedSupplier ->
                if (selectedNhom != null || selectedSupplier != null) {
                    collectionReference = firestore.collection("Items")
                    generatedID = collectionReference.document().id
                    Log.d("frag_ThemItems", "📡 Đang thêm Items với ID: $generatedID")
                } else {
                    Log.e("frag_ThemItems", "⚠ Không có nhóm hoặc nhà cung cấp nào được chọn!")
                }
            }
        }
    }

    private fun addView(view: View) {
        mImageButton = view.findViewById(R.id.btnAvaTen)
        backBtn = view.findViewById(R.id.btnbackview)
        themBtn = view.findViewById(R.id.btnAddItems)
        Log.d("frag_ThemItems", "✅ themBtn được tìm thấy: ${themBtn != null}")
        editTenItems = view.findViewById(R.id.editTenItems)
        editNumber = view.findViewById(R.id.editNumber)
        editDescription = view.findViewById(R.id.editDescription)
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
                val savedPath = imageSave.saveImageToLocalStorage(requireContext(), uri, "items_${generatedID}")

                if (savedPath != null) {
                    Log.d("ImageSave", "Image saved at: $savedPath")
                } else {
                    Log.e("ImageSave", "Lưu ảnh thất bại!")
                }
            }
        }
    }

    private fun addItems(view: View) {
        themBtn.setOnClickListener {
            val tieuDe = editTenItems.text.toString().trim()
            val soLuong = editNumber.text.toString().trim().toIntOrNull() ?: 0
            val moTa = editDescription.text.toString().trim()
            val loai = spinnerItemsType.selectedItem.toString()
            val nguonGoc = spinnerSupplier.selectedItem.toString()

            if (tieuDe.isEmpty() || loai.isEmpty() || soLuong <= 0 || nguonGoc.isEmpty() || moTa.isEmpty()) {
                Toast.makeText(requireContext(), "❌ Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tempItems = Items().apply {
                id = generatedID
                itemType = loai
                title = tieuDe
                number = soLuong
                supplier = nguonGoc
                description = moTa
                addTime = Date()
            }

            NhomVM.getData().value?.let { tempItems.nhomId = it.id }
            SupplierVM.getData().value?.let { tempItems.supplierId = it.id }

            collectionReference.document(generatedID).set(tempItems)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "✅ Thêm items mới thành công", Toast.LENGTH_SHORT).show()
                    resetForm()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "❌ Lỗi khi thêm items: ${e.message}")
                    Toast.makeText(requireContext(), "❌ Thêm items thất bại!", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun resetForm() {
        editTenItems.setText("")
        editNumber.setText("")
        editDescription.setText("")
        spinnerItemsType.setSelection(0)
        spinnerSupplier.setSelection(0)
    }

    private fun eventHandler() {
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}