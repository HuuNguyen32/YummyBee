package nhn.ntech.yummybee.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.model.FoodItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;
import nhn.ntech.yummybee.viewmodel.SharedViewModel;

public class DetailFoodActivity extends AppCompatActivity {

    private ImageView imgFood, imgMinusQuantity, imgAddQuantity;
    private TextView txtFoodName, txtFoodDescription, txtTotalPrice, txtFoodPrice;
    private EditText edtQuantity;
    private ImageButton btnBack, btnLike;
    private AppCompatButton btnAddToCart, btnBuyNow;

    private MainViewModel mainViewModel;

    private String foodId;
    private FoodItem currentFoodItem; // Lưu trữ món ăn hiện tại
    private boolean isCurrentlyFavorite = false; // Biến theo dõi trạng thái
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> loginLauncher;
    private Runnable pendingAction = null;
    private boolean isUserTriggeredFavoriteChange = false;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_food);
        mapping();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        // Nút back
        btnBack.setOnClickListener(view -> finish());

        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // HÀM NÀY CHẠY KHI LOGINACTIVITY ĐÓNG LẠI
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Người dùng vừa đăng nhập thành công!
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        // Cập nhật lại Auth
                        mAuth = FirebaseAuth.getInstance();

                        // Tải lại trạng thái like
                        observeFavoriteStatus(foodId);

                        // THỰC HIỆN HÀNH ĐỘNG ĐANG CHỜ (Tự động click)
                        if (pendingAction != null) {
                            pendingAction.run();
                            pendingAction = null; // Xóa hành động chờ
                        }

                    } else {
                        // Người dùng nhấn Back (hủy đăng nhập)
                        Toast.makeText(this, "Bạn đã hủy đăng nhập", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Lấy dữ liệu đẩy lên view
        FoodItem foodItem = (FoodItem) getIntent().getSerializableExtra("foodItem");
        if (foodItem == null || foodItem.getId() == null) {
            Toast.makeText(this, "Không tìm thấy món ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        this.currentFoodItem = foodItem;
        this.foodId = foodItem.getId();

        Glide.with(this)
                .load(foodItem.getImageUrl())
                .apply(new RequestOptions().transform(new CenterInside()))
                .into(imgFood);
        txtFoodName.setText(foodItem.getName());
        txtFoodPrice.setText(String.format(Locale.getDefault(), "%,d đ", foodItem.getPrice()));
        txtFoodDescription.setText(foodItem.getDescription());
        txtTotalPrice.setText(String.format(Locale.getDefault(), "%,d đ", foodItem.getPrice()));

        imgAddQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = getQuantity(edtQuantity);
                currentQuantity++;
                edtQuantity.setText(String.valueOf(currentQuantity));
                updateTotalPrice(currentQuantity, foodItem.getPrice());
            }
        });

        imgMinusQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = getQuantity(edtQuantity);
                if (currentQuantity > 1) {
                    currentQuantity--;
                    edtQuantity.setText(String.valueOf(currentQuantity));
                    updateTotalPrice(currentQuantity, foodItem.getPrice());
                } else {
                    Toast.makeText(DetailFoodActivity.this, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
                }

            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // (Chỉ lắng nghe trạng thái Yêu thích nếu đã đăng nhập)
            observeFavoriteStatus(foodId);
        } else {
            // (Nếu là khách, luôn hiển thị icon trái tim rỗng)
            btnLike.setImageResource(R.drawable.ic_heart_outline);
        }

        // Cài đặt listeners cho các nút hành động
        setupActionListeners();

    }

    private void showLoginPrompt(Runnable action) {
        pendingAction = action;
        Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);

        // Dùng Launcher
        loginLauncher.launch(intent);
    }

    private void setupActionListeners() {
        btnLike.setOnClickListener(v -> {
            if (currentFoodItem == null) return;

            // KIỂM TRA: Nếu chưa đăng nhập -> Chuyển sang Login
            if (mAuth.getCurrentUser() == null) {
                showLoginPrompt(() -> mainViewModel.toggleFavorite(currentFoodItem, isCurrentlyFavorite));
                return; // Dừng hành động
            }
            isUserTriggeredFavoriteChange = true;
            mainViewModel.toggleFavorite(currentFoodItem, isCurrentlyFavorite);
            observeFavoriteStatus(foodId);
        });

        btnAddToCart.setOnClickListener(v -> {
            if (currentFoodItem == null) return;

            if (mAuth.getCurrentUser() == null) {
                showLoginPrompt(pendingAction);
                return;
            }

            int quantity = getQuantity(edtQuantity);
            mainViewModel.addToCart(currentFoodItem, quantity);
            Toast.makeText(DetailFoodActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        });

        btnBuyNow.setOnClickListener(v -> {
            // (Tương tự như AddToCart, nhưng sau đó điều hướng đến CartActivity)
        });
    }

    private void observeFavoriteStatus(String foodId) {
        mainViewModel.getFavoriteStatus(foodId).observe(this, isFavorite -> {
            // Cập nhật biến trạng thái toàn cục
            isCurrentlyFavorite = isFavorite;

            if (isFavorite) {
                // Đã yêu thích
                btnLike.setImageResource(R.drawable.ic_heart_filled);
                if (isUserTriggeredFavoriteChange) {
                    Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Chưa yêu thích
                btnLike.setImageResource(R.drawable.ic_heart_outline);
                if (isUserTriggeredFavoriteChange) {
                    Toast.makeText(this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                }
            }
            isUserTriggeredFavoriteChange = false;
        });
    }

    private void updateTotalPrice(int quantity, long foodPrice) {
        long total = quantity * foodPrice;
        TextView txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtTotalPrice.setText(String.format(Locale.getDefault(), "%,d đ", total));
    }

    private int getQuantity(EditText edtQuantity) {
        String text = edtQuantity.getText().toString().trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 1; // Mặc định nếu lỗi
        }
    }

    private void mapping(){
        imgFood = findViewById(R.id.imgFood);
        btnBack = findViewById(R.id.btnBack);
        btnLike = findViewById(R.id.btnLike);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodDescription = findViewById(R.id.txtFoodDescription);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        imgMinusQuantity = findViewById(R.id.imgMinusQuantity);
        imgAddQuantity = findViewById(R.id.imgAddQuantity);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtFoodPrice = findViewById(R.id.txtFoodPrice);
    }
}