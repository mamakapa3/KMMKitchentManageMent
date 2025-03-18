package com.example.kmmkitchentmanagement.fragmentSub;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kmmkitchentmanagement.Model.LuanVan;
import com.example.kmmkitchentmanagement.Model.TacGia;
import com.example.kmmkitchentmanagement.Model.historyObj;
import com.example.kmmkitchentmanagement.R;
//import com.example.kmmkitchentmanagement.fragment_themND.ChinhLuanVan;
import com.example.kmmkitchentmanagement.viewmodelExtends.LuanVanVM;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LuanVanView extends Fragment {
    StorageReference storageReference;
    ImageButton backBtn;
    Button btnLuu, btnEdit, btnXoa,btnXemsau;
    TextView title, dateAdd, linhVuc, tacGia, datePush, moTa, trichDan;
    ImageView imageView;
    LuanVanVM luanVanVM;
    LuanVan luanVan;
    TacGia tacGiaClass;
    File file=new File(Environment.getExternalStoragePublicDirectory("//").getAbsolutePath()+"/QLLuanVan/");

    public LuanVanView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_luanvan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseInit();
        DataHandler();
        addView(view);
        eventHandler(view);
        AttackData();

        ThemDanhDau(view);
        Edit(view);
        DialogDelete(view);
    }
    protected void DataHandler(){
        luanVanVM=new ViewModelProvider(requireParentFragment()).get(LuanVanVM.class);
        luanVan=new LuanVan();
        luanVanVM.getData().observe(requireParentFragment().getViewLifecycleOwner(),(luanvan)->{
            luanVan=luanvan;
        });
    }
    protected void FirebaseInit(){
        storageReference= FirebaseStorage.getInstance().getReference("/luanvan/image");
    }
    protected void AttackData(){
        btnXemsau.setText(luanVan.isMarked()?"Xóa khỏi danh sách xem sau":"Lưu vào danh sách xem sau");
        title.setText(luanVan.getTitle());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateAdd.setText(dateFormat.format(luanVan.getPublishedDate()));
        linhVuc.setText(luanVan.getResearchField());

        tacGiaClass = new TacGia();
        tacGia.setText(tacGiaClass.getName());
        moTa.setText(luanVan.getDescription());
        trichDan.setText(luanVan.getCitation());
        Glide.with(getContext())
                .load(storageReference.child("/"+luanVan.getId()+".jpg"))
                .error(R.drawable.fail_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }
    protected void addView(View view) {
        btnXemsau=view.findViewById(R.id.xemsau);
        backBtn = view.findViewById(R.id.btnbacklvview);
        btnLuu = view.findViewById(R.id.btnsaveLVview);
        btnEdit = view.findViewById(R.id.btnSuaLVview);
        btnXoa = view.findViewById(R.id.btnDeleteLVview);

        title = view.findViewById(R.id.textTenLVview);
        dateAdd = view.findViewById(R.id.textngayupLVview);
        linhVuc = view.findViewById(R.id.textLinhVucLVview);
        tacGia = view.findViewById(R.id.texttacgiaLVview);
        datePush = view.findViewById(R.id.textngayxbLVview);
        moTa = view.findViewById(R.id.textmotaLVview);
        trichDan = view.findViewById(R.id.texttrichdanLVview);

        imageView = view.findViewById(R.id.imageLVview);
    }


    protected void eventHandler(View view) {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        btnXemsau.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean state=luanVan.isMarked()?false:true;
                luanVan.setMarked(state);
                btnXemsau.setText(state?"Xóa khỏi danh sách xem sau":"Lưu vào danh sách xem sau");
                FirebaseFirestore.getInstance().collection("/luanvan")
                        .document("/"+luanVan.getId()).update("marked",state);
            }
        });

    }

    public void ThemDanhDau(View view){
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1023);
                }
                else {

                    FirebaseStorage.getInstance().getReference("/luanvan/document/"+luanVan.getId()+".docx")
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadFile(getContext(), luanVan.getTitle(), ".docx", DIRECTORY_DOWNLOADS, uri);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                                    builder.setTitle("Lỗi tải xuống");
                                    builder.setMessage("Lưu trữ số của luận văn này không tồn tại trên hệ thống");
                                    builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.setIcon(R.drawable.error);
                                    builder.show();
                                }
                            });
                }


            }
        });
    }
    private void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, Uri uri) {
        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(destinationDirectory, fileName + fileExtension);
        downloadmanager.enqueue(request);
    }
    public void Edit(View view){
        btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.luanvanview,new ChinhLuanVan())
                        .addToBackStack("ToLuanVans")
                        .commit();
            }
        });
    }
    public void DialogDelete(View view){
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Thông báo").setMessage("Bạn thật sự muốn xóa luận văn này?")
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                historyObj history=new historyObj(new Date(),"Xóa 1 luận văn '"+luanVan.getTitle()+"\'");
                                FirebaseFirestore.getInstance().collection("/lichsu").add(history);
                                FirebaseFirestore.getInstance().collection("/luanvan")
                                                .document("/"+luanVan.getId()).delete();
                                getParentFragmentManager().popBackStack();
                            }
                        }).show();
            }
        });
    }

}
