package com.example.kmmkitchentmanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.kmmkitchentmanagement.R

class ClickableItemsAdapter(
    private val context: Context,
    private val items: ArrayList<IntArray>
) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = items[position]
        val view = LayoutInflater.from(context).inflate(R.layout.clickable_item_layout, parent, false)
        val image: ImageView = view.findViewById(R.id.image)
        val title: TextView = view.findViewById(R.id.itemTitle)

        title.text = item[1].toString()
        image.setImageResource(item[0])

        return view
    }
}
