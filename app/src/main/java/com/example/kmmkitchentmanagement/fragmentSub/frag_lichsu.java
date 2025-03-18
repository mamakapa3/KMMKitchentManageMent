package com.example.kmmkitchentmanagement.fragmentSub;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.kmmkitchentmanagement.Model.historyObj;
import com.example.kmmkitchentmanagement.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class frag_lichsu extends Fragment {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = firestore.collection("/lichsu");
    DocumentReference docRef = collectionReference.document();
    ImageButton backBtn,deleteHistoryBtn;
    String text;
    TextView textview;
    List<historyObj> historyList;
    public frag_lichsu() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_frag_lichsu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addView(view);
        DataHandler();
        eventHandler();
    }

    protected void FirebaseInit(){
        firestore=FirebaseFirestore.getInstance();
    }
    protected void addView(View view) {
        backBtn=view.findViewById(R.id.backBtn);
        deleteHistoryBtn=view.findViewById(R.id.deleteHistory);
        textview = view.findViewById(R.id.lichsuTextView);
    }

    protected void DataHandler(){
        historyList = new ArrayList<>();
        collectionReference.orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        historyList=value.toObjects(historyObj.class);
                        text="";
                        historyList.forEach(history -> {
                            text+=history;
                        });
                        textview.setText(text);
                    }
                });

    }
    protected void eventHandler(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        deleteHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Xóa toàn bộ lịch sử")
                        .setMessage("Thao tác này sẽ xóa toàn bộ lịch sử trong cơ sở dữ liệu ?")
                        .setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firestore.collection("/lichsu").get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                                    snapshot.getReference().delete();
                                                }
                                                getParentFragmentManager().popBackStack();
                                            }
                                        });
                            }
                        }).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

            }
        });
    }
}