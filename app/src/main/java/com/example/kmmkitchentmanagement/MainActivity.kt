package com.example.kmmkitchentmanagement

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.kmmkitchentmanagement.Data.NetworkUtil
import com.example.kmmkitchentmanagement.customdialog.*
import com.example.kmmkitchentmanagement.interfaceFile.EmailCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MainActivity : AppCompatActivity() {
    private lateinit var btnDangNhap: Button
    private lateinit var errorLogin: TextView
    private lateinit var passwordEditText: EditText
    private lateinit var usernameOrEmailEditText: EditText
    private lateinit var linkGetPassword: TextView
    private lateinit var mAuth: FirebaseAuth
    private var loadingDialogFragment: Loading? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        overridePendingTransition(0, R.anim.bottom_down)
        mAuth = FirebaseAuth.getInstance()
        addView()
        DangNhap()
    }

    private fun addView() {
        btnDangNhap = findViewById(R.id.btnDangNhap)
        passwordEditText = findViewById(R.id.password)
        usernameOrEmailEditText = findViewById(R.id.username_or_email)
        val togglePasswordVisibility: ImageButton = findViewById(R.id.toggle_password_visibility)
        errorLogin = findViewById(R.id.errorLogin)
        linkGetPassword = findViewById(R.id.LinkGetPassword)
        linkGetPassword.setOnClickListener { openForgotPasswordFragment() }

        errorLogin.visibility = View.GONE

        togglePasswordVisibility.setOnClickListener {
            if (passwordEditText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility)
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        passwordEditText.setText("123456a@")
        usernameOrEmailEditText.setText("nguyentrongninh2k3@gmail.com")
    }

    override fun onStart() {
        super.onStart()
        if (!NetworkUtil.isWifiConnected(this)) {
            showErrorConnectDialog()
        }
        val currentUser: FirebaseUser? = mAuth.currentUser
        currentUser?.let {
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

            showLoading()

            if (!Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
                errorLogin.visibility = View.VISIBLE
                hideLoading()
            } else {
                val fragment = supportFragmentManager.findFragmentByTag("customdialog") as? VerifyEmail
                fragment?.getEmail("email", usernameOrEmail, object : EmailCallback {
                    override fun onEmailRetrieved(email: String) {
                        CheckLogin(email, password)
                    }

                    override fun onErrorSearching() {
                        errorLogin.visibility = View.VISIBLE
                        hideLoading()
                    }

                    override fun onErrorETC() {
                        showErrorConnectDialog()
                    }
                })
            }
        }
    }

    private fun CheckLogin(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideLoading()
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivityHome::class.java)
                    intent.putExtra("user_email", email)
                    startActivity(intent)
                    Handler(Looper.getMainLooper()).postDelayed({ finish() }, 300)
                } else {
                    errorLogin.visibility = View.VISIBLE
                    if (!NetworkUtil.isWifiConnected(this)) {
                        showErrorConnectDialog()
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FirebaseError", "Error signing in: ${it.message}")
                showErrorConnectDialog()
            }
    }

    private fun showErrorConnectDialog() {
        hideLoading()
        val log = "Không có kết nối mạng. Vui lòng kiểm tra lại kết nối rồi thử lại sau!"
        val fragmentManager: FragmentManager = supportFragmentManager
        ErrorDialog(log, false).show(fragmentManager, "errorConnectDialog")
    }

    private fun showLoading() {
        if (loadingDialogFragment == null) {
            loadingDialogFragment = Loading()
        }
        loadingDialogFragment?.show(supportFragmentManager, "loading")
    }

    private fun hideLoading() {
        loadingDialogFragment?.dismiss()
    }
}
