package nhn.ntech.yummybee.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.CheckoutItemAdapter;
import nhn.ntech.yummybee.model.CartItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class OrderDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtDetailOrderId, txtDetailTotal, txtDetailAddress, txtDetailStatus;
    private RecyclerView rvOrderItems;
    private MainViewModel mainViewModel;
    private CheckoutItemAdapter checkoutItemAdapter;

    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);
        mapping();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        orderId = getIntent().getStringExtra("ORDER_ID");

        if (orderId == null) {
            Toast.makeText(this, "Lỗi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        initRecyclerView();

        observeOrderDetails(orderId);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initRecyclerView() {
        // Khởi tạo Adapter rỗng (chế độ chỉ đọc)
        checkoutItemAdapter = new CheckoutItemAdapter(new ArrayList<>());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvOrderItems.setLayoutManager(layoutManager);
        rvOrderItems.setAdapter(checkoutItemAdapter);

        // Thêm đường phân cách
        rvOrderItems.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        rvOrderItems.setNestedScrollingEnabled(false); // (Vì nó trong NestedScrollView)
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void observeOrderDetails(String id) {
        mainViewModel.loadOrderDetails(id).observe(this, orderItem -> {
            if (orderItem != null) {
                txtDetailOrderId.setText("Mã đơn: #" + orderItem.getOrderId().substring(0, 8).toUpperCase());
                txtDetailStatus.setText("Trạng thái: " + orderItem.getStatus());
                txtDetailTotal.setText("Tổng cộng: " +
                        String.format(Locale.getDefault(), "%,d đ", orderItem.getTotal_amount()));
                txtDetailAddress.setText(orderItem.getAddress());
                ArrayList<CartItem> items = convertMapToCartItem(orderItem.getItems());
                Log.d("OrderDetail", "Items:"+items);
                checkoutItemAdapter.setData(items);
            }
        });
    }

    private ArrayList<CartItem> convertMapToCartItem(List<Map<String, Object>> mapList) {
        if (mapList == null) return new ArrayList<>();

        ArrayList<CartItem> cartItems = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            CartItem item = new CartItem();
            item.setName((String) map.get("name"));
            item.setImageUrl((String) map.get("imageUrl"));
            item.setFood_id((String) map.get("food_id"));
            Long priceLong = (Long) map.get("price");
            Long quantityLong = (Long) map.get("quantity");

            // Kiểm tra Null an toàn trước khi gán
            if (priceLong != null) {
                item.setPrice(priceLong);
            }
            if (quantityLong != null) {
                item.setQuantity(quantityLong);
            }
            cartItems.add(item);
        }
        return cartItems;
    }

    private void mapping() {
        toolbar = findViewById(R.id.toolbar);
        rvOrderItems = findViewById(R.id.rvOrderItems);
        txtDetailOrderId = findViewById(R.id.txtDetailOrderId);
        txtDetailTotal = findViewById(R.id.txtDetailTotal);
        txtDetailAddress = findViewById(R.id.txtDetailAddress);
        txtDetailStatus = findViewById(R.id.txtDetailStatus);
    }
}