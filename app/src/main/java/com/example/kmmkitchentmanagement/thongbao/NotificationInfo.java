package com.example.kmmkitchentmanagement.thongbao;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kmmkitchentmanagement.Model.Notification;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.customdialog.WarmingDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationInfo extends Fragment {
    private TextView messageTitle;
    private TextView messageSender;
    private TextView messageDate;
    private TextView messageContent;
    private TextView logReported;
    private Button buttonReport;
    private FirebaseFirestore db;

    private Notification notification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_info, container, false);

        AddView(view);

        return view;
    }

    private void AddView(View view){
        messageTitle = view.findViewById(R.id.message_title);
        messageSender = view.findViewById(R.id.message_sender);
        messageDate = view.findViewById(R.id.message_date);
        messageContent = view.findViewById(R.id.message_content);
        logReported = view.findViewById(R.id.logReported);
        buttonReport = view.findViewById(R.id.buttonReport);

        // Get data from arguments
        if (getArguments() != null) {
            notification = (Notification) getArguments().getSerializable("notification");
            if (notification != null) {
                messageTitle.setText(notification.getTitle());
                messageSender.setText("Người gửi: " + notification.getUser());
                messageDate.setText("Thời gian: " + notification.getDateString());
                messageContent.setText(notification.getContent()); // Thay đổi nếu `Notification` có trường này
                switch (notification.getIsReport()) {
                    case 1:
                        buttonReport.setText("Báo cáo");
                        buttonReport.setVisibility(View.VISIBLE);
                        logReported.setVisibility(View.GONE);
                        break;
                    case 2:
                        buttonReport.setText("Huỷ báo cáo");
                        buttonReport.setVisibility(View.VISIBLE);
                        logReported.setVisibility(View.VISIBLE);
                        break;
                    default:
                        buttonReport.setVisibility(View.GONE);
                        logReported.setVisibility(View.GONE);
                        break;
                }
            }
        }

        // Set up click listener for report button
        buttonReport.setOnClickListener(v -> {
            // Handle report action
            showWarmingDialog();
        });
    }

    private void showWarmingDialog() {
        String message;
        if(notification.getIsReport() == 1){
            message = "Bạn có chắc chắn muốn báo cáo thông báo này không?";
        }else if(notification.getIsReport() == 2){
            message = "Bạn có chắc chắn muốn hủy báo cáo thông báo này không?";
        }else{
            return;
        }
        WarmingDialog warmingDialog = new WarmingDialog(message, "Huỷ", "Báo cáo");

        // Đặt tag để xử lý kết quả
        String dialogTag = "WarmingDialog";

        // Hiển thị dialog
        warmingDialog.show(getParentFragmentManager(), dialogTag);

        // Lắng nghe kết quả trả về từ WarmingDialog
        getParentFragmentManager().setFragmentResultListener(dialogTag, this, (requestKey, result) -> {
            boolean isConfirmed = result.getBoolean("result");
            if (isConfirmed) {
                // Xử lý khi người dùng xác nhận "Báo cáo"
                handleReport();
            } else {
                // Xử lý khi người dùng chọn "Huỷ"
                Log.d("NotificationInfo", "Báo cáo đã bị huỷ");
            }
        });
    }

    private void handleReport() {
        db = FirebaseFirestore.getInstance();
        // Lấy id của thông báo từ đối tượng Notification
        int notificationId = notification.getID(); // Giả sử Notification có trường ID

        // Lấy tham chiếu đến tài liệu thông báo trong Firestore
        DocumentReference notificationRef = db.collection("Notification").document(String.valueOf(notificationId));

        // Thay đổi trạng thái báo cáo
        int newReportStatus = (notification.getIsReport() == 1) ? 2 : 1; // Đổi trạng thái giữa 1 và 2

        // Cập nhật trạng thái báo cáo trong Firestore
        notificationRef.update("reported", newReportStatus)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật thành công
                    notification.setIsReport(newReportStatus); // Cập nhật giá trị trong đối tượng Notification
                    Toast.makeText(getContext(), "Cập nhật trạng thái báo cáo thành công", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi cập nhật thất bại
                    Log.w("NotificationInfo", "Lỗi khi cập nhật trạng thái báo cáo", e);
                    Toast.makeText(getContext(), "Lỗi khi cập nhật trạng thái báo cáo", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Bundle result = new Bundle();
        result.putBoolean("isUpdated", true);  // Thông báo rằng dữ liệu đã được cập nhật
        getParentFragmentManager().setFragmentResult("notificationDetailClosed", result);
    }
}
