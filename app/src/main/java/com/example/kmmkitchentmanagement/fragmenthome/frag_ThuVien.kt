package com.example.kmmkitchentmanagement.fragmenthome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.AppUtils.Utils
import com.example.kmmkitchentmanagement.Model.LuanVan
import com.example.kmmkitchentmanagement.Model.NguoiDung
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.ClickableItemsAdapter
import com.example.kmmkitchentmanagement.adapter.LuanVanAdapter
import com.example.kmmkitchentmanagement.fragmentSub.LuanVanView
import com.example.kmmkitchentmanagement.fragment_thuvien.LinhVucMain
import com.example.kmmkitchentmanagement.fragment_thuvien.LuanVanMain
import com.example.kmmkitchentmanagement.fragment_thuvien.TacGiaMain
import com.example.kmmkitchentmanagement.fragment_thuvien.XemSauMain
import com.example.kmmkitchentmanagement.viewmodelExtends.LuanVanVM
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FragThuVien : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var luanVanVM: LuanVanVM
    private lateinit var catalogView: ListView
    private lateinit var listGanDay: ListView
    private lateinit var luanVanAdapter: LuanVanAdapter
    private lateinit var catalogAdapter: ClickableItemsAdapter
    private lateinit var listRecent: MutableList<LuanVan>

    private var user = NguoiDung()
    private lateinit var textHello: TextView

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
    return inflater.inflate(R.layout.fragment_thu_vien, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        FirebaseInit()
        addView(view)

        getUserData()

        DataHandler()
        AttackData()
        attackView()
        eventHandler()
    }

    private fun FirebaseInit() {
        firestore = FirebaseFirestore.getInstance()
    }

    private fun DataHandler() {
        luanVanVM = ViewModelProvider(this).get(LuanVanVM::class.java)
        listRecent = mutableListOf()
        luanVanAdapter = LuanVanAdapter(requireContext(), listRecent)

        firestore.collection("/luanvan")
                .orderBy("addTime", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireActivity(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
            } else {
                listRecent.clear()
                listRecent.addAll(value?.toObjects(LuanVan::class.java) ?: emptyList())
                luanVanAdapter.notifyDataSetChanged()
                Utils.setListViewHeightBasedOnChildren(listGanDay)
            }
        }
    }

    private fun AttackData() {
        listGanDay.adapter = luanVanAdapter
    }

    private fun addView(view: View) {
        catalogView = view.findViewById(R.id.listThuVien)
        listGanDay = view.findViewById(R.id.listGanDay)
        textHello = view.findViewById(R.id.textHello)
    }

    private fun attackView() {
        val LuanVanMain = intArrayOf(R.drawable.ic_luan_van, R.string.LuanVanList)
        val TacGiaMain = intArrayOf(R.drawable.ic_tac_gia, R.string.TacGia)
        val ChuDeMain = intArrayOf(R.drawable.ic_chu_de, R.string.LinhVuc)
        val DaLuu = intArrayOf(R.drawable.ic_luu, R.string.Saved)

        val catalogs = arrayListOf(LuanVanMain, TacGiaMain, ChuDeMain, DaLuu)
        catalogAdapter = ClickableItemsAdapter(requireContext(), catalogs)
        catalogView.adapter = catalogAdapter
        Utils.setListViewHeightBasedOnChildren(catalogView)
    }

    private fun eventHandler() {
        catalogView.setOnItemClickListener { _, _, i, _ ->
                when (i) {
            0 -> {
                parentFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.thuvienView, LuanVanMain())
                        .addToBackStack("toLuanVanMains")
                        .commit()
            }
            1 -> {
                parentFragmentManager.beginTransaction()
                        .replace(R.id.thuvienView, TacGiaMain())
                        .addToBackStack("toTacgiaMains")
                        .commit()
            }
            2 -> {
                parentFragmentManager.beginTransaction()
                        .replace(R.id.thuvienView, LinhVucMain())
                        .addToBackStack("toLinhVucMain")
                        .commit()
            }
            3 -> {
                parentFragmentManager.beginTransaction()
                        .replace(R.id.thuvienView, XemSauMain())
                        .addToBackStack("toXemSauMain")
                        .commit()
            }
        }
        }

        listGanDay.setOnItemClickListener { _, _, i, _ ->
                luanVanVM.setData(listRecent[i])
            parentFragmentManager.beginTransaction()
                    .replace(R.id.thuvienView, LuanVanView())
                    .addToBackStack("kjd")
                    .commit()
        }
    }

    private fun getUserData() {
        userViewModel.user.observe(viewLifecycleOwner) { user ->
                user?.let {
            textHello.text = "Xin chào ${it.name}"
        }
        }
    }
}
