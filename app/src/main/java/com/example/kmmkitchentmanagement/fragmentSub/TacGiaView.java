package com.example.kmmkitchentmanagement.fragmentSub;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kmmkitchentmanagement.Model.LuanVan;
import com.example.kmmkitchentmanagement.Model.TacGia;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.adapter.LuanVanAdapter;
import com.example.kmmkitchentmanagement.viewmodelExtends.LuanVanVM;
import com.example.kmmkitchentmanagement.viewmodelExtends.TacGiaVM;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class TacGiaView extends Fragment {
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ArrayList<LuanVan> luanVans;
    TacGia tacgia;
    LuanVanAdapter luanVanAdapter;
    TacGiaVM tacGiaVM;
    LuanVanVM luanVanVM;
    TextView txtName, txtPhone, txtEmail;
    ImageButton btnBack;
    Button btnEdit, btnDelete,callBtn,sendEmailBtn;

    ListView luanvanList;


    public TacGiaView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tac_gia_view, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseInit();
        addView(view);
        DataHandler();
        AttackData();
        Back(view);
        CallPhone(view);
        SendEmail(view);
        Edit(view);
        Delete(view);
        eventHandler();
    }


    public void addView(View view){
        btnBack = view.findViewById(R.id.btnbackTGview);
        btnEdit = view.findViewById(R.id.btnSuaTGview);
        btnDelete = view.findViewById(R.id.btnDeleteTGview);

        txtName =  view.findViewById(R.id.txtTacGiaview);
        txtPhone = view.findViewById(R.id.txtSDTview);
        txtEmail = view.findViewById(R.id.txtEmailview);
        luanvanList=view.findViewById(R.id.luanvanList);

        callBtn=view.findViewById(R.id.callBtn);
        sendEmailBtn=view.findViewById(R.id.sendEmailBtn);
    }


    public void Back(View view){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    public void CallPhone(View view){
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1027);
                }else{
                    String dial="tel:"+txtPhone.getText().toString().trim();
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                }
            }
        });
    }

    public void SendEmail(View view){
        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL,txtEmail.getText().toString());
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent,"Gửi mail bằng"));
            }
        });
    }
    
    public void Delete(View view){
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Thông báo").setMessage("Xoá tác giả này đồng nghĩa những luận văn liên quan sẽ bị xóa, bạn muốn tiếp tục?")
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){

                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                luanVans.forEach((luanVan -> {
                                    storage.getReference("/luanvan/image/"+luanVan.getId()+".jpg").delete();
                                    storage.getReference("/luanvan/document/"+luanVan.getId()+".docx").delete();
                                    firestore.collection("/luanvan").document("/" + luanVan.getId()).delete();

                                }));
                                firestore.collection("/tacgia").document("/"+tacgia.getEmailAddress()).delete();
                                getParentFragmentManager().popBackStack();
                            }
                        }).show();
            }
        });
    }

    public void Edit(View view) {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_tac_gia, null);
                final EditText mEditName = (EditText) mView.findViewById(R.id.editNameEditTG);
                final EditText mEditPhone = (EditText) mView.findViewById(R.id.editPhoneEditTG);
                final EditText mEditEmail = (EditText) mView.findViewById(R.id.editEmailEditTG);
                mEditName.setText(tacgia.getName());
                mEditPhone.setText(tacgia.getPhoneNumber());
                mEditEmail.setText(tacgia.getEmailAddress());
                builder.setView(mView).setTitle("Chỉnh sửa thông tin")
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TacGia temp=new TacGia(mEditName.getText().toString(),mEditPhone.getText().toString(),mEditEmail.getText().toString());
                                luanVans.forEach((luanVan -> {
                                    luanVan.setTacGias(temp.getEmailAddress());
                                    Log.i("luanVans", luanVan.getTacGias());
                                    firestore.collection("/luanvan").document("/" + luanVan.getId()).update("tacGias", temp.getEmailAddress());
                                }));
                                firestore.collection("/tacgia").document("/"+tacgia.getEmailAddress()).delete();
                                firestore.collection("/tacgia").document("/"+temp.getEmailAddress()).set(temp);

                                txtName.setText(mEditName.getText().toString());
                                txtEmail.setText(mEditEmail.getText().toString());
                                txtPhone.setText(mEditPhone.getText().toString());
                            }
                        }).show();
            }
        });
        //thay đổi tt sau khi ấn nếu cần
    }
    protected void FirebaseInit(){
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
    }
    protected void DataHandler(){
        tacGiaVM=new ViewModelProvider(requireParentFragment()).get(TacGiaVM.class);
        tacgia=new TacGia();
        tacGiaVM.getData().observe(requireParentFragment().getViewLifecycleOwner(),(tg)->tacgia=tg);
        luanVanVM=new ViewModelProvider(this).get(LuanVanVM.class);
        luanVans=new ArrayList<>();
        luanVanAdapter=new LuanVanAdapter(getContext(),luanVans);

        firestore.collection("/luanvan").whereEqualTo("tacGias",tacgia.getEmailAddress())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                            builder.setTitle("Lỗi");
                            builder.setMessage("Tải xuống dữ liệu gặp lỗi");
                            builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getParentFragmentManager().popBackStack();
                                }
                            });
                        }
                        else{
                            luanVans.clear();
                            luanVans.addAll(value.toObjects(LuanVan.class));
                            luanVanAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    protected void AttackData(){
        luanvanList.setAdapter(luanVanAdapter);
        txtName.setText(tacgia.getName());
        txtEmail.setText(tacgia.getEmailAddress());
        txtPhone.setText(tacgia.getPhoneNumber());
    }
    protected void eventHandler(){
        luanvanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                luanVanVM.setData(luanVans.get(i));
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.tacgiaview,new LuanVanView())
                        .addToBackStack("toDetail")
                        .commit();
            }
        });
    }
}