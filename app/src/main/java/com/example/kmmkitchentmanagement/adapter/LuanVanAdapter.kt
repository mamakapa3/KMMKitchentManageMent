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
import com.example.kmmkitchentmanagement.Model.LuanVan
import com.example.kmmkitchentmanagement.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.DateFormat
import java.text.SimpleDateFormat

class LuanVanAdapter(
    private val context: Context,
    private var list: List<LuanVan>
) : BaseAdapter(), Filterable {

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var reflectList: List<LuanVan> = ArrayList(list)

    fun setReflectList(reflectList: List<LuanVan>) {
        this.reflectList = reflectList
    }

    fun getList(): List<LuanVan> {
        return list
    }

    fun getReflectList(): List<LuanVan> {
        return reflectList
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private val filter: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val result = mutableListOf<LuanVan>()
            if (charSequence.isNullOrEmpty()) {
                result.addAll(reflectList)
            } else {
                reflectList.forEach { luanVan ->
                    if (luanVan.title.trim().toLowerCase().contains(charSequence.toString().trim().toLowerCase())) {
                        result.add(luanVan)
                    }
                }
            }
            return FilterResults().apply {
                values = result
            }
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            list = filterResults?.values as List<LuanVan>
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter {
        return filter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.listview_noi_dung_luan_van, parent, false)

        val textLVtieude: TextView = view.findViewById(R.id.textLVtieude)
        val txtTacGiaLv: TextView = view.findViewById(R.id.txtTacGiaLV)
        val txtChuDeLV: TextView = view.findViewById(R.id.txtChuDeLV)
        val txtTinhTrangLV: TextView = view.findViewById(R.id.txtTinhTrangLV)
        val imageNDLV: ImageView = view.findViewById(R.id.imageNDLV)

        val luanVan = list[position]
        textLVtieude.text = luanVan.title
        txtTacGiaLv.text = luanVan.tacGias
        txtChuDeLV.text = luanVan.title
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")

        txtTinhTrangLV.text = if (luanVan.isPublished) "Đã xuất bản" else "Chưa xuất bản"

        val storageReference: StorageReference = firebaseStorage.getReference("/luanvan/image/${luanVan.id}.jpg")
        Glide.with(context)
            .load(storageReference)
            .error(R.drawable.fail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageNDLV)

        return view
    }
}
