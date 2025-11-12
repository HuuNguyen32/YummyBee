package nhn.ntech.yummybee.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.manager.UserSessionManager;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class EditProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ScrollView scrollView;
    private TextInputLayout textInputLayoutFirstName, textInputLayoutLastName, textInputLayoutPhone, textInputLayoutBirthday, textInputLayoutEmail;
    private TextInputEditText edtFirstName, edtLastName, edtPhone, edtBirthday, edtEmail;
    private AutoCompleteTextView genderDropdown, cityDropdown;
    private UserSessionManager userSessionManager;
    private AppCompatButton btnUpdate;
    private Long selectedBirthdayMillis = null;
    private MainViewModel mainViewModel;
    private List<String> genders, cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mapping();
        LoginActivity.setupUnfocusOnTouch(this, scrollView);
        initData();
        setUpDropDown();

        changeEdtBirthday();
        closeEditProfile();
        updateProfile();
        observeUpdateStatus();
    }

    private void setUpDropDown() {
        // Setup Dropdowns
        genders = List.of("Nam", "Nữ");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, genders);
        genderDropdown.setAdapter(adapter);

        cities = List.of(
                "Hà Nội", "Hải Phòng", "Hà Giang", "Cao Bằng", "Bắc Kạn", "Tuyên Quang", "Lào Cai", "Yên Bái",
                "Thái Nguyên", "Phú Thọ", "Bắc Giang", "Quảng Ninh", "Lạng Sơn",
                "Bắc Ninh", "Hà Nam", "Hải Dương", "Hưng Yên", "Nam Định", "Ninh Bình", "Thái Bình", "Vĩnh Phúc",
                "Thanh Hóa"
        );
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        cityDropdown.setAdapter(cityAdapter);
    }

    private void changeEdtBirthday() {
        textInputLayoutBirthday.setEndIconOnClickListener(view -> showDatePickerDialog());
        edtBirthday.setFocusable(false);
        edtBirthday.setClickable(true);
        edtBirthday.setOnClickListener(v -> showDatePickerDialog());
    }

    private void initData() {
        userSessionManager = new UserSessionManager(this);
        edtFirstName.setText(userSessionManager.getFirstName());
        edtLastName.setText(userSessionManager.getLastName());
        edtPhone.setText(userSessionManager.getPhoneNumber());
        edtBirthday.setText(userSessionManager.getBirthday());
        edtEmail.setText(userSessionManager.getEmail());
        genderDropdown.setText(userSessionManager.getGender());
        cityDropdown.setText(userSessionManager.getCity());
        selectedBirthdayMillis = userSessionManager.getBirthdayMillis();
    }


    private void updateProfile() {
        btnUpdate.setOnClickListener(view -> {
            String firstName = Objects.requireNonNull(edtFirstName.getText()).toString().trim();
            String lastName = Objects.requireNonNull(edtLastName.getText()).toString().trim();
            String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
            String phone = Objects.requireNonNull(edtPhone.getText()).toString().trim();
            String birthday = Objects.requireNonNull(edtBirthday.getText()).toString().trim();
            String gender = genderDropdown.getText().toString().trim();
            String city = cityDropdown.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || birthday.isEmpty() || gender.isEmpty() || city.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vô hiệu hóa nút để tránh click đúp
            btnUpdate.setEnabled(false);
            btnUpdate.setText(getString(R.string.is_updating));

            Map<String, Object> updates = new HashMap<>();
            updates.put("firstName", firstName);
            updates.put("lastName", lastName);
            updates.put("phoneNumber", phone);
            updates.put("birthday", birthday);
            updates.put("birthdayMillis", selectedBirthdayMillis);
            updates.put("gender", gender);
            updates.put("city", city);

            mainViewModel.updateUserProfile(updates);
        });
    }

    private void observeUpdateStatus() {
        mainViewModel.getProfileUpdateStatus().observe(this, isSuccess -> {
            if (isSuccess == null) return; // Trạng thái ban đầu (chưa cập nhật)

            if (isSuccess) {
                // Cập nhật Session Cục bộ (với dữ liệu MỚI)
                userSessionManager.saveUserInfo(
                        Objects.requireNonNull(edtFirstName.getText()).toString().trim(),
                        Objects.requireNonNull(edtLastName.getText()).toString().trim(),
                        Objects.requireNonNull(edtEmail.getText()).toString().trim(),
                        Objects.requireNonNull(edtPhone.getText()).toString().trim(),
                        Objects.requireNonNull(edtBirthday.getText()).toString().trim(),
                        selectedBirthdayMillis,
                        genderDropdown.getText().toString().trim(),
                        cityDropdown.getText().toString().trim()
                );

                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                mainViewModel.resetProfileUpdateStatus(); // Reset lại
                finish(); // Đóng màn hình Edit

            } else {
                // CẬP NHẬT FIRESTORE THẤT BẠI
                Toast.makeText(this, "Cập nhật thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();

                // Bật lại nút
                btnUpdate.setEnabled(true);
                btnUpdate.setText(getString(R.string.update));
                mainViewModel.resetProfileUpdateStatus(); // Reset lại
            }
        });
    }

    private void showDatePickerDialog(){
        long defaultSelection = selectedBirthdayMillis != null
                ? selectedBirthdayMillis
                : MaterialDatePicker.todayInUtcMilliseconds();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(defaultSelection)
                .build();

        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedBirthdayMillis = selection; // Lưu lại giá trị đã chọn
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String date = sdf.format(new Date(selection));
            edtBirthday.setText(date);
        });

    }

    private void closeEditProfile() {
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void mapping(){
        toolbar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scrollViewEditProfile);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtPhone = findViewById(R.id.edtPhone);
        edtBirthday = findViewById(R.id.edtBirthday);
        edtEmail = findViewById(R.id.edtEmail);
        genderDropdown = findViewById(R.id.genderDropdown);
        cityDropdown = findViewById(R.id.cityDropdown);
        btnUpdate = findViewById(R.id.btnUpdate);
        textInputLayoutFirstName = findViewById(R.id.textInputLayoutFirstName);
        textInputLayoutLastName = findViewById(R.id.textInputLayoutLastName);
        textInputLayoutPhone = findViewById(R.id.textInputLayoutPhone);
        textInputLayoutBirthday = findViewById(R.id.textInputLayoutBirthday);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
    }


}