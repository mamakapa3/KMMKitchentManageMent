package com.example.kmmkitchentmanagement.fragmenthome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.kmmkitchentmanagement.AppUtils.Utils;
import com.example.kmmkitchentmanagement.Model.LuanVan;
import com.example.kmmkitchentmanagement.Model.NguoiDung;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.adapter.ClickableItemsAdapter;
import com.example.kmmkitchentmanagement.adapter.LuanVanAdapter;
import com.example.kmmkitchentmanagement.fragmentSub.LuanVanView;
import com.example.kmmkitchentmanagement.fragment_thuvien.LinhVucMain;
import com.example.kmmkitchentmanagement.fragment_thuvien.LuanVanMain;
import com.example.kmmkitchentmanagement.fragment_thuvien.TacGiaMain;
import com.example.kmmkitchentmanagement.fragment_thuvien.XemSauMain;
import com.example.kmmkitchentmanagement.viewmodelExtends.LuanVanVM;
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class frag_ThuVien extends Fragment {
    FirebaseFirestore firestore;
    LuanVanVM luanVanVM;
    ListView catalogView,listGanDay;
    LuanVanAdapter luanVanAdapter;
    ClickableItemsAdapter catalogAdapter;
    List<LuanVan> listRecent;

    NguoiDung user = new NguoiDung();
    TextView textHello;

    private UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    public frag_ThuVien() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thu_vien, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        FirebaseInit();
        addView(view);

        getUserData();

        DataHandler();
        AttackData();
        attackView();
        eventHandler();
    }
    protected void FirebaseInit(){
        firestore=FirebaseFirestore.getInstance();
    }
    protected void DataHandler(){
        luanVanVM=new ViewModelProvider(this).get(LuanVanVM.class);
        listRecent=new ArrayList<>();
        luanVanAdapter=new LuanVanAdapter(getContext(),listRecent);
        firestore.collection("/luanvan").orderBy("addTime", Query.Direction.DESCENDING).limit(5)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null) Toast.makeText(requireActivity(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        else {
                            listRecent.clear();
                            listRecent.addAll(value.toObjects(LuanVan.class));
                            luanVanAdapter.notifyDataSetChanged();
                            Utils.setListViewHeightBasedOnChildren(listGanDay);
                        }
                    }
                });
    }
    protected void AttackData(){
        listGanDay.setAdapter(luanVanAdapter);

    }
    protected void addView(View view){
        catalogView=view.findViewById(R.id.listThuVien);
        listGanDay=view.findViewById(R.id.listGanDay);

        textHello=view.findViewById(R.id.textHello);
    }
    protected void attackView(){
        int[] LuanVanMain={R.drawable.ic_luan_van,R.string.LuanVanList};
        int[] TacGiaMain={R.drawable.ic_tac_gia,R.string.TacGia};
        int[] ChuDeMain={R.drawable.ic_chu_de,R.string.LinhVuc};
        int[] DaLuu={R.drawable.ic_luu,R.string.Saved};
        ArrayList<int[]> catalogs=new ArrayList<>();
        catalogs.add(LuanVanMain);catalogs.add(TacGiaMain);catalogs.add(ChuDeMain);catalogs.add(DaLuu);
        catalogAdapter=new ClickableItemsAdapter(getContext(),catalogs);
        catalogView.setAdapter(catalogAdapter);
        Utils.setListViewHeightBasedOnChildren(catalogView);
    }
    protected void eventHandler(){
        catalogView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    getChildFragmentManager()
                            .beginTransaction()
                            .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.thuvienView,new LuanVanMain())
                            .addToBackStack("toLuanVanMains")
                            .commit();
                }
                else if(i==1){
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.thuvienView,new TacGiaMain())
                            .addToBackStack("toTacgiaMains")
                            .commit();
                }
                else if(i==2){
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.thuvienView,new LinhVucMain())
                            .addToBackStack("toLinhVucMain")
                            .commit();
                }else if(i==3){
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.thuvienView,new XemSauMain())
                            .addToBackStack("toXemSauMain")
                            .commit();
                }
            }
        });
        listGanDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                luanVanVM.setData(listRecent.get(i));
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.thuvienView,new LuanVanView())
                        .addToBackStack("kjd")
                        .commit();
            }
        });
    }

    public void getUserData() {
        // Sử dụng đường dẫn chính xác
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                textHello.setText("Xin chào " + user.getName());
            }
        });
    }


}