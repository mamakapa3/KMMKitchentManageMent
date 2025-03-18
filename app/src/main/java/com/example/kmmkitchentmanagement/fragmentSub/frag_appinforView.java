package com.example.kmmkitchentmanagement.fragmentSub;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kmmkitchentmanagement.R;

public class frag_appinforView extends Fragment {

    ImageButton backBtn;
    TextView txtGT;

    public frag_appinforView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_appinfor_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backBtn=view.findViewById(R.id.backBtn);
        txtGT = view.findViewById(R.id.txtGioiThieu);
        textViewGT(view);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
    public void textViewGT(View view){
        String text = "     Quản lý luận văn là ứng dụng được tạo ra với mục đính quản lý tài liệu luận văn; đảm bảo tài nguyên, tính toàn vẹn và ổn định của luận văn" +
                " trước và sau khi xuất bản.";
        SpannableString ss = new SpannableString(text);
        StyleSpan stuleSpan = new StyleSpan(Typeface.BOLD);
        ss.setSpan(stuleSpan, 5, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtGT.setText(ss);
    }
}