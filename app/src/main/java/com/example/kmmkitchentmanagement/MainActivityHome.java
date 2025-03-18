package com.example.kmmkitchentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kmmkitchentmanagement.Model.NguoiDung;
import com.example.kmmkitchentmanagement.adapter.HomeListAdapter;
import com.example.kmmkitchentmanagement.customdialog.ErrorDialog;
import com.example.kmmkitchentmanagement.fragmenthome.ThongBao;
import com.example.kmmkitchentmanagement.fragmenthome.TuyChon;
import com.example.kmmkitchentmanagement.fragmenthome.frag_CongCu;
import com.example.kmmkitchentmanagement.fragmenthome.frag_ThuVien;
import com.example.kmmkitchentmanagement.interfaceFile.LogoutListener;
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivityHome extends AppCompatActivity implements LogoutListener {
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;

    NguoiDung user = new NguoiDung();

    private FirebaseFirestore db;
    private HomeListAdapter mHomeListAdapter;
    private String emailGet;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_home);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        emailGet = getIntent().getStringExtra("user_email");
        userViewModel.fetchUserData(emailGet);

        db = FirebaseFirestore.getInstance();

        mHomeListAdapter = new HomeListAdapter(getSupportFragmentManager(), getLifecycle());

        setupViews();

    }

    @Override
    public void onLogout() {
        logOut();
    }

    private void setupViews() {
        mTabLayout = findViewById(R.id.tablayouthome);
        mViewPager = findViewById(R.id.viewpagerhome);

        mViewPager.setAdapter(mHomeListAdapter);

        new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Thư viện");
                    tab.setIcon(R.drawable.books);
                    break;
                case 1:
                    tab.setText("Thông báo");
                    tab.setIcon(R.drawable.bell);
                    break;
                case 2:
                    tab.setText("Danh mục");
                    tab.setIcon(R.drawable.bigger);
                    break;
                case 3:
                    tab.setText("Tùy chọn");
                    tab.setIcon(R.drawable.setting);
                    break;
            }
        }).attach();

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedPosition = tab.getPosition();

                // Lấy Fragment từ tag
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + selectedPosition);

                if (currentFragment instanceof frag_ThuVien) {
                    ((frag_ThuVien) currentFragment).getUserData();
                }else if (currentFragment instanceof ThongBao) {
                    ((ThongBao) currentFragment).setupObservers();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Không cần xử lý, hoặc thêm logic nếu cần
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Không cần xử lý, hoặc thêm logic nếu cần
            }
        });
    }

    private void logOut() {
        Intent intent = new Intent(MainActivityHome.this, MainActivity.class);
        startActivity(intent);
        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 300);
    }

    public void showErrorDismissDialog(String log) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ErrorDialog connectErrorDialog = new ErrorDialog(log, true);
        connectErrorDialog.show(fragmentManager, "errorConnectDialog");
    }

    @Override
    public void onBackPressed() {
        // Kiểm tra fragment hiện tại
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(currentFragment instanceof ThongBao ||
            currentFragment instanceof frag_CongCu ||
            currentFragment instanceof TuyChon){
                mViewPager.setCurrentItem(0);
        }else{
            super.onBackPressed();
        }
    }

    public UserViewModel getUserViewModel() {
        return userViewModel;
    }
}
