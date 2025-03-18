package com.example.kmmkitchentmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.kmmkitchentmanagement.Model.TacGia;
import com.example.kmmkitchentmanagement.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TacGiaAdapter extends BaseAdapter implements Filterable {
    Context context;
    List<TacGia> tacGiaList,reflectList;
    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<TacGia> resultList=new ArrayList<>();
            if(charSequence==null||charSequence.length()==0) {
                resultList.addAll(reflectList);
            }else{
                reflectList.forEach((tacgia)->{
                    if(tacgia.toString().contains(charSequence.toString().trim().toLowerCase())){
                        resultList.add(tacgia);
                    }
                });
            }
            FilterResults results=new FilterResults();
            results.values=resultList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            tacGiaList.clear();
            tacGiaList.addAll((Collection<? extends TacGia>) filterResults.values);
            notifyDataSetChanged();
        }

    };

    public TacGiaAdapter(Context context, List<TacGia> tacGiaList) {
        this.context = context;
        this.tacGiaList = tacGiaList;
        reflectList=new ArrayList<>();
    }

    public List<TacGia> getReflectList() {
        return reflectList;
    }

    public void setReflectList(List<TacGia> reflectList) {
        this.reflectList = reflectList;
    }

    @Override
    public int getCount() {
        return tacGiaList.size();
    }

    @Override
    public Object getItem(int i) {
        return tacGiaList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view =LayoutInflater.from(context).inflate(R.layout.listview_tacgia,null);
        TextView txtTacGia=view.findViewById(R.id.txtTacGia);
        TextView txtEmail=view.findViewById(R.id.txtEmail);
        TextView txtSDT=view.findViewById(R.id.txtSDT);
        TacGia tacGia=tacGiaList.get(i);
        txtTacGia.setText(tacGia.getName());
        txtEmail.setText(tacGia.getEmailAddress());
        txtSDT.setText(tacGia.getPhoneNumber());
        return view;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}