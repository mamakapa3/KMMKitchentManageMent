package com.example.kmmkitchentmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kmmkitchentmanagement.R;

import java.util.ArrayList;

public class ClickableItemsAdapter extends BaseAdapter {
    Context context;
    ArrayList<int[]> items;

    public ClickableItemsAdapter(Context context, ArrayList<int[]> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int item[]=items.get(i);
        view= LayoutInflater.from(context).inflate(R.layout.clickable_item_layout,null);
        ImageView image=view.findViewById(R.id.image);
        TextView title=view.findViewById(R.id.itemTitle);
        title.setText(item[1]);
        image.setImageResource(item[0]);
        return view;
    }
}
