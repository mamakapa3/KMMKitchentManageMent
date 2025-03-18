package com.example.kmmkitchentmanagement.fragment_thuvien;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kmmkitchentmanagement.Model.TacGia;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.adapter.TacGiaAdapter;
import com.example.kmmkitchentmanagement.fragmentSub.TacGiaView;
import com.example.kmmkitchentmanagement.viewmodelExtends.TacGiaVM;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TacGiaMain extends Fragment {
    TacGiaVM tacGiaVM;
    FirebaseFirestore firestore;
    List<TacGia> tacGias;
    ImageButton backBtn;
    SearchView searchTG;
    ListView ListTG;
    TacGiaAdapter ListTGAdapter;
    public TacGiaMain() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tac_gia_main, container, false);
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
    protected void addView(View view){
        ListTG=view.findViewById(R.id.ListTG);
        searchTG=view.findViewById(R.id.searchTG);
        backBtn=view.findViewById(R.id.btnBackListTG);
    }
    protected void FirebaseInit(){
        firestore=FirebaseFirestore.getInstance();
    }
    protected void DataHandler() {
        tacGiaVM=new ViewModelProvider(this).get(TacGiaVM.class);
        tacGias = new ArrayList<>();
        ListTGAdapter = new TacGiaAdapter(getContext(),tacGias);
        firestore.collection("/tacgia").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    builder.show();
                }
                else{
                    tacGias.clear();
                    tacGias.addAll(value.toObjects(TacGia.class));
                    ListTGAdapter.setReflectList(value.toObjects(TacGia.class));
                    ListTGAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    public void AttackData(){
        ListTG.setAdapter(ListTGAdapter);
    }
    protected void eventHandler(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        ListTG.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tacGiaVM.setData(tacGias.get(i));
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.tacgiaMain,new TacGiaView())
                        .addToBackStack(null)
                        .commit();
            }
        });
        searchTG.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ListTGAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ListTGAdapter.getFilter().filter(s);
                return false;
            }
        });
    }
}