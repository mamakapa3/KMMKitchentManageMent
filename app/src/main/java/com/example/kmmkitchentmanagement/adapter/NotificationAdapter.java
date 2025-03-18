package com.example.kmmkitchentmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kmmkitchentmanagement.Model.Notification;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.viewmodelExtends.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    private Context context;
    private List<Notification> notificationList;
    private NotificationViewModel notificationViewModel;

    public NotificationAdapter(Context context, List<Notification> notifications, NotificationViewModel viewModel) {
        this.context = context;
        this.notificationList = notifications != null ? notifications : new ArrayList<>();
        this.notificationViewModel = viewModel;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_notification, parent, false);
            holder = new ViewHolder();
            holder.imageEmail = convertView.findViewById(R.id.imageEmail);
            holder.titleTextView = convertView.findViewById(R.id.titleNotificatoin);
            holder.userTextView = convertView.findViewById(R.id.userNotificatoin);
            holder.timeTextView = convertView.findViewById(R.id.timeNotification);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Cập nhật dữ liệu cho các view
        Notification notification = (Notification) getItem(position);
        updateView(holder, notification);

        return convertView;
    }

    private void updateView(ViewHolder holder, Notification notification) {
        if (notification.isRead()) {
            holder.imageEmail.setImageResource(R.drawable.open_message);
        } else {
            holder.imageEmail.setImageResource(R.drawable.email);
        }
        holder.titleTextView.setText(notification.getTitle());
        holder.userTextView.setText(notification.getUser());
        holder.timeTextView.setText(notification.getDateString());
    }

    public void updateNotifications(List<Notification> notifications) {
        this.notificationList.clear();
        if (notifications != null) {
            this.notificationList.addAll(notifications);
        }
        notifyDataSetChanged(); // Thông báo cho ListView để cập nhật
    }

    private static class ViewHolder {
        ImageView imageEmail;
        TextView titleTextView;
        TextView userTextView;
        TextView timeTextView;
    }
}
