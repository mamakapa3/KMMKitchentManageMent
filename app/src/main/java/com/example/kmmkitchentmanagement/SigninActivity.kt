package com.example.kmmkitchentmanagement


import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.kmmkitchentmanagement.Data.NetworkUtil
import com.example.kmmkitchentmanagement.customdialog.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SigninActivity : AppCompatActivity() {
    private lateinit var btnDangKy: Button
    private lateinit var passwordEditText: EditText
    private lateinit var usernameOrEmailEditText: EditText
    private lateinit var passwordConfirmEditText: EditText
    private lateinit var backToLogin : TextView
    private lateinit var mAuth: FirebaseAuth
    private var loadingDialogFragment: Loading? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_activity)
        overridePendingTransition(0, 0)
        mAuth = FirebaseAuth.getInstance()
        addView()
        eventHandler()
        DangKy()

    }

    private fun addView() {
        btnDangKy = findViewById(R.id.btnDangKy)
        passwordEditText = findViewById(R.id.password)
        passwordConfirmEditText = findViewById(R.id.confirmpassword)
        usernameOrEmailEditText = findViewById(R.id.username_or_email)
        passwordEditText.setSelection(passwordEditText.text.length)
        backToLogin = findViewById(R.id.BackToLogin)
    }

    private fun togglePasswordVisibility(editText: EditText, toggleButton: ImageButton) {
        if (editText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleButton.setImageResource(R.drawable.ic_visibility)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleButton.setImageResource(R.drawable.ic_visibility_on)
        }
        editText.setSelection(editText.text.length) // Giữ con trỏ ở cuối văn bản
    }

    private fun eventHandler() {
        val togglePasswordVisibility: ImageButton = findViewById(R.id.toggle_password_visibility)
        val togglePasswordVisibilityConfirm: ImageButton = findViewById(R.id.toggle_password_visibility1) // Nút cho confirm password

        togglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility(passwordEditText, togglePasswordVisibility)
        }
        togglePasswordVisibilityConfirm.setOnClickListener {
            togglePasswordVisibility(passwordConfirmEditText, togglePasswordVisibilityConfirm)
        }
        backToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onStart() {
        super.onStart()
        if (!NetworkUtil.isWifiConnected(this)) {
            showErrorConnectDialog()
        }
        mAuth.currentUser?.let {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun DangKy() {
        btnDangKy.setOnClickListener {
            if (!NetworkUtil.isWifiConnected(this)) {
                showErrorConnectDialog()
                return@setOnClickListener
            }

            val usernameOrEmail = usernameOrEmailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val passwordConfirm = passwordConfirmEditText.text.toString().trim()

            if (usernameOrEmail.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hiển thị loading
            loadingDialogFragment = Loading()
            loadingDialogFragment?.show(supportFragmentManager, "loading")

            // Đăng ký với Firebase Authentication
            mAuth.createUserWithEmailAndPassword(usernameOrEmail, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = mAuth.currentUser?.uid
                        if (userId != null) {
                            mAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    // 🟢 Lưu vào Firestore sau khi gửi email xác minh
                                    saveUserToFirestore(userId, usernameOrEmail)
                                } else {
                                    loadingDialogFragment?.dismiss()
                                    Toast.makeText(this, "Không thể gửi email xác minh: ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        loadingDialogFragment?.dismiss()
                        Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }

    private fun saveUserToFirestore(userId: String, email: String) {
        // Gán role mặc định là "Người dùng" (role = 2)
        val user = hashMapOf(
            "userId" to userId,
            "email" to email,
            "role" to 2,  // Role mặc định là Người dùng
            "createdAt" to System.currentTimeMillis()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("User").document(userId)
            .set(user)
            .addOnSuccessListener {
                loadingDialogFragment?.dismiss()
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                loadingDialogFragment?.dismiss()
                Toast.makeText(this, "Lỗi khi lưu dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun showErrorConnectDialog() {
        val fragmentManager: FragmentManager = supportFragmentManager
        ErrorDialog("Không có kết nối mạng. Vui lòng kiểm tra lại kết nối!", false).show(fragmentManager, "errorConnectDialog")
    }
}
