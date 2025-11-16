package nhn.ntech.yummybee.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.NotificationAdapter;
import nhn.ntech.yummybee.utils.DialogUtils;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class NotificationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvNotifications;
    private TextView txtMessageEmpty;
    private NotificationAdapter adapter;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        mapping();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setupToolbar();
        initRecyclerView();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            // Bắt đầu tải dữ liệu ngay lập tức
            loadNotifications();
            observeDeleteAllNotifications();
        }
        else {
            txtMessageEmpty.setText("Vui lòng đăng nhập để xem thông báo");
            txtMessageEmpty.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.baseline_more_vert_24));
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void initRecyclerView() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(new ArrayList<>());
        rvNotifications.setAdapter(adapter);
    }


    private void loadNotifications() {
        mainViewModel.loadNotifications().observe(this, notificationItems -> {
            if (notificationItems != null && !notificationItems.isEmpty()) {
                rvNotifications.setVisibility(View.VISIBLE);
                txtMessageEmpty.setVisibility(View.GONE);
                adapter.setNotifications(notificationItems);
            } else {
                rvNotifications.setVisibility(View.GONE);
                txtMessageEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void observeDeleteAllNotifications(){
        mainViewModel.getNotificationClearStatus().observe(this, aBoolean -> {
            if (aBoolean){
                Toast.makeText(this, "Đã xóa tất cả thông báo.", Toast.LENGTH_SHORT).show();
                loadNotifications();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_all_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_delete_all){
            DialogUtils.showCustomDialogBox(
                    this,
                    "Xóa tất cả thông báo",
                    "Bạn có chắc chắn muốn xóa tất cả thông báo?",
                    false,
                    () -> {
                        mainViewModel.clearNotifications();
                    }
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void mapping() {
        toolbar = findViewById(R.id.toolbar);
        rvNotifications = findViewById(R.id.rvNotifications);
        txtMessageEmpty = findViewById(R.id.txtMessageEmpty);
    }
}