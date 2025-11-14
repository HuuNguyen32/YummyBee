package nhn.ntech.yummybee.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.adapter.AddressAdapter;
import nhn.ntech.yummybee.model.AddressItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.OnAddressSelectedListener {

    private RecyclerView listAddress;
    private Toolbar toolbar;
    private TextView txtMessageAddress;
    private AppCompatButton btnAddNewAddress;
    private MainViewModel mainViewModel;
    private AddressAdapter addressAdapter;

    public static final String MODE_KEY = "ADDRESS_MODE";
    public static final int MODE_PICKER = 1;
    public static final int MODE_MANAGER = 2;
    private int currentMode;
    private ActivityResultLauncher<Intent> addOrEditAddressLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mapping();
        currentMode = getIntent().getIntExtra(MODE_KEY, MODE_PICKER);

        addOrEditAddressLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(this, "Đã cập nhật danh sách", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        setupToolbar();
        initRecyclerView();
        // Bắt đầu lắng nghe dữ liệu
        observeAddressList();
        setupAddNewAddressButton();
    }

    private void setupAddNewAddressButton() {
        btnAddNewAddress.setOnClickListener(view -> {
            Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
            addOrEditAddressLauncher.launch(intent);
        });
    }

    private void initRecyclerView() {
        listAddress.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressAdapter(new ArrayList<>(), this);
        listAddress.setAdapter(addressAdapter);
        listAddress.setNestedScrollingEnabled(false);
    }

    private void observeAddressList() {
        mainViewModel.loadAddresses().observe(this, addressItems -> {
            if (addressItems != null && !addressItems.isEmpty()) {
                listAddress.setVisibility(View.VISIBLE);
                txtMessageAddress.setVisibility(View.GONE);
                addressAdapter.setAddresses(addressItems);
            } else {
                listAddress.setVisibility(View.GONE);
                txtMessageAddress.setVisibility(View.VISIBLE);
            }
        });
    }


    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(view -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }

    private void mapping(){
        listAddress = findViewById(R.id.listAddress);
        toolbar = findViewById(R.id.toolbar);
        txtMessageAddress = findViewById(R.id.txtMessageAddress);
        btnAddNewAddress = findViewById(R.id.btnAddNewAddress);
    }

    @Override
    public void onAddressSelected(AddressItem address) {
        if (currentMode == MODE_PICKER) {
            // Trả kết quả về cho CheckoutActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_ADDRESS", address);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();

        } else if (currentMode == MODE_MANAGER) {
            // Mở AddAddressActivity ở chế độ "Sửa"
            Intent intent = new Intent(this, AddAddressActivity.class);
            intent.putExtra("ADDRESS_TO_EDIT", address);
            addOrEditAddressLauncher.launch(intent);
        }
    }
}