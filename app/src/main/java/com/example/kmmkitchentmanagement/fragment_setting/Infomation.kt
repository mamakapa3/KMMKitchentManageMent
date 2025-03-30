package com.example.kmmkitchentmanagement.fragment_setting

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.Data.NetworkUtil
import com.example.kmmkitchentmanagement.Model.NguoiDung
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.customdialog.ChangeInfomation
import com.example.kmmkitchentmanagement.customdialog.ErrorDialog

import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel

class Infomation : AppCompatActivity(), ChangeInfomation.OnChangeInformationListener {

    private var user = NguoiDung()
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var role: TextView
    private lateinit var back: ImageButton
    private lateinit var btnOption: ImageButton
    private var isEdit = false
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_frag__infomation)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val emailName = intent.getStringExtra("user_email")

        if (!NetworkUtil.isWifiConnected(this)) {
            showErrorDismissDialog("Không có kết nối Internet. Vui lòng kiểm tra lại kết nối và thử lại.", true)
            return
        }

        initializeViews()
        fetchDataFromFirebase(emailName)
    }

    private fun initializeViews() {
        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        role = findViewById(R.id.role)

        setDefaultView()

        back = findViewById(R.id.btnBack)
        back.setOnClickListener { navigateBack() }

        btnOption = findViewById(R.id.btnOption)
        btnOption.setOnClickListener { showPopupMenu(it) }
    }

    private fun fetchDataFromFirebase(userEmail: String?) {
        val email = userEmail ?: return // Thoát nếu null
        Log.d("User Data", "Fetching user data")
        userViewModel.fetchUserData(email)

        userViewModel.getUser().observe(this) { fetchedUser ->
            fetchedUser?.let {
                user = it
                Log.d("User Data", "User data fetched: $user")
                updateUserInfo()
            } ?: Log.d("User Data", "User data is null")
        }
        userViewModel.getError().observe(this) { error ->
            error?.let { showErrorDismissDialog(it, true) }
        }
    }

    private fun updateUserInfo() {
        username.text = user.name ?: "Không xác định"
        email.text = user.email ?: "Không xác định"
        role.text = user.getRole()
    }

    private fun setDefaultView() {
        username.setTextColor(Color.parseColor("#0000aa"))
        username.isClickable = false
        email.setTextColor(Color.parseColor("#0000aa"))
        email.isClickable = false
    }

    private fun setEditView() {
        username.setTextColor(Color.parseColor("#ff0000"))
        username.isClickable = true
        email.setTextColor(Color.parseColor("#ff0000"))
        email.isClickable = true

        username.setOnClickListener { showEditDialog(1, username.text.toString()) }
        email.setOnClickListener { showEditDialog(2, email.text.toString()) }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.menu_user, menu)
            menu.findItem(R.id.action_edit).title = if (isEdit) "Tắt chỉnh sửa" else "Bật chỉnh sửa"
        }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    if (isEdit) {
                        setDefaultView()
                        Toast.makeText(this, "Hiển thị chế độ người xem", Toast.LENGTH_LONG).show()
                    } else {
                        if (!NetworkUtil.isWifiConnected(this)) {
                            showErrorDismissDialog("Không có kết nối Internet. Vui lòng kiểm tra lại kết nối và thử lại.", false)
                            return@setOnMenuItemClickListener true
                        }
                        setEditView()
                        Toast.makeText(this, "Hiển thị chế độ chỉnh sửa", Toast.LENGTH_LONG).show()
                    }
                    isEdit = !isEdit
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showErrorDismissDialog(message: String, finishActivity: Boolean) {
        ErrorDialog(message, finishActivity).show(supportFragmentManager, "errorConnectDialog")
    }

    private fun navigateBack() {
        val resultIntent = Intent().apply {
            putExtra("user_email", user.email)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onBackPressed() {
        navigateBack()
        super.onBackPressed()
    }

    private fun showEditDialog(index: Int, currentText: String) {
        val fragmentManager = supportFragmentManager
        ChangeInfomation(index, currentText, user.email).show(fragmentManager, "editDialog")
    }

    override fun onInformationChanged(data: String, type: Int) {
        when (type) {
            1 -> {
                user.name = data
                username.text = data
                username.setTextColor(Color.parseColor("#008000"))
            }
            2 -> {
                user.email = data
                email.text = data
                email.setTextColor(Color.parseColor("#008000"))
            }
        }
    }
}