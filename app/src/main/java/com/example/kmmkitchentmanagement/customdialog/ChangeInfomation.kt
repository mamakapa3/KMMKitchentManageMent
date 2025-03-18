package com.example.kmmkitchentmanagement.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.kmmkitchentmanagement.Data.NetworkUtil
import com.example.kmmkitchentmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import java.util.concurrent.atomic.AtomicBoolean

class ChangeInformation(private val thuocTinh: Int, private val currentText: String, private val currentID: String) : DialogFragment() {

    private lateinit var thongTin: TextView
    private lateinit var errorLog: TextView
    private lateinit var newText: EditText
    private lateinit var okButton: Button
    private lateinit var skipButton: Button

    private var cantEmpty = false
    private var isSuccess = false

    interface OnChangeInformationListener {
        fun onInformationChanged(data: String, type: Int){

        }
    }

    private lateinit var listener: OnChangeInformationListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_change_infomation, null)

        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        thongTin = dialogView.findViewById(R.id.dialog_message)
        errorLog = dialogView.findViewById(R.id.errorLog)
        newText = dialogView.findViewById(R.id.username_or_email)
        okButton = dialogView.findViewById(R.id.dialog_button_sent)
        skipButton = dialogView.findViewById(R.id.dialog_button_skip)

        errorLog.visibility = View.GONE
        newText.setText(currentText)

        setVisibleInfo()

        okButton.setOnClickListener { checkAction() }
        skipButton.setOnClickListener { dismiss() }

        return dialog
    }

    private fun setVisibleInfo() {
        when (thuocTinh) {
            1 -> {
                thongTin.text = "Tên người dùng mới:"
                cantEmpty = true
                newText.hint = "Không được để trống"
                newText.setHintTextColor(Color.RED)
            }
            2 -> {
                thongTin.text = "Email mới:"
                cantEmpty = true
                newText.hint = "Không được để trống"
                newText.setHintTextColor(Color.RED)
            }
            else -> dismiss()
        }
    }

    private fun checkAction() {
        val inputText = newText.text.toString()

        if (inputText.isEmpty()) {
            if (cantEmpty) {
                errorLog.text = "Thông tin bắt buộc không được để trống!"
                errorLog.visibility = View.VISIBLE
            } else {
                doUpdateAction()
            }
        } else if (thuocTinh == 2 && !Patterns.EMAIL_ADDRESS.matcher(inputText).matches()) {
            errorLog.text = "Email không hợp lệ!"
            errorLog.visibility = View.VISIBLE
        } else {
            doUpdateAction()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as OnChangeInformationListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnChangeInformationListener")
        }
    }

    private fun doUpdateAction() {
        if (!NetworkUtil.isWifiConnected(context)) {
            showErrorDialog()
            return
        }

        if (thuocTinh == 2 && checkEmailExists(newText.text.toString())) {
            errorLog.text = "Email đã tồn tại!"
            errorLog.visibility = View.VISIBLE
            return
        }

        updateUserData()
    }

    private fun checkEmailExists(email: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        val exists = AtomicBoolean(false)

        db.collection("User").whereEqualTo("email", email).get().addOnSuccessListener {
            if (!it.isEmpty) exists.set(true)
        }

        db.collection("User").whereEqualTo("previousEmail", email).get().addOnSuccessListener {
            if (!it.isEmpty) exists.set(true)
        }

        return exists.get()
    }

    private fun updateUserData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("User").whereEqualTo("email", currentID).get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val doc = documents.documents[0]
                val updates = hashMapOf(getField(thuocTinh) to newText.text.toString())

                if (thuocTinh == 2) updates["previousEmail"] = currentID

                doc.reference.update(updates as Map<String, Any>).addOnSuccessListener {
                    listener.onInformationChanged(newText.text.toString(), thuocTinh)
                    isSuccess = true
                    dismiss()
                }.addOnFailureListener { showErrorDialog() }
            }
        }
    }

    private fun getField(type: Int): String = if (type == 1) "name" else "email"

    private fun showErrorDialog() {
        val errorDialog = ErrorDialog("Không có kết nối mạng. Vui lòng kiểm tra lại!", false)
        errorDialog.show(parentFragmentManager, "errorConnectDialog")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isSuccess) SuccessDialog("Cập nhật thông tin thành công!").show(parentFragmentManager, "SuccessDialog")
    }
}
