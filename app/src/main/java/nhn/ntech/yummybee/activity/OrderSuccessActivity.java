package nhn.ntech.yummybee.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import nhn.ntech.yummybee.R;

public class OrderSuccessActivity extends AppCompatActivity {
    private TextView txtOrderId;
    private AppCompatButton btnBackToHome;
    private String orderId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_success);
        mapping();

        if (getIntent().hasExtra("ORDER_ID")) {
            orderId = getIntent().getStringExtra("ORDER_ID");
            // Hiển thị 8 ký tự đầu của ID
            if (orderId != null){
                txtOrderId.setText("Mã đơn hàng: #" + orderId.substring(0, 8).toUpperCase());
            }
        }

        btnBackToHome.setOnClickListener(v -> {
            navigateToHome();
        });

    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mapping() {
        txtOrderId = findViewById(R.id.txtOrderId);
        btnBackToHome = findViewById(R.id.btnBackToHome);
    }
}