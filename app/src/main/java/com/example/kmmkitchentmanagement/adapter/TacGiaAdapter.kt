package com.example.kmmkitchentmanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.kmmkitchentmanagement.Model.TacGia
import com.example.kmmkitchentmanagement.R

class TacGiaAdapter(
    private val context: Context,
    private var tacGiaList: MutableList<TacGia>
) : BaseAdapter(), Filterable {

    private var reflectList: MutableList<TacGia> = mutableListOf()

    private val filter: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val resultList = mutableListOf<TacGia>()
            if (charSequence.isNullOrEmpty()) {
                resultList.addAll(reflectList)
            } else {
                reflectList.forEach { tacGia ->
                    if (tacGia.toString().contains(charSequence.toString().trim(), ignoreCase = true)) {
                        resultList.add(tacGia)
                    }
                }
            }
            return FilterResults().apply { values = resultList }
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            tacGiaList.clear()
            filterResults?.values?.let { tacGiaList.addAll(it as List<TacGia>) }
            notifyDataSetChanged()
        }
    }

    fun getReflectList(): List<TacGia> = reflectList

    fun setReflectList(reflectList: List<TacGia>) {
        this.reflectList = reflectList.toMutableList()
    }

    override fun getCount(): Int = tacGiaList.size

    override fun getItem(position: Int): Any = tacGiaList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var holder: ViewHolder

        val convertView = view ?: LayoutInflater.from(context).inflate(R.layout.listview_tacgia, parent, false).apply {
            holder = ViewHolder().also {
                it.txtTacGia = findViewById(R.id.txtTacGia)
                it.txtEmail = findViewById(R.id.txtEmail)
                it.txtSDT = findViewById(R.id.txtSDT)
            }
            tag = holder
        }

        holder = convertView.tag as ViewHolder
        val tacGia = tacGiaList[position]
        holder.txtTacGia.text = tacGia.name
        holder.txtEmail.text = tacGia.emailAddress
        holder.txtSDT.text = tacGia.phoneNumber

        return convertView
    }

    override fun getFilter(): Filter = filter

    private class ViewHolder {
        lateinit var txtTacGia: TextView
        lateinit var txtEmail: TextView
        lateinit var txtSDT: TextView
    }
}
