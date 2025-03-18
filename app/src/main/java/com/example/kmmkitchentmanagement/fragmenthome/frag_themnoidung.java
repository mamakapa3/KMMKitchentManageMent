package com.example.kmmkitchentmanagement.fragmenthome;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LifecycleOwner;

import com.example.kmmkitchentmanagement.Model.LuanVan;
import com.example.kmmkitchentmanagement.Model.historyObj;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.customdialog.UploadTaskDialog;
import com.example.kmmkitchentmanagement.fragment_themND.ThemTacGia;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class frag_themnoidung extends Fragment {
    FirebaseFirestore firestore;
    CollectionReference collectionReference;
    FirebaseStorage storage;
    List<String> linhvuclist;
    StorageReference storageReference;
    Uri luanVanThumbnail,luanvandocx;
    RadioGroup radioXB;
    LinearLayout viewDatePushed;
    ImageButton mImageButton;
    ActivityResultLauncher<Intent> activityResultLauncher;
    EditText mEditTieuDe, mEditChuThich, mEditTrichDan, mEditDatePushed, mEditTacGia, mEditLinhVuc;
    Button mAddTacGia, mAddLinhVuc, mAddLV,addFile;
    DatePickerDialog.OnDateSetListener setListener;

    String tieuDe, linhVuc, moTa, trichDan, tacGia;
    boolean isPushed = true;
    Date datePushed;

    public frag_themnoidung() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_themluanvan, container, false);
        FirebaseInit();
        AddView(view);
        DataHandler();
        OnRadioClicked(view);
        AddImage(view);
        ChooseDate(view);

        ClickForAddTacGia(view);
        ClickForAddLinhVuc(view);
        ActivityResult(view);
        AddLuanVan(view);
        return view;
    }
    protected void DataHandler(){
        linhvuclist=new ArrayList<>();
        firestore.collection("/linhvuc").document("/linhvuc")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Xử lý lỗi nếu cần
                            return;
                        }

                        if (value != null && value.exists()) {
                            List<String> linhvuc = (List<String>) value.get("linhvuc");
                            if (linhvuc != null) {
                                linhvuclist.clear();
                                linhvuclist.addAll(linhvuc);
                            }
                        }
                    }
                });
    }
    private void AddView(View view) {
        radioXB = (RadioGroup) view.findViewById(R.id.radioXB);
        viewDatePushed = view.findViewById(R.id.viewTGLV);
        mImageButton = view.findViewById(R.id.btnAvaLV);
        mEditTieuDe = view.findViewById(R.id.editTitleLV);
        mEditChuThich = view.findViewById(R.id.editMoTaTG);
        mEditTrichDan = view.findViewById(R.id.EditTrichDanLV);
        mEditDatePushed = view.findViewById(R.id.editTGLV);
        mAddTacGia = view.findViewById(R.id.btnAddTG);
        mAddLinhVuc = view.findViewById(R.id.btnAddLinhVuc);
        mEditTacGia = view.findViewById(R.id.editTacGiaLV);
        mEditLinhVuc = view.findViewById(R.id.editLinhVucLV);
        mAddLV = view.findViewById(R.id.btnAddLV);
        addFile=view.findViewById(R.id.documentUpload);
    }
    public void OnRadioClicked(View view) {
        radioXB.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.btnRadioXBTrue) {
                    viewDatePushed.setVisibility(View.GONE);
                }else if(checkedId == R.id.btnRadioXBFalse){
                    viewDatePushed.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    public void AddImage(View view){
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                activityResultLauncher.launch(intent);
            }
        });
    }

    public void ActivityResult(View view){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    Uri temp = data.getData();
                    if(temp.toString().toLowerCase().contains("image")){
                        luanVanThumbnail = temp;
                        mImageButton.setImageURI(luanVanThumbnail);
                    }else{
                        luanvandocx = temp;
                        addFile.setText(luanvandocx.toString());
                    }
                }
            }
        });
    }


    public void ChooseDate(View view){
        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        mEditDatePushed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        view.getContext(),android.R.style.Theme_Holo_Dialog_MinWidth,setListener, year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = day+"/"+month+"/"+year;
                mEditDatePushed.setText(date);
            }
        };
    }

    public void ClickForAddTacGia(View view){
        mAddTacGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerHome,new ThemTacGia())
                        .addToBackStack("ToLuanVans")
                        .commit();
            }
        });
        getParentFragmentManager().setFragmentResultListener("addTacGia", (LifecycleOwner) view.getContext(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String tempTG = result.getString("emailAddress");
                mEditTacGia.setText(tempTG);
            }
        });
    }
    public void ClickForAddLinhVuc(View view) {
        mAddLinhVuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    View mView = getLayoutInflater().inflate(R.layout.fragment_themchude, null);
                    final EditText mEditText = (EditText) mView.findViewById(R.id.editTextLinhVuc);
                    builder.setView(mView).
                            setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = (EditText) mView.findViewById(R.id.editTextLinhVuc);
                                    String tempLuanVan = editText.getText().toString();

                                    //TODO: Lưu dữ liệu vào database

                                    Bundle bundle = new Bundle();
                                    bundle.putString("data_lv", tempLuanVan);
                                    getParentFragmentManager().setFragmentResult("Linh_vuc", bundle);
                                    getParentFragmentManager().popBackStack();
                                }
                            });

                    builder.show();
                }
        });
        getParentFragmentManager().setFragmentResultListener("Linh_vuc", (LifecycleOwner) view.getContext(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String tempLV = result.getString("data_lv");
                mEditLinhVuc.setText(tempLV);
            }
        });
    }


    public void AddLuanVan(View view){
        mAddLV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                String generatedID=collectionReference.document().getId();
                tieuDe = mEditTieuDe.getText().toString();
                linhVuc = mEditLinhVuc.getText().toString().trim().toLowerCase();
                moTa = mEditChuThich.getText().toString();
                trichDan = mEditTrichDan.getText().toString();
                tacGia = mEditTacGia.getText().toString();

                String strDate = mEditDatePushed.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try{
                    datePushed = sdf.parse(strDate);
                }catch(ParseException e){
                }

                if(tieuDe.isEmpty() || tacGia.isEmpty() || trichDan.isEmpty() ) {
                    Toast.makeText(view.getContext(), "Kiểm tra lại dữ liệu", Toast.LENGTH_SHORT).show();;
                }else{
                    if(linhVuc.isEmpty()){
                        linhVuc = "Không lĩnh vực";
                    }else{
                        if(!linhvuclist.contains(linhVuc)){
                            linhvuclist.add(linhVuc);
                            firestore.collection("/linhvuc").document("/linhvuc").update("linhvuc",linhvuclist);
                        }
                    }
                    if(moTa.isEmpty()){
                        moTa = "Không mô tả";
                    }

                    LuanVan tempLuanVan = new LuanVan();
                    //Id tam thoi
                    tempLuanVan.setId(generatedID);
                    //
                    tempLuanVan.setTitle(tieuDe);
                    tempLuanVan.setTacGias(tacGia);
                    tempLuanVan.setResearchField(linhVuc);
                    tempLuanVan.setDescription(moTa);
                    tempLuanVan.setCitation(trichDan);
                    tempLuanVan.setPublished(isPushed);
                    tempLuanVan.setAddTime(new Date());
                    if(!isPushed){
                        tempLuanVan.setPublishedDate(datePushed);
                    }
                    Log.i("Luan van",tempLuanVan.toString());
                    collectionReference.document("/"+generatedID).set(tempLuanVan)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    ArrayList<UploadTask> uploadTasks=new ArrayList<>();
                                    if(luanVanThumbnail!=null) uploadTasks.add(storageReference.child("/image/"+tempLuanVan.getId()+".jpg").putFile(luanVanThumbnail));
                                    if(luanvandocx!=null) uploadTasks.add(storageReference.child("/document/"+tempLuanVan.getId()+".docx").putFile(luanvandocx));
                                    if(uploadTasks.size()>0){
                                        UploadTaskDialog dialog=new UploadTaskDialog(uploadTasks);
                                        dialog.show(getParentFragmentManager(),"uploading");
                                    }else{
                                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                        builder.setIcon(R.drawable.check).setMessage("Thêm luận văn mới thành công")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                }).show();
                                    }
                                    historyObj history=new historyObj(new Date(),"Thêm 1 luận văn mới '"+tempLuanVan.getTitle()+"\'");
                                    firestore.collection("/lichsu").add(history);
                                }
                            });

                }
            }
        });
    }
    protected void FirebaseInit(){
        firestore=FirebaseFirestore.getInstance();
        collectionReference= firestore.collection("/luanvan");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference("/luanvan");
    }

};