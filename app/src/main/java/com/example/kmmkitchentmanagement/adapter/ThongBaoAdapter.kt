package com.example.kmmkitchentmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kmmkitchentmanagement.R

class ThongBaoAdapter(private val thongBaoList: List<String>) :
    RecyclerView.Adapter<ThongBaoAdapter.ThongBaoViewHolder>() {

    class ThongBaoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textThongBao: TextView = view.findViewById(R.id.textThongBao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThongBaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thong_bao, parent, false)
        return ThongBaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThongBaoViewHolder, position: Int) {
        holder.textThongBao.text = thongBaoList[position]
    }

    override fun getItemCount(): Int = thongBaoList.size
}
