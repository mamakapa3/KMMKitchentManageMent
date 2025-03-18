package com.example.kmmkitchentmanagement.fragmenthome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kmmkitchentmanagement.Data.NetworkUtil;
import com.example.kmmkitchentmanagement.Model.Notification;
import com.example.kmmkitchentmanagement.R;
import com.example.kmmkitchentmanagement.adapter.NotificationAdapter;
import com.example.kmmkitchentmanagement.customdialog.ErrorDialog;
import com.example.kmmkitchentmanagement.customdialog.WarmingDialog;
import com.example.kmmkitchentmanagement.thongbao.NotificationInfo;
import com.example.kmmkitchentmanagement.viewmodelExtends.NotificationViewModel;
import com.example.kmmkitchentmanagement.viewmodelExtends.UserViewModel;

import java.util.ArrayList;

public class ThongBao extends Fragment {
    private TextView nofiLog;
    private ListView listViewNotifications;
    private NotificationAdapter notificationAdapter;
    private Spinner mySpinner;
    private int parse = 0;

    private NotificationViewModel notificationViewModel;
    private UserViewModel userViewModel;

    private static final String READ_NOTIFICATION_TAG = "readNotification";
    private static final String DELETE_NOTIFICATION_TAG = "deleteNotification";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thong_bao, container, false);
        initializeUI(view);
        setupObservers();

        getParentFragmentManager().setFragmentResultListener("notificationDetailClosed", this, (requestKey, result) -> {
            boolean isUpdated = result.getBoolean("isUpdated", false);
            if (isUpdated) {
                // Cập nhật lại danh sách thông báo
                userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        notificationViewModel.fetchNotifications(user.getEmail(), parse);
                    }
                });
            }
        });

        return view;
    }

    private void initializeUI(View view) {
        nofiLog = view.findViewById(R.id.nofiLog);
        listViewNotifications = view.findViewById(R.id.lvNotification);
        mySpinner = view.findViewById(R.id.mySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.my_spinner_items_notification,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mySpinner.setAdapter(adapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parse = position; // Cập nhật giá trị parse theo lựa chọn
                userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        if (!NetworkUtil.isWifiConnected(requireContext())) {
                            showErrorConnectDialog(); return;
                        }
                        notificationViewModel.fetchNotifications(user.getEmail(), parse);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Có thể để trống nếu không cần xử lý
            }
        });

        notificationAdapter = new NotificationAdapter(getContext(), new ArrayList<>(), notificationViewModel);
        listViewNotifications.setAdapter(notificationAdapter);

        listViewNotifications.setOnItemClickListener((parent, itemView, position, id) -> {
            Notification notification = (Notification) parent.getItemAtPosition(position);
            markNotificationAsRead(notification);
            openNotificationDetailFragment(notification);
        });

        view.findViewById(R.id.textReadNotification).setOnClickListener(v ->
                showWarmingDialog("Bạn chắc chắn muốn đọc tất cả thông báo?", "Hủy", "Đọc tất cả", READ_NOTIFICATION_TAG));
        getParentFragmentManager().setFragmentResultListener(READ_NOTIFICATION_TAG, getViewLifecycleOwner(), (requestKey, result) -> {
            boolean confirmed = result.getBoolean("result");
            if (confirmed) {
                MarkAllNotification();
            }
        });
        view.findViewById(R.id.textDeleteNotification).setOnClickListener(v -> {
            showWarmingDialog("Bạn chắc chắn muốn xóa tất cả thông báo?", "Hủy", "Xóa tất cả", DELETE_NOTIFICATION_TAG);
        });
        getParentFragmentManager().setFragmentResultListener(DELETE_NOTIFICATION_TAG, getViewLifecycleOwner(), (requestKey, result) -> {
            boolean confirmed = result.getBoolean("result");
            if (confirmed) {
                DeleteNotification();
            }
        });



        Button ntemp = view.findViewById(R.id.button2);
        ntemp.setOnClickListener(v -> {
            userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    notificationViewModel.addNotification("Tiêu đề thông báo", "Nội dung thông báo", user.getEmail());
                }
            });
        });
    }

    private void showWarmingDialog(String message, String leftBtnText, String rightBtnText, String tag) {
        WarmingDialog warmingDialog = new WarmingDialog(message, leftBtnText, rightBtnText);
        warmingDialog.show(getParentFragmentManager(), tag);
    }

    private void openNotificationDetailFragment(Notification notification) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("notification", notification);

        NotificationInfo notificationInfoFragment = new NotificationInfo();
        notificationInfoFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, notificationInfoFragment) // Đảm bảo ID chính xác
                .addToBackStack(null)
                .commit();
    }

    private void markNotificationAsRead(Notification notification) {
        if (!notification.isRead()) {
            if (!NetworkUtil.isWifiConnected(this.getContext())) {
                showErrorConnectDialog(); return;
            }
            notificationViewModel.updateFirebaseRead(notification.getID()); // Cập nhật trạng thái đã đọc
        }
    }

    public void setupObservers() {
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                notificationViewModel.fetchNotifications(user.getEmail(), parse);

                notificationViewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
                    notificationAdapter.updateNotifications(notifications);
                    UpdateNotificationLog();
                });

                notificationViewModel.getCountNotifiNotRead().observe(getViewLifecycleOwner(), count -> {
                    String logtemp = "Bạn có " + count + " thông báo mới!";
                    nofiLog.setText(logtemp);
                    nofiLog.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                });
            }
        });
    }

    private void UpdateNotificationLog() {
        if (notificationAdapter != null) {
            notificationAdapter.notifyDataSetChanged();
        }
    }

    private void MarkAllNotification() {

        if (!NetworkUtil.isWifiConnected(this.getContext())) {
            showErrorConnectDialog(); return;
        }
        notificationViewModel.markAllNotificationsAsRead();
        Toast.makeText(getContext(), "Đã đánh dấu tất cả thông báo là đã đọc.", Toast.LENGTH_LONG).show();
    }

    private void DeleteNotification() {
        if (!NetworkUtil.isWifiConnected(this.getContext())) {
            showErrorConnectDialog(); return;
        }
        notificationViewModel.deleteNotifications();
        Toast.makeText(getContext(), "Đã xóa các thông báo đã đọc.", Toast.LENGTH_SHORT).show();
    }

    private void showErrorConnectDialog() {
        String log = "Không có kết nối mạng. Vui lòng kiểm tra lại kết nối rồi thử lại sau!";
        ErrorDialog errorDialog = new ErrorDialog(log, false);
        errorDialog.show(getParentFragmentManager(), "errorConnectDialog");
    }
}
