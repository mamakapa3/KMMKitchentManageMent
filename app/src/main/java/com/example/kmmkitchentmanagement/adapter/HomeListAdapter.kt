package com.example.kmmkitchentmanagement.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kmmkitchentmanagement.fragmenthome.ThongBao
import com.example.kmmkitchentmanagement.fragmenthome.TuyChon
import com.example.kmmkitchentmanagement.fragmenthome.frag_CongCu
import com.example.kmmkitchentmanagement.fragmenthome.frag_ThuVien

class HomeListAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> frag_ThuVien()
            1 -> ThongBao()
            2 -> frag_CongCu()
            3 -> TuyChon()
            else -> frag_ThuVien() // Default case if needed
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}
