package com.example.kmmkitchentmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kmmkitchentmanagement.Model.LuanVan;
import com.example.kmmkitchentmanagement.Model.TacGia;
import com.example.kmmkitchentmanagement.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LuanVanAdapter extends BaseAdapter implements Filterable {
    FirebaseStorage firebaseStorage;
    Context context ;

    List<LuanVan> list,reflectList;
    TacGia tacGia;

    public LuanVanAdapter(Context context, List<LuanVan> list ) {
        firebaseStorage=FirebaseStorage.getInstance();
        this.context =  context;
        this.list = list;
        this.reflectList=new ArrayList<>(list);
    }

    public void setReflectList(List<LuanVan> reflectList) {
        this.reflectList = reflectList;
    }

    public List<LuanVan> getList() {
        return list;
    }

    public List<LuanVan> getReflectList() {
        return reflectList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    Filter filter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<LuanVan> result = new ArrayList<LuanVan>();
            if(charSequence==null||charSequence.length()==0) {
                result.addAll(reflectList);
            }else{
                reflectList.forEach(luanVan -> {
                    if(luanVan.getTitle().trim().toLowerCase().contains(charSequence.toString().trim().toLowerCase())) {
                        result.add(luanVan);
                    }
                } );
            }
            FilterResults results=new FilterResults();
            results.values=result;
            return results;
        }
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((Collection<? extends LuanVan>) filterResults.values);
            notifyDataSetChanged();
        }

    };
    @Override
    public Filter getFilter() {
        return filter;
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.listview_noi_dung_luan_van, null);

        TextView textLVtieude = view.findViewById(R.id.textLVtieude);
        TextView txtTacGiaLv = view.findViewById(R.id.txtTacGiaLV);
        TextView txtChuDeLV = view.findViewById(R.id.txtChuDeLV);
        TextView txtTinhTrangLV =  view.findViewById(R.id.txtTinhTrangLV);
        ImageView imageNDLV =view.findViewById(R.id.imageNDLV);

        LuanVan luanVan =list.get(position);
        textLVtieude.setText(luanVan.getTitle());

        txtTacGiaLv.setText(luanVan.getTacGias());
        txtChuDeLV.setText(luanVan.getTitle());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (luanVan.isPublished() == true) {
            txtTinhTrangLV.setText("Đã xuất bản");
        } else {
            txtTinhTrangLV.setText("Chưa xuất bản");
        }
        StorageReference storageReference= firebaseStorage.getReference("/luanvan/image/"+luanVan.getId()+".jpg");
        Glide.with(context)
                .load(storageReference)
                .error(R.drawable.fail_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageNDLV);
        return view;
    }
}
