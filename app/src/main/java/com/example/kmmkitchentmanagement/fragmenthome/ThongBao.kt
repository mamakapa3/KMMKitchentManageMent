package com.example.kmmkitchentmanagement.fragmenthome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kmmkitchentmanagement.Data.NetworkUtil
import com.example.kmmkitchentmanagement.Model.Notification
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.adapter.NotificationAdapter
import com.example.kmmkitchentmanagement.customdialog.ErrorDialog
import com.example.kmmkitchentmanagement.customdialog.WarmingDialog
import com.example.kmmkitchentmanagement.thongbao.NotificationInfo
import com.example.kmmkitchentmanagement.viewmodelExtends.NotificationViewModel
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel
import java.util.ArrayList

class ThongBao : Fragment() {
    private lateinit var nofiLog: TextView
    private lateinit var listViewNotifications: ListView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var mySpinner: Spinner
    private var parse = 0

    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var userViewModel: UserViewModel

    private val READ_NOTIFICATION_TAG = "readNotification"
    private val DELETE_NOTIFICATION_TAG = "deleteNotification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        notificationViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_thong_bao, container, false)
        initializeUI(view)
        setupObservers()

        parentFragmentManager.setFragmentResultListener("notificationDetailClosed", this) { _, result ->
            val isUpdated = result.getBoolean("isUpdated", false)
            if (isUpdated) {
                userViewModel.getUser().observe(viewLifecycleOwner) { user ->
                    user?.let {
                        notificationViewModel.fetchNotifications(user.email, parse)
                    }
                }
            }
        }

        return view
    }

    private fun initializeUI(view: View) {
        nofiLog = view.findViewById(R.id.nofiLog)
        listViewNotifications = view.findViewById(R.id.lvNotification)
        mySpinner = view.findViewById(R.id.mySpinner)

        val adapter = context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.my_spinner_items_notification,
                R.layout.spinner_item
            )
        }
        if (adapter != null) {
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        }
        mySpinner.adapter = adapter

        mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parse = position
                userViewModel.getUser().observe(viewLifecycleOwner) { user ->
                    user?.let {
                        if (!NetworkUtil.isWifiConnected(requireContext())) {
                            showErrorConnectDialog()
                            return@observe
                        }
                        notificationViewModel.fetchNotifications(user.email, parse)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        notificationAdapter =
            context?.let { NotificationAdapter(it, ArrayList(), notificationViewModel) }!!
        listViewNotifications.adapter = notificationAdapter

        listViewNotifications.setOnItemClickListener { parent, _, position, _ ->
            val notification = parent.getItemAtPosition(position) as Notification
            markNotificationAsRead(notification)
            openNotificationDetailFragment(notification)
        }

        view.findViewById<Button>(R.id.textReadNotification).setOnClickListener {
            showWarmingDialog("Bạn chắc chắn muốn đọc tất cả thông báo?", "Hủy", "Đọc tất cả", READ_NOTIFICATION_TAG)
        }

        parentFragmentManager.setFragmentResultListener(READ_NOTIFICATION_TAG, viewLifecycleOwner) { _, result ->
            val confirmed = result.getBoolean("result")
            if (confirmed) {
                markAllNotification()
            }
        }

        view.findViewById<Button>(R.id.textDeleteNotification).setOnClickListener {
            showWarmingDialog("Bạn chắc chắn muốn xóa tất cả thông báo?", "Hủy", "Xóa tất cả", DELETE_NOTIFICATION_TAG)
        }

        parentFragmentManager.setFragmentResultListener(DELETE_NOTIFICATION_TAG, viewLifecycleOwner) { _, result ->
            val confirmed = result.getBoolean("result")
            if (confirmed) {
                deleteNotification()
            }
        }

        val ntemp = view.findViewById<Button>(R.id.button2)
        ntemp.setOnClickListener {
            userViewModel.getUser().observe(viewLifecycleOwner) { user ->
                user?.let {
                    notificationViewModel.addNotification("Tiêu đề thông báo", "Nội dung thông báo", user.email)
                }
            }
        }
    }

    private fun showWarmingDialog(message: String, leftBtnText: String, rightBtnText: String, tag: String) {
        val warmingDialog = WarmingDialog(message, leftBtnText, rightBtnText)
        warmingDialog.show(parentFragmentManager, tag)
    }

    private fun openNotificationDetailFragment(notification: Notification) {
        val bundle = Bundle().apply {
            putSerializable("notification", notification)
        }
        val notificationInfoFragment = NotificationInfo().apply {
            arguments = bundle
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, notificationInfoFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun markNotificationAsRead(notification: Notification) {
        if (!notification.isRead && context?.let { NetworkUtil.isWifiConnected(it) } == true) {
//            notificationViewModel.updateFirebaseRead(notification.ID) // Cập nhật trạng thái đã đọc
        }
    }

    private fun setupObservers() {
        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            user?.let {
                notificationViewModel.fetchNotifications(user.email, parse)

                notificationViewModel.notifications.observe(viewLifecycleOwner) { notifications ->
                    notificationAdapter.updateNotifications(notifications)
                    updateNotificationLog()
                }

                notificationViewModel.countNotifiNotRead.observe(viewLifecycleOwner) { count ->
                    val logtemp = "Bạn có $count thông báo mới!"
                    nofiLog.text = logtemp
                    nofiLog.visibility = if (count > 0) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun updateNotificationLog() {
        notificationAdapter?.notifyDataSetChanged()
    }

    private fun markAllNotification() {
        if (!context?.let { NetworkUtil.isWifiConnected(it) }!!) {
            showErrorConnectDialog()
            return
        }
        notificationViewModel.markAllNotificationsAsRead()
        Toast.makeText(context, "Đã đánh dấu tất cả thông báo là đã đọc.", Toast.LENGTH_LONG).show()
    }

    private fun deleteNotification() {
        if (!context?.let { NetworkUtil.isWifiConnected(it) }!!) {
            showErrorConnectDialog()
            return
        }
        notificationViewModel.deleteNotifications()
        Toast.makeText(context, "Đã xóa các thông báo đã đọc.", Toast.LENGTH_SHORT).show()
    }

    private fun showErrorConnectDialog() {
        val log = "Không có kết nối mạng. Vui lòng kiểm tra lại kết nối rồi thử lại sau!"
        val errorDialog = ErrorDialog(log, false)
        errorDialog.show(parentFragmentManager, "errorConnectDialog")
    }
}
