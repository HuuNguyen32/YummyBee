package nhn.ntech.yummybee.activity;

import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;
import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.OrderHistoryAdapter;
import nhn.ntech.yummybee.model.OrderItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class OrderHistoryActivity extends AppCompatActivity implements OrderHistoryAdapter.OnOrderClickListener {

    private Toolbar toolbar;
    private RecyclerView rvOrderHistory;
    private TextView txtMessageEmpty;
    private MainViewModel mainViewModel;
    private OrderHistoryAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        mapping();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setupToolbar();
        initRecyclerView();

        // Kiểm tra đăng nhập
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            observeOrderHistory();

            observeSingleOrderDeleteStatus();
        } else {
            // Chưa đăng nhập
            txtMessageEmpty.setText("Vui lòng đăng nhập để xem lịch sử");
            txtMessageEmpty.setVisibility(View.VISIBLE);
            rvOrderHistory.setVisibility(View.GONE);
        }
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.baseline_more_vert_24));
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initRecyclerView() {
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(new ArrayList<>(), this);
        rvOrderHistory.setAdapter(adapter);
    }


    private void observeSingleOrderDeleteStatus() {
        mainViewModel.getSingleOrderDeleteStatus().observe(this, isSuccess -> {
            if (isSuccess == null) return;

            if (isSuccess) {
                Toast.makeText(this, "Đã xóa đơn hàng.", Toast.LENGTH_SHORT).show();
                // Gọi lại để cập nhật danh sách
                observeOrderHistory();
            } else {
                Toast.makeText(this, "Xóa đơn hàng thất bại.", Toast.LENGTH_SHORT).show();
            }
            mainViewModel.resetSingleOrderDeleteStatus();
        });
    }

    private void observeOrderHistory() {
        mainViewModel.loadOrderHistory().observe(this, orderItems -> {
            if (orderItems != null && !orderItems.isEmpty()) {
                rvOrderHistory.setVisibility(View.VISIBLE);
                txtMessageEmpty.setVisibility(View.GONE);
                adapter.setOrders(orderItems);
            } else {
                rvOrderHistory.setVisibility(View.GONE);
                txtMessageEmpty.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_all_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_delete_all){

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onOrderClick(OrderItem order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("ORDER_ID", order.getOrderId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(OrderItem order) {
        mainViewModel.deleteSingleOrder(order.getOrderId());
        Toast.makeText(this, "Đã xóa đơn hàng.", Toast.LENGTH_SHORT).show();
    }

    private void mapping() {
        toolbar = findViewById(R.id.toolbar);
        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        txtMessageEmpty = findViewById(R.id.txtMessageEmpty);
    }
}