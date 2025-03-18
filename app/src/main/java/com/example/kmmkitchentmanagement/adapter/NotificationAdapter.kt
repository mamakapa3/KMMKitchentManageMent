package com.example.kmmkitchentmanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.kmmkitchentmanagement.Model.Notification
import com.example.kmmkitchentmanagement.R
import com.example.kmmkitchentmanagement.viewmodelExtends.NotificationViewModel

class NotificationAdapter(
    private val context: Context,
    private var notificationList: MutableList<Notification> = mutableListOf(),
    private val notificationViewModel: NotificationViewModel
) : BaseAdapter() {

    override fun getCount(): Int {
        return notificationList.size
    }

    override fun getItem(position: Int): Any {
        return notificationList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.listview_notification, parent, false).apply {
            holder = ViewHolder()
            holder.imageEmail = findViewById(R.id.imageEmail)
            holder.titleTextView = findViewById(R.id.titleNotificatoin)
            holder.userTextView = findViewById(R.id.userNotificatoin)
            holder.timeTextView = findViewById(R.id.timeNotification)
            tag = holder
        }

        holder = view.tag as ViewHolder

        val notification = getItem(position) as Notification
        updateView(holder, notification)

        return view
    }

    private fun updateView(holder: ViewHolder, notification: Notification) {
        if (notification.isRead) {
            holder.imageEmail.setImageResource(R.drawable.open_message)
        } else {
            holder.imageEmail.setImageResource(R.drawable.email)
        }
        holder.titleTextView.text = notification.title
        holder.userTextView.text = notification.user
        holder.timeTextView.text = notification.dateString
    }

    fun updateNotifications(notifications: List<Notification>?) {
        notificationList.clear()
        notifications?.let { notificationList.addAll(it) }
        notifyDataSetChanged()
    }

    private class ViewHolder {
        lateinit var imageEmail: ImageView
        lateinit var titleTextView: TextView
        lateinit var userTextView: TextView
        lateinit var timeTextView: TextView
    }
}
