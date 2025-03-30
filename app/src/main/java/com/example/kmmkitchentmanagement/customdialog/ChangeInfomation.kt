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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChangeInfomation(private val thuocTinh: Int, private val currentText: String, private val currentID: String) : DialogFragment() {
    private lateinit var thongTin: TextView
    private lateinit var errorLog: TextView
    private lateinit var newText: EditText
    private var cantEmpty = false
    private var isSuccess = false
    private var listener: OnChangeInformationListener? = null

    interface OnChangeInformationListener {
        fun onInformationChanged(data: String, type: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_change_infomation, null)
        builder.setView(dialogView)

        val dialog = builder.create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        thongTin = dialogView.findViewById(R.id.dialog_message)
        errorLog = dialogView.findViewById(R.id.errorLog)
        errorLog.visibility = View.GONE
        newText = dialogView.findViewById(R.id.username_or_email)
        newText.setText(currentText)

        setVisibleInfo()

        dialogView.findViewById<Button>(R.id.dialog_button_sent).setOnClickListener { checkAction() }
        dialogView.findViewById<Button>(R.id.dialog_button_skip).setOnClickListener { dismiss() }

        return dialog
    }

    private fun setVisibleInfo() {
        when (thuocTinh) {
            1 -> {
                thongTin.text = "Tên người dùng mới:"
                setCantEmpty()
            }
            2 -> {
                thongTin.text = "Email mới:"
                setCantEmpty()
            }
            else -> dismiss()
        }
    }

    private fun setCantEmpty() {
        cantEmpty = true
        newText.apply {
            hint = "Không được để trống"
            setHintTextColor(Color.RED)
        }
    }

    private fun checkAction() {
        val inputText = newText.text.toString()
        when {
            inputText.isEmpty() && cantEmpty -> {
                errorLog.text = "Thông tin bắt buộc không được để trống!"
                errorLog.visibility = View.VISIBLE
            }
            thuocTinh == 2 && !Patterns.EMAIL_ADDRESS.matcher(inputText).matches() -> {
                errorLog.text = "Email không hợp lệ!"
                errorLog.visibility = View.VISIBLE
            }
            else -> updateAction()
        }
    }

    private fun updateAction() {
        if (!NetworkUtil.isWifiConnected(requireContext())) {
            showErrorConnectDialog()
            return
        }

        if (thuocTinh == 2) {
            CoroutineScope(Dispatchers.Main).launch {
                if (checkEmailExist(newText.text.toString())) {
                    errorLog.text = "Email đã tồn tại!"
                    errorLog.visibility = View.VISIBLE
                    return@launch
                }
                updateAuthEmail(newText.text.toString())
                updateDataUser()
            }
        } else {
            updateDataUser()
        }
    }

    private fun updateAuthEmail(email: String) {
        FirebaseAuth.getInstance().currentUser?.verifyBeforeUpdateEmail(email)?.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                showErrorConnectDialog()
            }
        }
    }

    private suspend fun checkEmailExist(email: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        return try {
            val emailExists = db.collection("User").whereEqualTo("email", email).get().await().isEmpty.not()
            val prevEmailExists = db.collection("User").whereEqualTo("previousEmail", email).get().await().isEmpty.not()
            emailExists || prevEmailExists
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error checking email existence: ${e.message}")
            false
        }
    }

    private fun updateDataUser() {
        val db = FirebaseFirestore.getInstance()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = db.collection("User").whereEqualTo("email", currentID).get().await()
                if (!result.isEmpty) {
                    val document = result.documents[0]
                    val updates = mutableMapOf<String, Any>(getField(thuocTinh) to newText.text.toString()).apply {
                        if (thuocTinh == 2) this["previousEmail"] = currentID
                    }
                    document.reference.update(updates).await()
                    listener?.onInformationChanged(newText.text.toString(), thuocTinh)
                    isSuccess = true
                    dismiss()
                } else {
                    showErrorConnectDialog()
                }
            } catch (e: Exception) {
                Log.e("FirebaseError", "Error updating user: ${e.message}")
                showErrorConnectDialog()
            }
        }
    }

    private fun showErrorConnectDialog() {
        val errorDialog = ErrorDialog("Không có kết nối mạng. Vui lòng kiểm tra lại kết nối rồi thử lại sau!", false)
        errorDialog.show(parentFragmentManager, "errorConnectDialog")
    }

    private fun getField(thuocTinh: Int): String = when (thuocTinh) {
        1 -> "name"
        2 -> "email"
        else -> ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnChangeInformationListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement OnChangeInformationListener")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isSuccess) {
            val successDialog = SuccessDialog("Cập nhật thông tin thành công!")
            successDialog.show(parentFragmentManager, "SuccessDialog")
        }
    }
}
