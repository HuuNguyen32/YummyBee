package nhn.ntech.yummybee.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.model.AddressItem;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class AddAddressActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtToolbarTitle;
    private TextInputEditText edtFullName, edtPhone, edtStreet, edtWard, edtDistrict, edtCity;
    private SwitchMaterial switchSetDefault;
    private AppCompatButton btnSaveAddress;
    private MainViewModel mainViewModel;
    private AddressItem addressToEdit;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_address);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mapping();
        LoginActivity.setupUnfocusOnTouch(this, scrollView);
        setupToolbar();

        if (getIntent().hasExtra("ADDRESS_TO_EDIT")) {
            addressToEdit = (AddressItem) getIntent().getSerializableExtra("ADDRESS_TO_EDIT");
            txtToolbarTitle.setText(getString(R.string.update_address));
            btnSaveAddress.setText(getString(R.string.update));
            fillDataForEdit();
        } else {
            addressToEdit = null;
            txtToolbarTitle.setText(getString(R.string.add_new_address));
            btnSaveAddress.setText(getString(R.string.save_address));
        }

        // Nút Lưu
        btnSaveAddress.setOnClickListener(v -> {
            saveAddress();
        });

        // Lắng nghe kết quả từ ViewModel
        observeSaveStatus();
    }

    private void fillDataForEdit() {
        if (addressToEdit == null) return;
        edtFullName.setText(addressToEdit.getFullName());
        edtPhone.setText(addressToEdit.getPhone());
        edtStreet.setText(addressToEdit.getStreet());
        edtWard.setText(addressToEdit.getWard());
        edtDistrict.setText(addressToEdit.getDistrict());
        edtCity.setText(addressToEdit.getCity());
        switchSetDefault.setChecked(addressToEdit.isDefault());
    }

    private void observeSaveStatus() {
        mainViewModel.getAddressSaveStatus().observe(this, isSuccess -> {

            if (isSuccess == null) {
                return; // Trạng thái ban đầu (đang chờ reset)
            }

            if (isSuccess) {
                Toast.makeText(this, "Lưu địa chỉ thành công!", Toast.LENGTH_SHORT).show();
                mainViewModel.resetAddressSaveStatus(); // Reset lại LiveData
                finish(); // Đóng Activity

            } else {
                Toast.makeText(this, "Lỗi khi lưu địa chỉ. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                // Bật lại nút
                btnSaveAddress.setEnabled(true);
                if (addressToEdit != null) {
                    btnSaveAddress.setText(getString(R.string.update));
                } else {
                    btnSaveAddress.setText(getString(R.string.save_address));
                }
                mainViewModel.resetAddressSaveStatus(); // Reset lại LiveData
            }
        });
    }

    private void saveAddress() {
        // Lấy dữ liệu
        String fullName = Objects.requireNonNull(edtFullName.getText()).toString().trim();
        String phone = Objects.requireNonNull(edtPhone.getText()).toString().trim();
        String street = Objects.requireNonNull(edtStreet.getText()).toString().trim();
        String ward = Objects.requireNonNull(edtWard.getText()).toString().trim();
        String district = Objects.requireNonNull(edtDistrict.getText()).toString().trim();
        String city = Objects.requireNonNull(edtCity.getText()).toString().trim();
        boolean isDefault = switchSetDefault.isChecked();

        // Kiểm tra rỗng
        if (fullName.isEmpty() || phone.isEmpty() || street.isEmpty() || city.isEmpty() || district.isEmpty() || ward.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tắt nút để tránh click đúp
        btnSaveAddress.setEnabled(false);
        btnSaveAddress.setText(getString(R.string.is_saving));

        // Tạo đối tượng AddressItem
        AddressItem newAddress = new AddressItem(fullName, phone, street, ward, district, city, isDefault);

        // Gọi ViewModel để lưu
        if (addressToEdit != null) {
            mainViewModel.updateAddress(addressToEdit.getId(), newAddress);
        } else {
            mainViewModel.saveNewAddress(newAddress);
        }
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(view -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }

    private void mapping(){
        toolbar = findViewById(R.id.toolbar);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtStreet = findViewById(R.id.edtStreet);
        edtWard = findViewById(R.id.edtWard);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtCity = findViewById(R.id.edtCity);
        switchSetDefault = findViewById(R.id.switchSetDefault);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
        txtToolbarTitle = findViewById(R.id.txtTitleToolbar);
        scrollView = findViewById(R.id.scrollViewAddAddress);
    }
}