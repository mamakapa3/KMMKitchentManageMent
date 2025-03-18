package com.example.kmmkitchentmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.kmmkitchentmanagement.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LinhVucAdapter extends BaseAdapter implements Filterable {
    FirebaseFirestore firestore;
    Context context;
    List<String> listchude,reflectList;
    Filter filter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<String> resultList=new ArrayList<>();
            if(charSequence==null||charSequence.length()==0){
                resultList.addAll(reflectList);
            }else {
                reflectList.forEach((chude)->{
                    if(chude.trim().toLowerCase().contains(charSequence)){
                        resultList.add(chude);
                    }
                });
            }
            FilterResults results=new FilterResults();
            results.values=resultList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            listchude.clear();
            listchude.addAll((Collection<? extends String>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public LinhVucAdapter(Context context, List<String> listchude) {
        firestore=FirebaseFirestore.getInstance();
        this.context = context;
        this.listchude = listchude;
        reflectList=new ArrayList<>();
    }

    public List<String> getReflectList() {
        return reflectList;
    }

    public void setReflectList(List<String> reflectList) {
        this.reflectList = reflectList;
    }

    @Override
    public int getCount() {
        return listchude.size();
    }

    @Override
    public Object getItem(int i) {
        return listchude.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view= LayoutInflater.from(context).inflate(R.layout.listview_linhvuc,null);
        TextView linhvuc=view.findViewById(R.id.linhvuc);
        TextView soluong=view.findViewById(R.id.soluong);
        Button deleteBtn=view.findViewById(R.id.deleteHistory);
        String chude= listchude.get(i);
        linhvuc.setText(chude);
        firestore.collection("/luanvan").whereEqualTo("researchField",chude)
                .count().get(AggregateSource.SERVER).addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onSuccess(AggregateQuerySnapshot snapshot) {
                        soluong.setText(String.valueOf(snapshot.getCount()));
                    }
                });

        return view;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
