package com.example.kmmkitchentmanagement.fragment_thuvien;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.adapter.LinhVucAdapter;
import com.example.kmmkitchentmanagement.fragmentSub.LinhVucView;
import com.example.kmmkitchentmanagement.viewmodelExtends.LinhVucVM;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class LinhVucMain extends Fragment {
    FirebaseFirestore firestore;
    Button backBtn;
    ListView listview;
    List<String> linhvuc;
    LinhVucAdapter listAdapter;
    SearchView searchView;
    LinhVucVM linhVucVM;

    public LinhVucMain() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_linh_vuc_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseInit();
        addView(view);
        DataHandler();
        attackData();
        eventHandler();
    }
    protected void addView(View view) {
        backBtn=view.findViewById(R.id.btnBackListDD);
        listview=view.findViewById(R.id.listview);
        searchView=view.findViewById(R.id.searchview);
    }
    protected void FirebaseInit(){
        firestore=FirebaseFirestore.getInstance();
    }
    protected void DataHandler(){
        linhVucVM=new ViewModelProvider(this).get(LinhVucVM.class);
        linhvuc=new ArrayList<String>();
        listAdapter=new LinhVucAdapter(getContext(),linhvuc);
        firestore.collection("/linhvuc").document("/linhvuc")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){
                            Log.i("linhvuc_loi", String.valueOf(linhvuc.size()));
                        }
                        else{
                            if((List<String>)value.get("linhvuc")!=null){
                                linhvuc.clear();
                                linhvuc.addAll((List<String>)value.get("linhvuc"));
                                listAdapter.getReflectList().clear();
                                listAdapter.getReflectList().addAll((List<String>)value.get("linhvuc"));
                                listAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

    }
    protected void attackData(){
        listview.setAdapter(listAdapter);
    }
    protected void eventHandler(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                linhVucVM.setData(linhvuc.get(i));
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.linhvucMain,new LinhVucView())
                        .addToBackStack("linhvuc")
                        .commit();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                listAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                listAdapter.getFilter().filter(s);
                return false;
            }
        });
    }
}