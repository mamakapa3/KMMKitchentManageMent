package com.example.kmmkitchentmanagement.customdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.example.kmmkitchentmanagement.AppUtils.Utils
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.IOTaskList
import com.google.firebase.storage.UploadTask

class UploadTaskDialog(private val uploadTasks: ArrayList<UploadTask>) : DialogFragment() {

    private lateinit var progressList: ListView
    private lateinit var ioTaskList: IOTaskList
    private lateinit var admit: Button
    private lateinit var dialogView: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.progress_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogView = view.findViewById(R.id.dialogView)
        dialogView.layoutParams.width = (resources.displayMetrics.widthPixels * 0.8).toInt()

        progressList = view.findViewById(R.id.progressList)
        ioTaskList = context?.let { IOTaskList(uploadTasks, it) }!!
        progressList.adapter = ioTaskList

        Utils.setListViewHeightBasedOnChildren(progressList)

        admit = view.findViewById(R.id.button)
        admit.setOnClickListener { dialog?.dismiss() }
    }

    fun getAdmitBtn(): Button = admit
}
