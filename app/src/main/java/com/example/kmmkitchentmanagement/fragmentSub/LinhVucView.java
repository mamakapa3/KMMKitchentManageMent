package com.example.kmmkitchentmanagement.fragmentSub;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kmmkitchentmanagement.Model.LuanVan;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.adapter.LuanVanAdapter;
import com.example.kmmkitchentmanagement.viewmodelExtends.LinhVucVM;
import com.example.kmmkitchentmanagement.viewmodelExtends.LuanVanVM;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LinhVucView extends Fragment {
    List<LuanVan> luanVanList;
    LuanVanAdapter adapter;
    LuanVanVM luanVanVM;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    TextView linhvucName;
    ListView listView;
    LinhVucVM linhVucVM;
    String linhvuc;
    ImageButton backBtn,deleteBtn;


    public LinhVucView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_linh_vuc_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseInit();
        AddView(view);
        DataHandler();
        AttackData();
        eventHandler();
    }
    protected void FirebaseInit(){
        firestore=FirebaseFirestore.getInstance();
    }
    protected void AddView(View view) {
        linhvucName=view.findViewById(R.id.linhvucName);
        listView=view.findViewById(R.id.listview);
        backBtn=view.findViewById(R.id.backBtn);
        deleteBtn=view.findViewById(R.id.btnDeleteLinhVuc);

    }
    protected void DataHandler(){
        luanVanVM=new ViewModelProvider(this).get(LuanVanVM.class);
        linhVucVM=new ViewModelProvider(requireParentFragment()).get(LinhVucVM.class);
        linhVucVM.getData().observe(requireParentFragment().getViewLifecycleOwner(),(lv)->{
            linhvuc=lv;
        });
        luanVanList=new ArrayList<>();
        adapter=new LuanVanAdapter(getContext(),luanVanList);
        firestore.collection("/luanvan")
                .whereEqualTo("researchField",linhvuc)
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
                        }else{
                            luanVanList.clear();
                            luanVanList.addAll(value.toObjects(LuanVan.class));
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    protected void AttackData(){
        listView.setAdapter(adapter);
        linhvucName.setText(linhvuc);
    }
    protected void eventHandler(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                luanVanVM.setData(luanVanList.get(i));
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.linhvucView,new LuanVanView())
                        .addToBackStack("2luanvan")
                        .commit();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Xóa lĩnh vực")
                        .setMessage("Xóa lĩnh vực "+linhvuc+" cũng đồng thời xóa toàn bộ luận văn thuộc lĩnh vực này. Bạn có chắc chắn xóa")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Thực hiện hành động xóa lĩnh vực và xóa luạn văn tại đay
                                //Khi xóa luạn văn pahi3 đồng thời xóa toàn bộ những file thuộc luạn văn đó
                                // biến String linhvuc bên trên là tên lĩnh vực;

                                CollectionReference linhvuRef = firestore.collection("/linhvuc");
                                linhvuRef.document("/linhvuc").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                // Retrieve the array field "linhvuc"
                                                List<String> linhvucList = (List<String>) document.get("linhvuc");
                                                // Do something with the array data
                                                if (linhvucList != null) {
                                                    for (String linhvucitem : linhvucList) {
                                                        // Handle each element in the array
                                                        linhvuRef.document("/linhvuc")
                                                                .update("linhvuc",FieldValue.arrayRemove(linhvuc))
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        firestore.collection("/luanvan")
                                                                                .whereEqualTo("researchField", linhvuc)
                                                                                .get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            for (int index = 0; index < luanVanList.size(); index++) {
                                                                                                String id = luanVanList.get(index).getId();
                                                                                                DocumentReference docRef = firestore.collection("/luanvan").document(id);
                                                                                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                // Document successfully deleted.
                                                                                                                // Handle the success case here, if needed.
                                                                                                                Toast.makeText(view.getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                // Handle the error case if document deletion fails.
                                                                                                                Toast.makeText(view.getContext(), "Xóa không thành công", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });
                                                                        linhvucList.remove(linhvuc);
                                                                        // The item has been successfully removed from the "favorites" array.
                                                                        // You may update the local ArrayList (if you have one) to reflect the change.
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Handle the error case if the item removal from the "favorites" array fails.
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                                }
                        }).setNegativeButton("Hủy",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        }).show(); // You need to call show() to display the AlertDialog.
            }
        });
    }
}