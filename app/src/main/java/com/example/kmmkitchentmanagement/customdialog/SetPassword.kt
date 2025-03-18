package com.example.kmmkitchentmanagement.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.kmmkitchentmanagement.Data.NetworkUtil
import com.example.kmmkitchentmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class SetPassword(private val email: String) : DialogFragment() {

    private lateinit var passwordEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var skipButton: Button
    private lateinit var log: TextView
    private lateinit var mAuth: FirebaseAuth
    private var isSuccess = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mAuth = FirebaseAuth.getInstance()
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_get_password, null)

        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        passwordEditText = view.findViewById(R.id.password)
        val togglePasswordVisibility: ImageButton = view.findViewById(R.id.toggle_password_visibility)

        togglePasswordVisibility.setOnClickListener {
            if (passwordEditText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility)
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        sendButton = view.findViewById(R.id.dialog_button_sent)
        skipButton = view.findViewById(R.id.dialog_button_skip)
        log = view.findViewById(R.id.logText)
        log.visibility = View.GONE

        sendButton.setOnClickListener {
            if (!NetworkUtil.isWifiConnected(requireContext())) {
                showErrorConnectDialog()
                return@setOnClickListener
            }
            val password = passwordEditText.text.toString()
            when {
                password.isEmpty() -> {
                    log.visibility = View.VISIBLE
                    log.text = "Mật khẩu không được để trống."
                }
                password.length < 8 -> {
                    log.visibility = View.VISIBLE
                    log.text = "Mật khẩu phải có ít nhất 8 ký tự."
                }
                else -> checkEmail()
            }
        }

        skipButton.setOnClickListener { dismiss() }

        return alertDialog
    }

    private fun checkEmail() {
        val db = FirebaseFirestore.getInstance()
        db.collection("User").whereEqualTo("email", email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && !task.result.isEmpty) {
                    for (document in task.result) {
                        if (document.contains("previousEmail")) {
                            showErrorEmailDialog()
                            return@addOnCompleteListener
                        }
                    }
                    sendVerify(email)
                } else {
                    showErrorConnectDialog()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseError", "Error checking email: ${e.message}")
                showErrorConnectDialog()
            }
    }

    private fun sendVerify(email: String) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isSuccess = true
                dismiss()
            } else {
                if (!NetworkUtil.isWifiConnected(requireContext())) {
                    showErrorConnectDialog()
                }
            }
        }.addOnFailureListener { e ->
            Log.e("FirebaseError", "Error sending verification: ${e.message}")
            showErrorConnectDialog()
        }
    }

    private fun showErrorConnectDialog() {
        val errorDialog = ErrorDialog("Không có kết nối mạng. Vui lòng kiểm tra lại kết nối rồi thử lại sau!", false)
        errorDialog.show(parentFragmentManager, "errorConnectDialog")
    }

    private fun showErrorEmailDialog() {
        val errorDialog = ErrorDialog("Email của bạn chưa được xác thực, vui lòng truy cập email để xác thực trước rồi quay lại sau!", false)
        errorDialog.show(parentFragmentManager, "errorEmailDialog")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isSuccess) {
            val successDialog = SuccessDialog("Chúng tôi đã gửi mật khẩu xác thực đến email, vui lòng truy cập để thay đổi mật khẩu!")
            successDialog.show(parentFragmentManager, "SuccessDialog")
        }
    }
}
