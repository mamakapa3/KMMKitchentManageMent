package com.example.kmmkitchentmanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ProgressBar
import com.example.kmmkitchentmanagement.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask

class IOTaskList(
    private val uploadTasks: ArrayList<UploadTask>,
    private val context: Context
) : BaseAdapter() {

    override fun getCount(): Int {
        return uploadTasks.size
    }

    override fun getItem(position: Int): Any {
        return uploadTasks[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.onprogress, parent, false)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar2)
        val btn: ImageButton = view.findViewById(R.id.state)
        val uploadTask = uploadTasks[position]

        uploadTask.addOnProgressListener { snapshot ->
            val progress = snapshot.bytesTransferred / snapshot.totalByteCount.toDouble()
            progressBar.progress = (progress * 100).toInt()
        }

        uploadTask.addOnSuccessListener { taskSnapshot ->
            btn.setImageResource(R.drawable.check)
        }

        uploadTask.addOnFailureListener { e ->
            btn.setImageResource(R.drawable.error)
        }

        return view
    }
}
