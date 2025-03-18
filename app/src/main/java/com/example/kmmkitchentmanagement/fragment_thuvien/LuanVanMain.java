package com.example.kmmkitchentmanagement.fragment_thuvien;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kmmkitchentmanagement.Model.LuanVan;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.adapter.LuanVanAdapter;
import com.example.kmmkitchentmanagement.fragmentSub.LuanVanView;
import com.example.kmmkitchentmanagement.viewmodelExtends.LuanVanVM;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class LuanVanMain extends Fragment {
    FirebaseFirestore firestore;
    ArrayList<LuanVan> luanVanList;
    ImageButton backBtn;
    LuanVanAdapter luanVanAdapter;
    SearchView searchLV;
    ListView ListLV;
    LuanVanVM luanVanVM;


    public LuanVanMain() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_luan_van_main, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseInit();
        addView(view);
        DataHandler();
        AttackData();
        eventHandler();
    }
    protected void addView(View view) {
        backBtn=view.findViewById(R.id.btnBackListLV);
        ListLV=view.findViewById(R.id.listLV);
        searchLV=view.findViewById(R.id.searchTG);
    }

    protected void FirebaseInit(){
        firestore=FirebaseFirestore.getInstance();
    }
    protected void DataHandler(){
        luanVanVM=new ViewModelProvider(this).get(LuanVanVM.class);
        luanVanList=new ArrayList<>();
        luanVanAdapter=new LuanVanAdapter(getContext(),luanVanList);
        firestore.collection("/luanvan").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    luanVanList.clear();
                    luanVanList.addAll(value.toObjects(LuanVan.class));
                    luanVanAdapter.getReflectList().clear();
                    luanVanAdapter.getReflectList().addAll(value.toObjects(LuanVan.class));
                    luanVanAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    protected void AttackData(){
        ListLV.setAdapter(luanVanAdapter);
    }
    protected void eventHandler(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        searchLV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                luanVanAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                luanVanAdapter.getFilter().filter(s);
                return false;
            }
        });
        ListLV.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("dkkj","position"+i);
                luanVanVM.setData(luanVanList.get(i));
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.luanvanMain,new LuanVanView())
                        .addToBackStack("toDetail")
                        .commit();
            }
        });

    }
}