package com.example.kmmkitchentmanagement.fragmenthome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kmmkitchentmanagement.Data.NetworkUtil;
import com.example.kmmkitchentmanagement.Model.NguoiDung;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.customdialog.ErrorDialog;
import com.example.kmmkitchentmanagement.customdialog.SetPassword;
import com.example.kmmkitchentmanagement.fragment_setting.Infomation;
import com.example.kmmkitchentmanagement.interfaceFile.LogoutListener;
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel;

public class TuyChon extends Fragment {
    private LinearLayout profileLayout;
    private LinearLayout accountOptions;
    private ImageView profileImage;
    private ImageView profileImageArrow;
    private Button mExit;
    private boolean isClicked = false;
    private TextView profileName;
    private TextView ttcn, tdmk, logApp;

    private Handler handler;
    private Runnable runnable;

    private LogoutListener logoutListener;
    private NguoiDung user;

    private ActivityResultLauncher<Intent> infoActivityResultLauncher;

    public TuyChon() {
        // Required empty public constructor
    }

    private UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        infoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = getActivity().getIntent();
                        String userEmail = intent.getStringExtra("user_email");
                        userViewModel.fetchUserData(userEmail);
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getUserData();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tuy_chon, container, false);

        AddView(view);

        return view;
    }

    public void AddView(View view){
        profileLayout = view.findViewById(R.id.profile_layout);
        accountOptions = view.findViewById(R.id.AccoutOptions);
        profileImage = view.findViewById(R.id.profile_image);
        profileImageArrow = view.findViewById(R.id.image_profile_arrow);
        mExit = view.findViewById(R.id.logout);
        profileName = view.findViewById(R.id.profile_name);
        profileName.setText("Không xác định");
        ttcn = view.findViewById(R.id.ttcn);
        tdmk = view.findViewById(R.id.tdmk);
        logApp = view.findViewById(R.id.logLoading);

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAccountClick(v);
            }
        });

        ttcn.setOnClickListener(v -> {
            openInfoFragment();
        });

        tdmk.setOnClickListener(v -> {
            if (!NetworkUtil.isWifiConnected(this.getContext())) {
                showErrorConnectDialog();
            }else{
                SetPassword setPassword = new SetPassword(user.getEmail());
                setPassword.show(getParentFragmentManager(), "setPassword");
            }
        });

        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().finish();
                onLogoutButtonClicked();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof LogoutListener) {
            logoutListener = (LogoutListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LogoutListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        logoutListener = null;
    }

    private void getUserData() {
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            this.user = user;
            if (user != null) {
                profileName.setText(user.getName());
                logApp.setVisibility(View.GONE);
            } else {
                profileName.setText("Không xác định");
                logApp.setVisibility(View.VISIBLE);
            }
        });
    }


    public void onAccountClick(View view) {
        if (!isClicked) {
            accountOptions.setVisibility(View.VISIBLE);
            profileImageArrow.setImageResource(R.drawable.up_arrow);
            isClicked = true;
        } else {
            accountOptions.setVisibility(View.GONE);
            profileImageArrow.setImageResource(R.drawable.arrow_down_sign_to_navigate);
            isClicked = false;
        }
    }

    public void openInfoFragment(){
        Intent intent = new Intent(requireActivity(), Infomation.class);
        intent.putExtra("user_email", user.getEmail()); // Truyền email qua Intent
        infoActivityResultLauncher.launch(intent);
    }

    // When the button is clicked
    public void onLogoutButtonClicked() {
        if (logoutListener != null) {
            logoutListener.onLogout();
        }
    }

    private void showErrorConnectDialog() {
        String log = "Không có kết nối mạng. Vui lòng kiểm tra lại kết nối rồi thử lại sau!";
        ErrorDialog errorDialog = new ErrorDialog(log, false);
        errorDialog.show(getParentFragmentManager(), "errorConnectDialog");
    }
}