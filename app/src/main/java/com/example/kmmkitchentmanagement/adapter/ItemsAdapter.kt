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
import com.example.kmmkitchentmanagement.AppUtils.Utils
import com.example.kmmkitchentmanagement.Model.Items
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ItemsAdapter(private val context: Context, private var list: MutableList<Items>) : BaseAdapter(), Filterable {
    private var reflectList: MutableList<Items> = ArrayList(list)

    override fun getCount(): Int = list.size
    override fun getItem(i: Int): Any = list[i]
    override fun getItemId(i: Int): Long = i.toLong()

    private val filter: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val result = mutableListOf<Items>()
            if (charSequence.isNullOrEmpty()) {
                result.addAll(reflectList)
            } else {
                reflectList.forEach { Items ->
                    if (Items.title.trim().lowercase(Locale.getDefault()).contains(charSequence.toString().trim().lowercase(Locale.getDefault()))) {
                        result.add(Items)
                    }
                }
            }
            return FilterResults().apply { values = result }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            list.clear()
            (filterResults.values as? MutableList<Items>)?.let {
                list.addAll(it)
            }
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter = filter

override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_noi_dung_items_main, parent, false)

    val textLVtieude: TextView = view.findViewById(R.id.textLVtieude)
    val imageNhom: ImageView = view.findViewById(R.id.imageItems)

    val items = list[position]
    textLVtieude.text = items.title

    // 📂 Đường dẫn ảnh của nhóm
    val imagePath = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "YourAppImages/items_${items.id}.jpg"
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
        .into(imageNhom)
    return view
}
}