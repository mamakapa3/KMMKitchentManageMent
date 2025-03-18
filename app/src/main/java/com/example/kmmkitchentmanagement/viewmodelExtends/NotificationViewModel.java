package com.example.kmmkitchentmanagement.viewmodelExtends;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kmmkitchentmanagement.Model.Notification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationViewModel extends ViewModel {
    private MutableLiveData<List<Notification>> notifications;
    private MutableLiveData<Integer> countNotifiNotRead;
    private final int reportNum = 2;

    public NotificationViewModel() {
        notifications = new MutableLiveData<>(new ArrayList<>());
        countNotifiNotRead = new MutableLiveData<>(0);
    }

    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public LiveData<Integer> getCountNotifiNotRead() {
        return countNotifiNotRead;
    }



    public void fetchNotifications(String emailUser, int parse) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notification")
                .whereEqualTo("email_receive", emailUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> notificationList = new ArrayList<>();
                        int count = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int isReported = document.getLong("reported").intValue();
                            boolean isRead = document.getBoolean("isRead");
                            Log.d("ThongBao", "isReported: " + isReported);

                            switch (parse) {
                                case 2:
                                    // Khi parse = 2, chỉ thêm thông báo nếu nó được báo cáo
                                    if (isReported == reportNum) {
                                        notificationList.add(createNotificationFromDocument(document, isRead, isReported));
                                        Log.d("ThongBao", "Mode: 2");
                                    }
                                    break;

                                case 0:
                                    // Khi parse = 0, thêm thông báo đã đọc
                                    if (isReported != reportNum) {
                                        notificationList.add(createNotificationFromDocument(document, isRead, isReported));
                                        Log.d("ThongBao", "Mode: 0");
                                    }
                                    if(!isRead){
                                        count++;
                                    }
                                    break;
                                case 1:
                                    // Khi parse = 1, thêm thông báo chưa đọc
                                    if (!isRead && isReported != reportNum) {
                                        count++;
                                        notificationList.add(createNotificationFromDocument(document, isRead, isReported));
                                        Log.d("ThongBao", "Mode: 1");
                                    }
                                    break;

                                default:
                                    Log.d("ThongBao", "Unknown mode: " + parse);
                                    break;
                            }
                        }
                        // Cập nhật LiveData
                        notifications.setValue(notificationList);
                        countNotifiNotRead.setValue(count);
                    } else {
                        // Xử lý lỗi
                        Log.e("ThongBao", "Error fetching notifications", task.getException());
                    }
                });
    }

    private Notification createNotificationFromDocument(QueryDocumentSnapshot document, boolean isRead, int report) {
        int id = Integer.parseInt(document.getId());
        String title = document.getString("title");
        String user = document.getString("user");
        String content = document.getString("noidung");
        Date date = document.getDate("date");
        return new Notification(id, title, user, content, date, isRead, report);
    }

    public void markAllNotificationsAsRead() {
        List<Notification> currentNotifications = notifications.getValue();
        if (currentNotifications != null) {
            for (Notification notification : currentNotifications) {
                if (!notification.isRead()) {
                    notification.setRead(true);
                    updateFirebaseRead(notification.getID());
                }
            }
            // Cập nhật giá trị countNotifiNotRead sau khi thay đổi
            countNotifiNotRead.setValue(0); // Đặt lại số lượng thông báo chưa đọc về 0
            notifications.setValue(currentNotifications); // Cập nhật danh sách thông báo
        }
    }

    public void updateFirebaseRead(int notificationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String notificationIdString = Integer.toString(notificationId); // Chuyển ID số nguyên thành chuỗi
        db.collection("Notification")
                .document(notificationIdString) // Truy cập tài liệu bằng ID chuỗi
                .update("isRead", true)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notification marked as read: " + notificationId))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating notification: " + notificationId, e));
    }

    public void deleteNotifications() {
        List<Notification> currentNotifications = notifications.getValue();
        if (currentNotifications != null) {
            List<Notification> notificationsToKeep = new ArrayList<>();
            for (Notification notification : currentNotifications) {
                if (!notification.isRead()) {
                    notificationsToKeep.add(notification);
                } else {
                    deleteNotificationFromFirebase(notification.getID());
                }
            }
            notifications.setValue(notificationsToKeep);
            // Cập nhật số lượng thông báo chưa đọc
            countNotifiNotRead.setValue(notificationsToKeep.size());
        }
    }

    private void deleteNotificationFromFirebase(int notificationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String notificationIdString = Integer.toString(notificationId); // Chuyển ID số nguyên thành chuỗi
        db.collection("Notification")
                .document(notificationIdString) // Truy cập tài liệu bằng ID chuỗi
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notification deleted: " + notificationId))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting notification: " + notificationId, e));
    }

    public void addNotification(String title, String content, String userEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AtomicInteger newId = new AtomicInteger(1);  // Khởi tạo ID mới với giá trị 1

        // Truy vấn để tìm ID lớn nhất hiện có trong collection "Notification"
        db.collection("Notification")
                .orderBy("id", Query.Direction.DESCENDING) // Sắp xếp theo ID giảm dần
                .limit(1) // Lấy tài liệu có ID lớn nhất
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Lấy tài liệu đầu tiên trong danh sách
                        DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                        Long lastId = lastDocument.getLong("id"); // Lấy giá trị ID cuối cùng

                        // Kiểm tra nếu lastId không null và tăng giá trị ID lên 1
                        if (lastId != null) {
                            newId.set(lastId.intValue() + 1);
                        }
                    }

                    // Kiểm tra ID mới và tự động tăng ID nếu bị trùng
                    checkAndAddNotification(db, newId.get(), title, content, userEmail);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching last notification ID", e);
                });
    }

    private void checkAndAddNotification(FirebaseFirestore db, int id, String title, String content, String userEmail) {
        db.collection("Notification").document(String.valueOf(id)).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Nếu ID đã tồn tại, tăng ID lên và kiểm tra lại
                        Log.e("Firestore", "ID đã tồn tại: " + id + ", tăng ID và thử lại.");
                        checkAndAddNotification(db, id + 1, title, content, userEmail);
                    } else {
                        // Nếu ID chưa tồn tại, tiến hành thêm thông báo
                        Map<String, Object> notification = new HashMap<>();
                        notification.put("id", id);                    // ID mới
                        notification.put("title", title);              // Tiêu đề thông báo
                        notification.put("noidung", content);          // Nội dung
                        notification.put("email_receive", userEmail);  // Email người dùng nhận thông báo
                        notification.put("date", new Date());          // Ngày tháng
                        notification.put("isRead", false);             // Trạng thái chưa đọc
                        notification.put("reported", 1);               // Số lần báo cáo
                        notification.put("user", "Self");              // Người tạo

                        // Lưu thông báo với ID là số vào Firestore
                        db.collection("Notification")
                                .document(String.valueOf(id))   // Sử dụng ID là số làm document ID
                                .set(notification)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Notification added with ID: " + id);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error adding notification", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error checking if ID exists", e);
                });
    }
}
