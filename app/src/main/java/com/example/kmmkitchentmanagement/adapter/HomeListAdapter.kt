package com.example.kmmkitchentmanagement.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.kmmkitchentmanagement.Model.NguoiDung;
import com.example.kmmkitchentmanagement.fragmenthome.ThongBao;
import com.example.kmmkitchentmanagement.fragmenthome.TuyChon;
import com.example.kmmkitchentmanagement.fragmenthome.frag_CongCu;
import com.example.kmmkitchentmanagement.fragmenthome.frag_ThuVien;

public class HomeListAdapter extends FragmentStateAdapter {
    private NguoiDung user;

    public HomeListAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(@NonNull int position){
        switch(position){
            case 0:
                return new frag_ThuVien();
            case 1:
                return new ThongBao();
            case 2:
                return new frag_CongCu();
            case 3:
                return new TuyChon();
            default:
                return new frag_ThuVien();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
