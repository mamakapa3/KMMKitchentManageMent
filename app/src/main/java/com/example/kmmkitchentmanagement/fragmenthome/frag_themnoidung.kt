package com.example.kmmkitchentmanagement.fragmenthome

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.kmmkitchentmanagement.Model.LuanVan
import com.example.kmmkitchentmanagement.Model.historyObj
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.customdialog.UploadTaskDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FragThemNoiDung() : Fragment(), Parcelable {

    private var firestore: FirebaseFirestore? = null
    private var collectionReference: CollectionReference? = null
    private var storage: FirebaseStorage? = null
    private var linhvuclist: MutableList<String>? = null
    private var storageReference: StorageReference? = null
    private var luanVanThumbnail: Uri? = null
    private var luanvandocx: Uri? = null
    private var radioXB: RadioGroup? = null
    private var viewDatePushed: LinearLayout? = null
    private var mImageButton: ImageButton? = null
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var mEditTieuDe: EditText? = null
    private var mEditChuThich: EditText? = null
    private var mEditTrichDan: EditText? = null
    private var mEditDatePushed: EditText? = null
    private var mEditTacGia: EditText? = null
    private var mEditLinhVuc: EditText? = null
    private var mAddTacGia: Button? = null
    private var mAddLinhVuc: Button? = null
    private var mAddLV: Button? = null
    private var addFile: Button? = null
    private var setListener: DatePickerDialog.OnDateSetListener? = null

    private var tieuDe: String? = null
    private var linhVuc: String? = null
    private var moTa: String? = null
    private var trichDan: String? = null
    private var tacGia: String? = null
    private var isPushed: Boolean = true
    private var datePushed: Date? = null

    constructor(parcel: Parcel) : this() {
        luanVanThumbnail = parcel.readParcelable(Uri::class.java.classLoader)
        luanvandocx = parcel.readParcelable(Uri::class.java.classLoader)
        tieuDe = parcel.readString()
        linhVuc = parcel.readString()
        moTa = parcel.readString()
        trichDan = parcel.readString()
        tacGia = parcel.readString()
        isPushed = parcel.readByte() != 0.toByte()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_themluanvan, container, false)
//        FirebaseInit()
        AddView(view)
        DataHandler()
        OnRadioClicked(view)
        AddImage(view)
        ChooseDate(view)
        ActivityResult(view)
        return view
    }

    private fun DataHandler() {
        linhvuclist = ArrayList()
        firestore!!.collection("/linhvuc").document("/linhvuc")
            .addSnapshotListener(EventListener { value, error ->
                if (error != null) {
                    return@EventListener
                }
                value?.let {
                    val linhvuc = it["linhvuc"] as List<String>?
                    linhvuc?.let { list ->
                        linhvuclist?.clear()
                        linhvuclist?.addAll(list)
                    }
                }
            })
    }

    private fun AddView(view: View) {
        radioXB = view.findViewById(R.id.radioXB)
        viewDatePushed = view.findViewById(R.id.viewTGLV)
        mImageButton = view.findViewById(R.id.btnAvaLV)
        mEditTieuDe = view.findViewById(R.id.editTitleLV)
        mEditChuThich = view.findViewById(R.id.editMoTaTG)
        mEditTrichDan = view.findViewById(R.id.EditTrichDanLV)
        mEditDatePushed = view.findViewById(R.id.editTGLV)
        mAddTacGia = view.findViewById(R.id.btnAddTG)
        mAddLinhVuc = view.findViewById(R.id.btnAddLinhVuc)
        mEditTacGia = view.findViewById(R.id.editTacGiaLV)
        mEditLinhVuc = view.findViewById(R.id.editLinhVucLV)
        mAddLV = view.findViewById(R.id.btnAddLV)
        addFile = view.findViewById(R.id.documentUpload)
    }

    private fun OnRadioClicked(view: View?) {
        radioXB!!.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.btnRadioXBTrue) {
                viewDatePushed!!.visibility = View.GONE
            } else if (checkedId == R.id.btnRadioXBFalse) {
                viewDatePushed!!.visibility = View.VISIBLE
            }
        }
    }

    private fun AddImage(view: View?) {
        mImageButton!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            activityResultLauncher!!.launch(intent)
        }
        addFile!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            }
            activityResultLauncher!!.launch(intent)
        }
    }

    private fun ActivityResult(view: View?) {
        activityResultLauncher = registerForActivityResult(
            StartActivityForResult(),
            ActivityResultCallback<ActivityResult?> { result ->
                val data = result?.data
                data?.data?.let { temp ->
                    if (temp.toString().lowercase(Locale.getDefault()).contains("image")) {
                        luanVanThumbnail = temp
                        mImageButton!!.setImageURI(luanVanThumbnail)
                    } else {
                        luanvandocx = temp
                        addFile!!.text = luanvandocx.toString()
                    }
                }
            })
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(luanVanThumbnail, flags)
        parcel.writeParcelable(luanvandocx, flags)
        parcel.writeString(tieuDe)
        parcel.writeString(linhVuc)
        parcel.writeString(moTa)
        parcel.writeString(trichDan)
        parcel.writeString(tacGia)
        parcel.writeByte(if (isPushed) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FragThemNoiDung> {
        override fun createFromParcel(parcel: Parcel): FragThemNoiDung {
            return FragThemNoiDung(parcel)
        }

        override fun newArray(size: Int): Array<FragThemNoiDung?> {
            return arrayOfNulls(size)
        }
    }

    private fun ChooseDate(view: View) {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]

        mEditDatePushed!!.setOnClickListener {
            val dialog = DatePickerDialog(
                view.context,
                android.R.style.Theme_Holo_Dialog_MinWidth,
                setListener,
                year,
                month,
                day
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
        setListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val formattedMonth = month + 1
            val date = "$dayOfMonth/$formattedMonth/$year"
            mEditDatePushed!!.setText(date)
        }
    }
//    private fun FirebaseInit() {
//        firestore = FirebaseFirestore.getInstance()
//        collectionReference = firestore.collection("luanvan")
//        storage = FirebaseStorage.getInstance()
//        storageReference = storage.reference.child("luanvan")
//    }

}

