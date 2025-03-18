package com.example.kmmkitchentmanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.kmmkitchentmanagement.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.AggregateQuerySnapshot
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore

class LinhVucAdapter(
    private val context: Context,
    private var listchude: List<String>
) : BaseAdapter(), Filterable {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var reflectList: List<String> = ArrayList()

    private val filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val resultList = mutableListOf<String>()
            if (charSequence.isNullOrEmpty()) {
                resultList.addAll(reflectList)
            } else {
                reflectList.forEach { chude ->
                    if (chude.trim().toLowerCase().contains(charSequence.toString())) {
                        resultList.add(chude)
                    }
                }
            }
            return FilterResults().apply {
                values = resultList
            }
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            listchude = filterResults?.values as List<String>
            notifyDataSetChanged()
        }
    }

    fun getReflectList(): List<String> {
        return reflectList
    }

    fun setReflectList(reflectList: List<String>) {
        this.reflectList = reflectList
    }

    override fun getCount(): Int {
        return listchude.size
    }

    override fun getItem(position: Int): Any {
        return listchude[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.listview_linhvuc, parent, false)
        val linhvuc: TextView = view.findViewById(R.id.linhvuc)
        val soluong: TextView = view.findViewById(R.id.soluong)
        val deleteBtn: Button = view.findViewById(R.id.deleteHistory)

        val chude = listchude[position]
        linhvuc.text = chude

        firestore.collection("/luanvan").whereEqualTo("researchField", chude)
            .count().get(AggregateSource.SERVER).addOnSuccessListener { snapshot ->
                soluong.text = snapshot.count.toString()
            }

        return view
    }

    override fun getFilter(): Filter {
        return filter
    }
}
