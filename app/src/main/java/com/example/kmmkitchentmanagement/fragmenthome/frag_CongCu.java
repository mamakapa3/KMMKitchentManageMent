package com.example.kmmkitchentmanagement.fragmenthome;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.kmmkitchentmanagement.Model.LuanVan;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.fragmentSub.frag_appinforView;
import com.example.kmmkitchentmanagement.fragmentSub.frag_lichsu;
import com.example.kmmkitchentmanagement.interfaceFile.LogoutListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class frag_CongCu extends Fragment {
    FirebaseFirestore firestore;
    SwipeRefreshLayout refreshLayout;
    TextView all,recent7,recent30,onprogress,published,txtCCTongTG,txtCCTongCD;
    private Button mExit,historyBtn,ViewInforBtn,savetofileBtn;

    public frag_CongCu() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag__cong_cu, container, false);

        AddView(view);
        Exit(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseInit();
        DataHandler();
        EventHandler();
    }
    protected void FirebaseInit() {
        firestore=FirebaseFirestore.getInstance();
    }
    protected void DataHandler(){
        Date last7day=new Date();
        last7day.setDate(last7day.getDate()-7);
        Date last30day=new Date();
        last30day.setDate(last30day.getDate()-30);
        firestore.collection("/luanvan").count()
                .get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        all.setText(String.valueOf(task.getResult().getCount()));
                    }
                });
        firestore.collection("/luanvan")
                .whereGreaterThan("addTime",last7day).count()
                .get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        recent7.setText( String.valueOf(task.getResult().getCount()));
                    }
                });
        firestore.collection("/luanvan")
                .whereGreaterThan("addTime",last30day).count()
                .get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        recent30.setText(String.valueOf(task.getResult().getCount()));
                    }
                });
        firestore.collection("/luanvan")
                .whereEqualTo("published",true).count()
                .get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        published.setText(String.valueOf(task.getResult().getCount()));

                    }
                });
        firestore.collection("/luanvan")
                .whereEqualTo("published",false).count()
                .get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        onprogress.setText(String.valueOf(task.getResult().getCount()));

                    }
                });

        firestore.collection("/tacgia").count()
                .get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        txtCCTongTG.setText(String.valueOf(task.getResult().getCount()));
                    }
                });

        firestore.collection("/linhvuc").document("/linhvuc")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){

                        }
                        else{
                            if((List<String>)value.get("linhvuc")!=null){
                                txtCCTongCD.setText(String.valueOf(((List<String>)value.get("linhvuc")).size()));
                            }
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
    }


    private void AddView(View view) {
        historyBtn=view.findViewById(R.id.historyBtn);
        refreshLayout=view.findViewById(R.id.swiperLayout);
        mExit = (Button) view.findViewById(R.id.btnExit);

        all=view.findViewById(R.id.all);
        recent7=view.findViewById(R.id.recent7);
        recent30=view.findViewById(R.id.recent30);
        onprogress=view.findViewById(R.id.onprogress);
        published=view.findViewById(R.id.published);
        txtCCTongTG=view.findViewById(R.id.txtCCTongTG);
        txtCCTongCD=view.findViewById(R.id.txtCCTongCD);
        ViewInforBtn=view.findViewById(R.id.inforView);
        savetofileBtn=view.findViewById(R.id.savetofileBtn);
    }
    protected  void EventHandler(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataHandler();
            }
        });
        historyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.congcuMain,new frag_lichsu())
                        .addToBackStack("xem lich su")
                        .commit();
            }
        });
        ViewInforBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.congcuMain,new frag_appinforView())
                        .addToBackStack("xem lich su")
                        .commit();
            }
        });
        savetofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file=new File(path+"/luanvanText.txt");
                StringBuilder sb=new StringBuilder();
                firestore.collection("/luanvan").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List luanvan=new ArrayList(queryDocumentSnapshots.toObjects(LuanVan.class));

                        luanvan.forEach((lv)->{
                            sb.append(lv.toString());
                            try {
                                FileOutputStream fileOutputStream=new FileOutputStream(file);
                                fileOutputStream.write(sb.toString().getBytes());
                                fileOutputStream.close();
                                Toast.makeText(getContext(),"Lưu thông tin luận văn vào \nDownload/luanvanText.txt",Toast.LENGTH_LONG);


                            } catch (IOException e) {
                                Log.i("Lỗi đọc ghi file",e.toString());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Lỗi xảy ra khi tải dữ liệu",Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private LogoutListener logoutListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LogoutListener) {
            logoutListener = (LogoutListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LogoutListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        logoutListener = null;
    }

    // When the button is clicked
    public void onLogoutButtonClicked() {
        if (logoutListener != null) {
            logoutListener.onLogout();
        }
    }


    private void Exit(View view) {
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().finish();
                onLogoutButtonClicked();
            }
        });

    }





}