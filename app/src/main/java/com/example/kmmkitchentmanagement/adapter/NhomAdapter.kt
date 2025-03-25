package com.example.kmmkitchentmanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class NhomAdapter(private val context: Context, private var list: MutableList<Nhom>) : BaseAdapter(), Filterable {
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    var reflectList: MutableList<Nhom> = ArrayList(list)

    override fun getCount(): Int = list.size
    override fun getItem(i: Int): Any = list[i]
    override fun getItemId(i: Int): Long = i.toLong()

    private val filter: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val result = mutableListOf<Nhom>()
            if (charSequence.isNullOrEmpty()) {
                result.addAll(reflectList)
            } else {
                reflectList.forEach { Nhom ->
                    if (Nhom.title.trim().lowercase(Locale.getDefault()).contains(charSequence.toString().trim().lowercase(Locale.getDefault()))) {
                        result.add(Nhom)
                    }
                }
            }
            return FilterResults().apply { values = result }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            list.clear()
            list.addAll(filterResults.values as MutableList<Nhom>)
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter = filter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_noi_dung_nhom_main, parent, false)

        val textLVtieude: TextView = view.findViewById(R.id.textLVtieude)
        val imageNDLV: ImageView = view.findViewById(R.id.imageNDLV)

        val Nhom = list[position]
        textLVtieude.text = Nhom.title

        val storageReference: StorageReference = firebaseStorage.getReference("/Nhom/image/${Nhom.id}.jpg")
        Glide.with(context)
            .load(storageReference)
            .error(R.drawable.fail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageNDLV)
        return view
    }
}