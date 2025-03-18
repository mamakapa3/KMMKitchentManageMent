package com.example.kmmkitchentmanagement

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.kmmkitchentmanagement.Model.NguoiDung
import com.example.kmmkitchentmanagement.adapter.HomeListAdapter
import com.example.kmmkitchentmanagement.customdialog.ErrorDialog
import com.example.kmmkitchentmanagement.fragmenthome.ThongBao
import com.example.kmmkitchentmanagement.fragmenthome.TuyChon
import com.example.kmmkitchentmanagement.fragmenthome.frag_CongCu
//import com.example.kmmkitchentmanagement.fragmenthome.frag_ThuVien
import com.example.kmmkitchentmanagement.interfaceFile.LogoutListener
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore

class MainActivityHome : AppCompatActivity(), LogoutListener {
    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager: ViewPager2

    private val user = NguoiDung()

    private lateinit var db: FirebaseFirestore
    private lateinit var mHomeListAdapter: HomeListAdapter
    private var emailGet: String? = null
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_home)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        emailGet = intent.getStringExtra("user_email")
        emailGet?.let { userViewModel.fetchUserData(it) }

        db = FirebaseFirestore.getInstance()

        mHomeListAdapter = HomeListAdapter(supportFragmentManager, lifecycle)

        setupViews()
    }

    override fun onLogout() {
        logOut()
    }

    private fun setupViews() {
        mTabLayout = findViewById(R.id.tablayouthome)
        mViewPager = findViewById(R.id.viewpagerhome)

        mViewPager.adapter = mHomeListAdapter

        TabLayoutMediator(mTabLayout, mViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Thư viện"
                    tab.setIcon(R.drawable.books)
                }
                1 -> {
                    tab.text = "Thông báo"
                    tab.setIcon(R.drawable.bell)
                }
                2 -> {
                    tab.text = "Danh mục"
                    tab.setIcon(R.drawable.bigger)
                }
                3 -> {
                    tab.text = "Tùy chọn"
                    tab.setIcon(R.drawable.setting)
                }
            }
        }.attach()

        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedPosition = tab.position

                // Lấy Fragment từ tag
//                val currentFragment = supportFragmentManager.findFragmentByTag("f$selectedPosition")
//
//                when (currentFragment) {
//                    is frag_ThuVien -> currentFragment.getUserData()
//                    is ThongBao -> currentFragment.setupObservers()
//                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Không cần xử lý, hoặc thêm logic nếu cần
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Không cần xử lý, hoặc thêm logic nếu cần
            }
        })
    }

    private fun logOut() {
        val intent = Intent(this@MainActivityHome, MainActivity::class.java)
        startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({ finish() }, 300)
    }

    fun showErrorDismissDialog(log: String) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val connectErrorDialog = ErrorDialog(log, true)
        connectErrorDialog.show(fragmentManager, "errorConnectDialog")
    }

    override fun onBackPressed() {
        // Kiểm tra fragment hiện tại
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment is ThongBao || currentFragment is frag_CongCu || currentFragment is TuyChon) {
            mViewPager.setCurrentItem(0)
        } else {
            super.onBackPressed()
        }
    }

    fun getUserViewModel(): UserViewModel {
        return userViewModel
    }
}
