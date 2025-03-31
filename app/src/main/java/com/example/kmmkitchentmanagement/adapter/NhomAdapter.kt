package com.example.kmmkitchentmanagement.adapter

import android.app.Activity
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
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
class NhomAdapter(private val context: Context, private var list: MutableList<Nhom>) : BaseAdapter(), Filterable {
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private var reflectList: MutableList<Nhom> = ArrayList(list)

    override fun getCount(): Int = list.size
    override fun getItem(i: Int): Any = list[i]
    override fun getItemId(i: Int): Long = i.toLong()

    private val filter: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val result = mutableListOf<Nhom>()
            if (charSequence.isNullOrEmpty()) {
                result.addAll(reflectList)
            } else {
                reflectList.forEach { nhom ->
                    if (nhom.title.trim().lowercase(Locale.getDefault()).contains(charSequence.toString().trim().lowercase(Locale.getDefault()))) {
                        result.add(nhom)
                    }
                }
            }
            return FilterResults().apply { values = result }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            list.clear()
            (filterResults.values as? MutableList<Nhom>)?.let {
                list.addAll(it)
            }
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter = filter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_noi_dung_nhom_main, parent, false)

        val textLVtieude: TextView = view.findViewById(R.id.textLVtieude)
        val imageNhom: ImageView = view.findViewById(R.id.imageNhom)

        val nhom = list[position]
        textLVtieude.text = nhom.title

        // 📂 Đường dẫn ảnh của nhóm
        val imagePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "YourAppImages/nhom_${nhom.id}.jpg"
        )

        // 🛠 Kiểm tra xem file có tồn tại không
        if (imagePath.exists()) {
            Log.d("ImageCheck", "✅ Ảnh tồn tại: ${imagePath.absolutePath}")
        } else {
            Log.e("ImageCheck", "❌ Ảnh KHÔNG tồn tại: ${imagePath.absolutePath}")
        }

        val activity = context as? Activity
        if (activity == null || activity.isDestroyed) {
            return view // Không load ảnh nếu Activity đã bị hủy
        }

        // 🖼 Load ảnh bằng Glide (nếu có thì load, không thì dùng ảnh mặc định)
        Glide.with(context)
            .load(if (imagePath.exists()) imagePath else R.drawable.fail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageNhom)

        return view  // 🔹 **Thêm dòng này để tránh lỗi**
    }
}
