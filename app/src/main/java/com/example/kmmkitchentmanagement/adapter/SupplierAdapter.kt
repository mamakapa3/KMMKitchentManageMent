package com.example.kmmkitchentmanagement.adapter

import android.content.Context
import android.os.Environment
import android.util.Log
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
import com.example.kmmkitchentmanagement.Model.Supplier
import com.example.kmmkitchentmanagement.R
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*

class SupplierAdapter(private val context: Context, private var list: MutableList<Supplier>) : BaseAdapter(), Filterable {
    private var reflectList: MutableList<Supplier> = ArrayList(list)

    override fun getCount(): Int = list.size
    override fun getItem(i: Int): Any = list[i]
    override fun getItemId(i: Int): Long = i.toLong()

    private val filter: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val result = mutableListOf<Supplier>()
            if (charSequence.isNullOrEmpty()) {
                result.addAll(reflectList)
            } else {
                reflectList.forEach { supplier ->
                    if (supplier.name.trim().lowercase(Locale.getDefault()).contains(charSequence.toString().trim().lowercase(Locale.getDefault()))) {
                        result.add(supplier)
                    }
                }
            }
            return FilterResults().apply { values = result }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            list.clear()
            (filterResults.values as? MutableList<Supplier>)?.let {
                list.addAll(it)
            }
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter = filter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_noi_dung_supplier, parent, false)

        val SupplierName: TextView = view.findViewById(R.id.textSupplierName)
        val imageSupplier: ImageView = view.findViewById(R.id.imageSupplier)
        val SupplierLocation :TextView = view.findViewById(R.id.txtLocation)
        val SupplierItemsType:TextView = view.findViewById(R.id.txtItemsType)
        val SupplierStatus:TextView = view.findViewById(R.id.txtStatus)
        val supplier = list[position]
        SupplierName.text = supplier.name
        SupplierLocation.text = supplier.location
        SupplierItemsType.text = supplier.itemType
        if(supplier.status == true){
            SupplierStatus.text ="Còn hàng"
        }else {
            SupplierStatus.text ="Hết Hàng"
        }

        // 📂 Đường dẫn ảnh của nhóm
        val imagePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "YourAppImages/supplier_${supplier.id}.jpg"
        )

        // 🛠 Kiểm tra xem file có tồn tại không
        if (imagePath.exists()) {
            Log.d("ImageCheck", "✅ Ảnh tồn tại: ${imagePath.absolutePath}")
        } else {
            Log.e("ImageCheck", "❌ Ảnh KHÔNG tồn tại: ${imagePath.absolutePath}")
        }

        // 🖼 Load ảnh bằng Glide (nếu có thì load, không thì dùng ảnh mặc định)
        Glide.with(context)
            .load(if (imagePath.exists()) imagePath else R.drawable.fail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)  // Không dùng cache để load ảnh mới nhất
            .skipMemoryCache(true)
            .into(imageSupplier)
        return view
    }

}
