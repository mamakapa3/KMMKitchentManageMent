package com.example.kmmkitchentmanagement.fragmenthome

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kmmkitchentmanagement.Model.LuanVan
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.fragmentSub.frag_appinforView
import com.example.kmmkitchentmanagement.fragmentSub.frag_lichsu
import com.example.kmmkitchentmanagement.interfaceFile.LogoutListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class frag_CongCu : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var all: TextView
    private lateinit var recent7: TextView
    private lateinit var recent30: TextView
    private lateinit var onprogress: TextView
    private lateinit var published: TextView
    private lateinit var txtCCTongTG: TextView
    private lateinit var txtCCTongCD: TextView
    private lateinit var mExit: Button
    private lateinit var historyBtn: Button
    private lateinit var ViewInforBtn: Button
    private lateinit var savetofileBtn: Button
    private var logoutListener: LogoutListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_frag__cong_cu, container, false)
        addView(view)
        exit(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseInit()
        dataHandler()
        eventHandler()
    }

    private fun firebaseInit() {
        firestore = FirebaseFirestore.getInstance()
    }

    private fun dataHandler() {
        val last7Day = Date().apply { date -= 7 }
        val last30Day = Date().apply { date -= 30 }

        firestore.collection("/luanvan").count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                all.text = task.result?.count.toString()
            }

        firestore.collection("/luanvan")
            .whereGreaterThan("addTime", last7Day)
            .count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                recent7.text = task.result?.count.toString()
            }

        firestore.collection("/luanvan")
            .whereGreaterThan("addTime", last30Day)
            .count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                recent30.text = task.result?.count.toString()
            }

        firestore.collection("/luanvan")
            .whereEqualTo("published", true)
            .count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                published.text = task.result?.count.toString()
            }

        firestore.collection("/luanvan")
            .whereEqualTo("published", false)
            .count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                onprogress.text = task.result?.count.toString()
            }

        firestore.collection("/tacgia").count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                txtCCTongTG.text = task.result?.count.toString()
            }

        firestore.collection("/linhvuc").document("/linhvuc")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val linhvuc = value?.get("linhvuc") as? List<*>
                txtCCTongCD.text = linhvuc?.size.toString()
                refreshLayout.isRefreshing = false
            }
    }

    private fun addView(view: View) {
        historyBtn = view.findViewById(R.id.historyBtn)
        refreshLayout = view.findViewById(R.id.swiperLayout)
        mExit = view.findViewById(R.id.btnExit)
        all = view.findViewById(R.id.all)
        recent7 = view.findViewById(R.id.recent7)
        recent30 = view.findViewById(R.id.recent30)
        onprogress = view.findViewById(R.id.onprogress)
        published = view.findViewById(R.id.published)
        txtCCTongTG = view.findViewById(R.id.txtCCTongTG)
        txtCCTongCD = view.findViewById(R.id.txtCCTongCD)
        ViewInforBtn = view.findViewById(R.id.inforView)
        savetofileBtn = view.findViewById(R.id.savetofileBtn)
    }

    private fun eventHandler() {
        refreshLayout.setOnRefreshListener { dataHandler() }

        historyBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.congcuMain, frag_lichsu())
                .addToBackStack("xem lich su")
                .commit()
        }

        ViewInforBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.congcuMain, frag_appinforView())
                .addToBackStack("xem lich su")
                .commit()
        }

        savetofileBtn.setOnClickListener {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(path, "luanvanText.txt")
            val sb = StringBuilder()

            firestore.collection("/luanvan").get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    val luanvan = ArrayList(queryDocumentSnapshots.toObjects(LuanVan::class.java))

                    luanvan.forEach { lv ->
                        sb.append(lv.toString())
                        try {
                            val fileOutputStream = FileOutputStream(file)
                            fileOutputStream.write(sb.toString().toByteArray())
                            fileOutputStream.close()
                            Toast.makeText(context, "Lưu thông tin luận văn vào \nDownload/luanvanText.txt", Toast.LENGTH_LONG).show()
                        } catch (e: IOException) {
                            Log.i("Lỗi đọc ghi file", e.toString())
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Lỗi xảy ra khi tải dữ liệu", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun exit(view: View) {
        mExit.setOnClickListener {
            onLogoutButtonClicked()
        }
    }

    private fun onLogoutButtonClicked() {
        logoutListener?.onLogout()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LogoutListener) {
            logoutListener = context
        } else {
            throw RuntimeException("$context must implement LogoutListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        logoutListener = null
    }
}
