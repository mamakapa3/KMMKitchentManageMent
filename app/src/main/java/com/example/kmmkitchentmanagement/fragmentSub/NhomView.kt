package com.example.kmmkitchentmanagement.fragmentSub


import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.util.Log

import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.kmmkitchentmanagement.Model.Nhom
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.fragmenthome.frag_Nhom

import com.example.kmmkitchentmanagement.viewmodelExtends.NhomVM
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

import java.text.SimpleDateFormat
import java.util.Locale
class NhomView : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var backBtn: ImageButton
    private lateinit var btnNhomDelete: ImageButton
    private lateinit var members: LinearLayout
    private lateinit var btnLeave: Button
    private lateinit var title: TextView
    private lateinit var memNumber: TextView
    private lateinit var GroupCreator: TextView
    private lateinit var imageView: ImageView
    private val NhomVM: NhomVM by activityViewModels()
    private var Nhom: Nhom = Nhom()
    private var currentUserId: String = "" // Lưu ID người dùng hiện tại

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chitietnhom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseInit()
        getCurrentUserId()
        dataHandler()
        addView(view)
        eventHandler()
    }

    private fun firebaseInit() {
        if (!::firestore.isInitialized) {
            firestore = FirebaseFirestore.getInstance()
        }
    }

    private fun getCurrentUserId() {
        val userVM = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        currentUserId = userVM.getUser().value?.userId ?: ""
    }

    private fun dataHandler() {
        NhomVM.getData().observe(viewLifecycleOwner) { nhom: Nhom? ->
            if (nhom != null) {
                Nhom = nhom
                attachData()
            }
        }
    }

    private fun attachData() {
        title.text = Nhom.title
        memNumber.text = Nhom.memNumb.toString()

        // 🔹 Chỉ trưởng nhóm mới thấy nút Xóa Nhóm
        if (currentUserId == Nhom.creatorId) {
            btnNhomDelete.visibility = View.VISIBLE
        } else {
            btnNhomDelete.visibility = View.GONE
        }

        // 🔹 Lấy tên trưởng nhóm từ Firestore
        if (Nhom.creatorId.isNotEmpty()) {
            firestore.collection("User").document(Nhom.creatorId).get()
                .addOnSuccessListener { document ->
                    GroupCreator.text = document.getString("name") ?: "Không rõ"
                }
                .addOnFailureListener {
                    GroupCreator.text = "Lỗi tải tên"
                }
        } else {
            GroupCreator.text = "Không rõ"
        }


        val imagePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "YourAppImages/nhom_${Nhom.id}.jpg"
        )

        Glide.with(requireContext())
            .load(if (imagePath.exists()) imagePath else R.drawable.fail_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
    }

    private fun addView(view: View) {
        backBtn = view.findViewById(R.id.btnbackview)
        btnNhomDelete = view.findViewById(R.id.btnNhomDelete)
        btnLeave = view.findViewById(R.id.btnLeave)
        title = view.findViewById(R.id.textTenNhomview)
        memNumber = view.findViewById(R.id.textMemNumb)
        GroupCreator = view.findViewById(R.id.textGroupCreator)
        imageView = view.findViewById(R.id.imageview)
        members = view.findViewById(R.id.members)
    }

    private fun setupMemberListDialog(groupId: String) {
        val memberList = mutableListOf<Pair<String, String>>()
        val dialogBuilder = AlertDialog.Builder(requireContext())

        firestore.collection("Nhom").document(groupId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val memberIds = document.get("members") as? List<String> ?: emptyList()
                    if (memberIds.isEmpty()) {
                        dialogBuilder.setTitle("Danh sách thành viên")
                            .setMessage("Nhóm chưa có thành viên.")
                            .setPositiveButton("Đóng") { dialog, _ -> dialog.dismiss() }
                            .show()
                        return@addOnSuccessListener
                    }

                    val tasks = memberIds.map { memberId ->
                        firestore.collection("User").document(memberId).get()
                    }

                    Tasks.whenAllSuccess<DocumentSnapshot>(tasks).addOnSuccessListener { results ->
                        results.forEach { document ->
                            val id = document.id
                            val name = document.getString("name") ?: "Không tên"
                            memberList.add(Pair(id, name))
                        }

                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, memberList.map { it.second })

                        dialogBuilder.setTitle("Danh sách thành viên")
                            .setAdapter(adapter) { _, which ->
                                val memberId = memberList[which].first
                                confirmKickMember(groupId, memberId)
                            }
                            .setPositiveButton("Đóng") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Lỗi tải danh sách: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Lỗi tải nhóm: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmKickMember(groupId: String, memberId: String) {
        if (currentUserId != Nhom.creatorId) {
            Toast.makeText(requireContext(), "Chỉ trưởng nhóm mới có thể xóa thành viên!", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Xóa thành viên")
            .setMessage("Bạn có chắc muốn xóa thành viên này khỏi nhóm?")
            .setPositiveButton("Xóa") { _, _ -> kickMember(groupId, memberId) }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun kickMember(groupId: String, memberId: String) {
        val groupRef = firestore.collection("Nhom").document(groupId)

        groupRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val members = document.get("members") as? MutableList<String> ?: mutableListOf()

                if (members.contains(memberId)) {
                    members.remove(memberId)

                    groupRef.update(
                        mapOf(
                            "members" to members,
                            "memNumb" to members.size
                        )
                    ).addOnSuccessListener {
                        Toast.makeText(requireContext(), "Đã xóa thành viên!", Toast.LENGTH_SHORT).show()
                        memNumber.text = members.size.toString()
                    }.addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Lỗi khi xóa thành viên: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Không lấy được nhóm: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun deleteGroup(groupId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa Nhóm")
            .setMessage("Bạn có chắc chắn muốn xóa nhóm này không? Hành động này không thể hoàn tác.")
            .setPositiveButton("Xóa") { _, _ ->
                firestore.collection("Nhom").document(groupId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Nhóm đã được xóa!", Toast.LENGTH_SHORT).show()

                        // 🔹 Cập nhật danh sách nhóm của từng thành viên
                        removeGroupFromUsers(Nhom.members, groupId)

                        // 🔄 Quay lại danh sách nhóm
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.NhomView, frag_Nhom())
                            .commit()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Lỗi khi xóa nhóm: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
    private fun removeGroupFromUsers(memberIds: List<String>, groupId: String) {
        for (memberId in memberIds) {
            val userRef = firestore.collection("User").document(memberId)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val userGroups = document.get("groups") as? MutableList<String> ?: mutableListOf()
                    if (userGroups.contains(groupId)) {
                        userGroups.remove(groupId)
                        userRef.update("groups", userGroups)
                    }
                }
            }
        }
    }
    private fun showEditDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Chỉnh sửa tên nhóm")

        val input = android.widget.EditText(requireContext())
        input.setText(Nhom.title) // Hiển thị tên nhóm hiện tại
        input.setSelection(input.text.length) // Đặt con trỏ ở cuối
        builder.setView(input)

        builder.setPositiveButton("Lưu") { _, _ ->
            val newTitle = input.text.toString().trim()
            if (newTitle.isNotEmpty()) {
                updateGroupName(newTitle)
            } else {
                Toast.makeText(requireContext(), "Tên nhóm không được để trống!", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Hủy", null)
        builder.show()
    }
    private fun updateGroupName(newTitle: String) {
        firestore.collection("Nhom").document(Nhom.id)
            .update("title", newTitle)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Tên nhóm đã cập nhật!", Toast.LENGTH_SHORT).show()
                title.text = newTitle // Cập nhật UI
                Nhom = Nhom.copy(title = newTitle) // Cập nhật dữ liệu trong ViewModel
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Lỗi khi cập nhật: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eventHandler() {
        members.setOnClickListener {
            setupMemberListDialog(Nhom.id)
        }
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnNhomDelete.setOnClickListener {
            deleteGroup(Nhom.id)
        }
        title.setOnClickListener {
            if (currentUserId == Nhom.creatorId) {
                showEditDialog()
            }
        }

        btnLeave.setOnClickListener {
            if (currentUserId.isEmpty()) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nhomRef = firestore.collection("Nhom").document(Nhom.id)
            nhomRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val members = document.get("members") as? MutableList<String> ?: mutableListOf()

                    if (members.contains(currentUserId)) {
                        members.remove(currentUserId)
                        val newMemNumb = members.size

                        nhomRef.update(
                            mapOf(
                                "members" to members,
                                "memNumb" to newMemNumb
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(requireContext(), "Bạn đã rời nhóm!", Toast.LENGTH_SHORT).show()
                            memNumber.text = newMemNumb.toString()
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.NhomView, frag_Nhom())
                                .commit()
                        }
                    }
                }
            }
        }
    }
}
