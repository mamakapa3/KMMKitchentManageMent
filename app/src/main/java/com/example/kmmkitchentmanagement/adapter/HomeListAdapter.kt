package com.example.kmmkitchentmanagement.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kmmkitchentmanagement.Model.NguoiDung
import com.example.kmmkitchentmanagement.fragmenthome.*

class HomeListAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var user: NguoiDung? = null

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> frag_Nhom()
            1 -> frag_Supplier()
            2 -> frag_ThongBao()
            3 -> frag_TuyChon()
            else -> frag_Nhom()
        }
    }
    override fun getItemCount(): Int = 4
}