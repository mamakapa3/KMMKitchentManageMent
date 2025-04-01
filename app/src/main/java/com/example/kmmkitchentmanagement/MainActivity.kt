package com.example.kmmkitchentmanagement

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Patterns
import android.view.View
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

class MainActivity : AppCompatActivity() {
    private lateinit var btnDangNhap: Button
    private lateinit var errorLogin: TextView
    private lateinit var passwordEditText: EditText
    private lateinit var usernameOrEmailEditText: EditText
    private lateinit var linkGetPassword: TextView
    private lateinit var gotosignin: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        overridePendingTransition(0, 0)
        mAuth = FirebaseAuth.getInstance()
        addView()
        DangNhap()
        eventHandler()
    }

    private fun addView() {
        btnDangNhap = findViewById(R.id.btnDangNhap)
        passwordEditText = findViewById(R.id.password)
        usernameOrEmailEditText = findViewById(R.id.username_or_email)
        errorLogin = findViewById(R.id.errorLogin)
        linkGetPassword = findViewById(R.id.LinkGetPassword)
        linkGetPassword.setOnClickListener { openForgotPasswordFragment() }
        errorLogin.visibility = View.GONE
        gotosignin = findViewById(R.id.GoToSignin)

//        passwordEditText.setText("123123asd")
//        usernameOrEmailEditText.setText("nguyentrongninh2k3@gmail.com")
    }

    private fun eventHandler() {
        val togglePasswordVisibility: ImageButton = findViewById(R.id.toggle_password_visibility)
        togglePasswordVisibility.setOnClickListener {
            if (passwordEditText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility)
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
            }
        }
        gotosignin.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish() // Kết thúc Activity hiện tại nếu không muốn quay lại trang này
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

    private fun openForgotPasswordFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        VerifyEmail().show(fragmentManager, "verifyEmail")
    }

    private fun DangNhap() {
        btnDangNhap.setOnClickListener {
            if (!NetworkUtil.isWifiConnected(this)) {
                showErrorConnectDialog()
                return@setOnClickListener
            }

            val usernameOrEmail = usernameOrEmailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                errorLogin.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
                errorLogin.visibility = View.VISIBLE
            } else {
                CheckLogin(usernameOrEmail, password)
            }
        }
    }

    private fun CheckLogin(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user?.isEmailVerified == true) {
                        val userId = user.uid

                        // 🔍 Kiểm tra user trong Firestore
                        FirebaseFirestore.getInstance().collection("User").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val intent = Intent(this, MainActivityHome::class.java)
                                    intent.putExtra("user_email", email)
                                    startActivity(intent)
                                    Handler(Looper.getMainLooper()).postDelayed({ finish() }, 300)
                                } else {
                                    FirebaseAuth.getInstance().signOut()
                                    Toast.makeText(this, "Tài khoản chưa đăng ký. Vui lòng đăng ký!", Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Lỗi hệ thống, thử lại sau!", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Email chưa được xác minh!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // ❌ Thay vì hiển thị lỗi chi tiết, chỉ hiển thị thông báo chung chung
                    Toast.makeText(this, "Đăng nhập thất bại! Vui lòng kiểm tra lại tài khoản và mật khẩu.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun showErrorConnectDialog() {
        val fragmentManager: FragmentManager = supportFragmentManager
        ErrorDialog("Không có kết nối mạng. Vui lòng kiểm tra lại kết nối!", false).show(fragmentManager, "errorConnectDialog")
    }
}
