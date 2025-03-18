package com.example.kmmkitchentmanagement.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.kmmkitchentmanagement.Data.NetworkUtil
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.interfaceFile.EmailCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class VerifyEmail : DialogFragment() {
    val db = FirebaseFirestore.getInstance()
    private lateinit var usernameOrEmailEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var skipButton: Button
    private lateinit var logTextView: TextView
    private lateinit var mAuth: FirebaseAuth
    private var isSuccess = false
    private lateinit var log: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mAuth = FirebaseAuth.getInstance()
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_get_verify, null)

        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        usernameOrEmailEditText = view.findViewById(R.id.username_or_email)
        sendButton = view.findViewById(R.id.dialog_button_sent)
        skipButton = view.findViewById(R.id.dialog_button_skip)
        logTextView = view.findViewById(R.id.logText)
        logTextView.visibility = View.GONE

        sendButton.setOnClickListener {
            if (!NetworkUtil.isWifiConnected(requireContext())) {
                showErrorConnectDialog()
                return@setOnClickListener
            }
            sendVerificationEmail()
        }

        skipButton.setOnClickListener {
            dismiss()
        }

        return alertDialog
    }

    private fun sendVerificationEmail() {
        val usernameOrEmail = usernameOrEmailEditText.text.toString().trim()

        when {
            usernameOrEmail.isEmpty() -> showError("Email không được để trống!")
            !Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches() -> showError("Email không hợp lệ!")
            else -> getEmail("email", usernameOrEmail, object : EmailCallback {
                override fun onEmailRetrieved(email: String) {
                    sendVerifyEmail(email)
                }

                override fun onErrorSearching() {
                    showError("Email không tồn tại!")
                }

                override fun onErrorETC() {
                    showErrorConnectDialog()
                }
            })
        }
    }

    fun getEmail(tt: String, username: String, callback: EmailCallback) {
        db.collection("User")
            .whereEqualTo(tt, username)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot) {
                            if (document.contains("previousEmail")) {
                                WarmingDialogChange(
                                    document.getString("name") ?: "",
                                    document.getString("email") ?: "",
                                    document.getString("previousEmail") ?: ""
                                )
                                return@addOnCompleteListener
                            } else {
                                val email = document.getString("email")
                                if (!email.isNullOrEmpty()) {
                                    callback.onEmailRetrieved(email)
                                    return@addOnCompleteListener
                                }
                            }
                        }
                    } else if (tt == "email") {
                        db.collection("User")
                            .whereEqualTo("previousEmail", username)
                            .get()
                            .addOnCompleteListener { taskTwo ->
                                if (taskTwo.isSuccessful && !taskTwo.result.isEmpty) {
                                    for (document in taskTwo.result) {
                                        WarmingDialogChange(
                                            document.getString("name") ?: "",
                                            document.getString("email") ?: "",
                                            document.getString("previousEmail") ?: ""
                                        )
                                        return@addOnCompleteListener
                                    }
                                } else {
                                    callback.onErrorSearching()
                                }
                            }
                            .addOnFailureListener {
                                callback.onErrorETC()
                            }
                    } else {
                        callback.onErrorSearching()
                    }
                } else {
                    callback.onErrorETC()
                }
            }
            .addOnFailureListener {
                Log.e("FirebaseError", "Error signing in: \${it.message}")
                showErrorConnectDialog()
            }
    }

    fun WarmingDialogChange(name: String, newEmail: String, oldEmail: String) {
        val fragmentManager = parentFragmentManager
        val warmingDialog = WarmingDialog(
            "Tài khoản: <b><i>" + name + "</i></b> chưa xác thực email: <b><i><font color='red'>" + newEmail + "</font></i></b>." +
                    "Vui lòng xác thực email hoặc chọn <b>Khôi phục</b> để sử dụng lại email: <b><i><font color='blue'>" + oldEmail + "</font></i></b>.",
            "Khôi phục", "Bỏ qua"
        )
        warmingDialog.show(fragmentManager, "WarmingDialog")
        fragmentManager.setFragmentResultListener(
            "requestKey",
            this
        ) { requestKey: String?, bundle: Bundle ->
            val result = bundle.getBoolean("result")
            if (result) {
                getOldEmail(oldEmail)
            }
        }
    }

    fun getOldEmail(oldEmail: String) {
        db.collection("User")
            .whereEqualTo("previousEmail", oldEmail)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (!documents.isNullOrEmpty()) {
                        val document = documents[0]
                        val updates = hashMapOf<String, Any>(
                            "email" to oldEmail,
                            "previousEmail" to FieldValue.delete()
                        )

                        document.reference.update(updates as Map<String, Any>)
                            .addOnSuccessListener {
                                Log.d("TAG", "Updated successfully.")
                                successDialogShow("Đã khôi phục tài khoản!")
                            }
                            .addOnFailureListener { e ->
                                Log.e("TAG", "Failed to update user name: ${e.message}")
                                showErrorConnectDialog()
                            }
                    } else {
                        showErrorConnectDialog()
                    }
                } else {
                    Log.e("TAG", "Failed to find user document: ${task.exception}")
                    showErrorConnectDialog()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseError", "Error signing in: ${e.message}")
                showErrorConnectDialog()
            }
    }

    private fun sendVerifyEmail(email: String) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isSuccess = true
                dismiss()
            } else {
                showError("Email không tồn tại!")
                if (!NetworkUtil.isWifiConnected(requireContext())) {
                    showErrorConnectDialog()
                }
            }
        }.addOnFailureListener {
            showErrorConnectDialog()
        }
    }

    private fun showErrorConnectDialog() {
        val errorDialog = ErrorDialog("Không có kết nối mạng. Vui lòng kiểm tra lại kết nối rồi thử lại sau!", false)
        errorDialog.show(parentFragmentManager, "errorConnectDialog")
    }

    private fun showError(message: String) {
        logTextView.text = message
        logTextView.setTextColor(Color.RED)
        logTextView.visibility = View.VISIBLE
    }

    fun errorAccountSearching(errLog: String) {
        log.text = errLog
        log.setTextColor(Color.RED)
        log.visibility = View.VISIBLE
    }

    fun successDialogShow(log: String) {
        val successDialog = SuccessDialog(log)
        successDialog.show(parentFragmentManager, "SuccessDialog")
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isSuccess) {
            val successDialog = SuccessDialog("Chúng tôi đã gửi mật khẩu xác thực đến email, vui lòng truy cập để thay đổi mật khẩu!")
            successDialog.show(parentFragmentManager, "SuccessDialog")
        }
    }
}
