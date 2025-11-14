package nhn.ntech.yummybee.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.CheckoutItemAdapter;
import nhn.ntech.yummybee.model.AddressItem;
import nhn.ntech.yummybee.model.CartItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class CheckOutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtCheckoutAddressName, txtCheckoutAddressFull;
    private LinearLayout layoutChangeAddress;
    private MainViewModel mainViewModel;
    private TextView txtPriceSubtotal, txtPriceDelivery, txtPriceTax, txtPriceTotal;
    private AppCompatButton btnPlaceOrder;
    private LinearLayout layoutCod, layoutPayNow;
    private ImageView imgCodCheck, imgPayNowCheck;
    private AddressItem currentSelectedAddress;
    private ArrayList<CartItem> cartItems;
    private long totalAmount = 0;
    private String selectedPaymentMethod = "COD"; // Mặc định là COD
    private RecyclerView rvCheckoutItems;
    private CheckoutItemAdapter checkoutItemAdapter;
    private ActivityResultLauncher<Intent> addressPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);
        mapping();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // (Code đăng ký addressPickerLauncher của bạn giữ nguyên)
        addressPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        AddressItem newAddress = (AddressItem) result.getData().getSerializableExtra("SELECTED_ADDRESS");
                        if (newAddress != null) {
                            currentSelectedAddress = newAddress;
                            displayAddress(currentSelectedAddress);
                        }
                    }
                }
        );

        // Tải dữ liệu khi mở
        loadCartSummaryFromIntent();
        loadDefaultAddress();
        initRecyclerView();

        setupToolbar();
        setupAddressListener();
        setupPaymentSelection();
        setupPlaceOrderButton();

        // Lắng nghe kết quả Đặt hàng
        observeOrderStatus();
    }

    private void initRecyclerView() {
        if (cartItems == null) return;

        // Khởi tạo Adapter ĐƠN GIẢN
        checkoutItemAdapter = new CheckoutItemAdapter(cartItems);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvCheckoutItems.setLayoutManager(layoutManager);
        rvCheckoutItems.setAdapter(checkoutItemAdapter);

        // Thêm đường phân cách (Divider)
        DividerItemDecoration divider = new DividerItemDecoration(
                rvCheckoutItems.getContext(),
                layoutManager.getOrientation()
        );
        rvCheckoutItems.addItemDecoration(divider);

        // BẮT BUỘC: Tắt cuộn lồng (vì nó nằm trong NestedScrollView)
        rvCheckoutItems.setNestedScrollingEnabled(false);
    }

    private void selectPaymentMethod(boolean isCodSelected) {
        if (isCodSelected) {
            selectedPaymentMethod = "COD";
            // Cập nhật UI:
            layoutCod.setBackgroundResource(R.drawable.payment_background_selected);
            imgCodCheck.setVisibility(View.VISIBLE);
            layoutPayNow.setBackgroundResource(R.drawable.payment_background_default);
            imgPayNowCheck.setVisibility(View.GONE);

        } else {
            selectedPaymentMethod = "PAY_NOW";
            // Cập nhật UI:
            layoutCod.setBackgroundResource(R.drawable.payment_background_default);
            imgCodCheck.setVisibility(View.GONE);
            layoutPayNow.setBackgroundResource(R.drawable.payment_background_selected);
            imgPayNowCheck.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("unchecked") // bỏ qua báo lỗi
    private void loadCartSummaryFromIntent() {
        cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("CART_LIST_DATA");

        if (cartItems != null && !cartItems.isEmpty()) {
            long subtotal = 0;
            for (CartItem item : cartItems) {
                subtotal += item.getPrice() * item.getQuantity();
            }
            long shippingFee = (subtotal > 0) ? 25000 : 0; // Phí ship cố định
            long tax = (long) (subtotal * 0.08); // Thuế 8%
            totalAmount = subtotal + shippingFee + tax; // Lưu tổng tiền

            // Cập nhật UI Tóm tắt
            txtPriceSubtotal.setText(String.format(Locale.getDefault(), "%,d đ", subtotal));
            txtPriceDelivery.setText(String.format(Locale.getDefault(), "%,d đ", shippingFee));
            txtPriceTax.setText(String.format(Locale.getDefault(), "%,d đ", tax));
            txtPriceTotal.setText(String.format(Locale.getDefault(), "%,d đ", totalAmount));
        } else {
            // Nếu giỏ hàng rỗng, đóng Activity
            Toast.makeText(this, "Giỏ hàng rỗng!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadDefaultAddress() {
        mainViewModel.loadAddresses().observe(this, addressItems -> {
            if (addressItems != null && !addressItems.isEmpty()) {
                currentSelectedAddress = addressItems.get(0);
                displayAddress(currentSelectedAddress);
            } else {
                txtCheckoutAddressName.setText("Chưa có địa chỉ");
                txtCheckoutAddressFull.setText("Vui lòng thêm địa chỉ mới");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayAddress(AddressItem address) {
        txtCheckoutAddressName.setText(address.getFullName() + " | " + address.getPhone());
        String fullAddress = address.getStreet() + ", " + address.getWard() + ", " + address.getDistrict() + ", " + address.getCity();
        txtCheckoutAddressFull.setText(fullAddress);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupAddressListener() {
        layoutChangeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressActivity.class);
            intent.putExtra(AddressActivity.MODE_KEY, AddressActivity.MODE_PICKER);
            addressPickerLauncher.launch(intent);
        });
    }

    private void setupPaymentSelection() {
        selectPaymentMethod(true);

        layoutCod.setOnClickListener(v -> {
            selectPaymentMethod(true); // true = COD
        });

        layoutPayNow.setOnClickListener(v -> {
            selectPaymentMethod(false); // false = Pay Now
        });
    }

    private void setupPlaceOrderButton() {
        btnPlaceOrder.setOnClickListener(v -> {
            if (currentSelectedAddress == null) {
                Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cartItems == null || cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng rỗng", Toast.LENGTH_SHORT).show();
                return;
            }

            btnPlaceOrder.setEnabled(false);
            btnPlaceOrder.setText("Đang xử lý...");

            if (selectedPaymentMethod.equals("COD")) {
                mainViewModel.placeOrder(cartItems, currentSelectedAddress, totalAmount, selectedPaymentMethod);

            } else if (selectedPaymentMethod.equals("PAY_NOW")) {
                processOnlinePayment();
            }
        });
    }

    private void processOnlinePayment() {
        Toast.makeText(this, "Chuyển đến cổng thanh toán (Chưa tích hợp)...", Toast.LENGTH_SHORT).show();
        mainViewModel.placeOrder(cartItems, currentSelectedAddress, totalAmount, selectedPaymentMethod);
    }

    private void observeOrderStatus() {
        mainViewModel.getOrderStatus().observe(this, orderId -> {
            if (orderId != null) {
                // Đặt hàng THÀNH CÔNG
                Toast.makeText(this, "Đặt hàng thành công! Mã đơn: " + orderId, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, OrderSuccessActivity.class);
                intent.putExtra("ORDER_ID", orderId);
                startActivity(intent);
                finish();

            } else if (mainViewModel.getOrderStatus().getValue() != null) {

            } else {
                Toast.makeText(this, "Đặt hàng thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                btnPlaceOrder.setEnabled(true);
                btnPlaceOrder.setText(getString(R.string.order_now));
            }
        });
    }

    private void mapping() {
        toolbar = findViewById(R.id.toolbar);
        layoutChangeAddress = findViewById(R.id.layoutChangeAddress);
        txtCheckoutAddressName = findViewById(R.id.txtCheckoutAddressName);
        txtCheckoutAddressFull = findViewById(R.id.txtCheckoutAddressFull);

        // (Ánh xạ các View Tóm tắt)
        View summaryView = findViewById(R.id.summaryView);
        txtPriceSubtotal = summaryView.findViewById(R.id.txtPriceSubtotal);
        txtPriceDelivery = summaryView.findViewById(R.id.txtPriceDelivery);
        txtPriceTax = summaryView.findViewById(R.id.txtPriceTax);
        txtPriceTotal = summaryView.findViewById(R.id.txtPriceTotal);

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        layoutCod = findViewById(R.id.layoutCod);
        layoutPayNow = findViewById(R.id.layoutPayNow);
        imgCodCheck = findViewById(R.id.imgCodCheck);
        imgPayNowCheck = findViewById(R.id.imgPayNowCheck);
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
    }
}