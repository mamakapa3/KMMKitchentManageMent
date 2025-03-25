package com.example.kmmkitchentmanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ProgressBar

import com.example.kmmkitchentmanagement.R
import com.google.firebase.storage.UploadTask

class IOTaskList(private val uploadTasks: MutableList<UploadTask>, private val context: Context) : BaseAdapter() {

    override fun getCount(): Int = uploadTasks.size

    override fun getItem(i: Int): Any = uploadTasks[i]

    override fun getItemId(i: Int): Long = i.toLong()

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val itemView = view ?: LayoutInflater.from(context).inflate(R.layout.onprogress, null)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar2)
        val btn: ImageButton = itemView.findViewById(R.id.state)
        val uploadTask = uploadTasks[i]

        uploadTask.addOnProgressListener { snapshot ->
            val progress = (snapshot.bytesTransferred * 100 / snapshot.totalByteCount).toInt()
            progressBar.progress = progress
        }

        uploadTask.addOnSuccessListener {
            btn.setImageResource(R.drawable.check)
        }

        uploadTask.addOnFailureListener {
            btn.setImageResource(R.drawable.error)
        }
        return itemView
    }
}
