package com.example.kmmkitchentmanagement

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2

import com.example.kmmkitchentmanagement.Model.NguoiDung
import com.example.kmmkitchentmanagement.adapter.HomeListAdapter
import com.example.kmmkitchentmanagement.customdialog.ErrorDialog
import com.example.kmmkitchentmanagement.fragmenthome.frag_Nhom
import com.example.kmmkitchentmanagement.fragmenthome.frag_ThongBao
import com.example.kmmkitchentmanagement.fragmenthome.frag_TuyChon
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
    private lateinit var emailGet: String
    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_home)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        emailGet = intent.getStringExtra("user_email").orEmpty()
        userViewModel.fetchUserData(emailGet)

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
                    tab.text = "Nhóm của bạn"
                    tab.setIcon(R.drawable.authors)
                }
                1 -> {
                    tab.text = "Nhà cung cấp"
                    tab.setIcon(R.drawable.parcel)
                }
                2 -> {
                    tab.text = "Thông báo"
                    tab.setIcon(R.drawable.bell)
                }
                3 -> {
                    tab.text = "Tùy chọn"
                    tab.setIcon(R.drawable.setting)
                }
            }
        }.attach()

//        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                val selectedPosition = tab.position
//                val currentFragment = supportFragmentManager.findFragmentByTag("f$selectedPosition")
//
//                when (currentFragment) {
//                    is frag_Nhom -> currentFragment.getUserData()
//                    is ThongBao -> currentFragment.setupObservers()
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })
    }

    private fun logOut() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({ finish() }, 300)
    }

    fun showErrorDismissDialog(log: String) {
        val fragmentManager = supportFragmentManager
        val connectErrorDialog = ErrorDialog(log, true)
        connectErrorDialog.show(fragmentManager, "errorConnectDialog")
    }

//    override fun onBackPressed() {
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
//
//        if (currentFragment is frag_ThemNhom||
//            currentFragment is frag_TuyChon ||
//            currentFragment is frag_ThongBao
//        ) {
//            mViewPager.currentItem = 0
//        } else {
//            super.onBackPressed()
//        }
//    }

    fun getUserViewModel(): UserViewModel {
        return userViewModel
    }
}
