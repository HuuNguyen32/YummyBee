package nhn.ntech.yummybee.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern; // Import Pattern

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.manager.UserSessionManager;
import nhn.ntech.yummybee.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ScrollView scrollViewRegisterScreen;
    private AutoCompleteTextView genderDropdown, cityDropdown;
    private TextInputEditText edtFirstName, edtLastName, edtPhone, edtEmail, edtPassword, edtConfirmPassword, edtBirthday;

    private TextInputLayout textInputLayoutFirstName, textInputLayoutLastName, textInputLayoutPhone, textInputLayoutEmail, textInputLayoutPassword, textInputLayoutConfirmPassword, textInputLayoutBirthday;

    private CheckBox securityCheckbox;
    private Button btnRegister;
    private Toolbar toolbar;
    private List<String> genders, cities;

    private AuthViewModel authViewModel;
    private Long selectedBirthdayMillis = null;
    private  UserSessionManager session;


    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mapping(); // Ánh xạ

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        LoginActivity.setupUnfocusOnTouch(this, scrollViewRegisterScreen);

        session = new UserSessionManager(this);

        // Setup Dropdowns
        genders = Arrays.asList(getResources().getStringArray(R.array.gender_options));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, genders);
        genderDropdown.setAdapter(adapter);

        cities = Arrays.asList(getResources().getStringArray(R.array.city_options));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        cityDropdown.setAdapter(cityAdapter);

        // Setup Toolbar
        toolbar.setNavigationOnClickListener(view -> finish());

        setEdtBirthday();

        setBtnRegister();
    }

    private void setEdtBirthday(){
        textInputLayoutBirthday.setEndIconOnClickListener(view -> showDatePickerDialog());
        edtBirthday.setFocusable(false);
        edtBirthday.setClickable(true);
        edtBirthday.setOnClickListener(v -> showDatePickerDialog());
    }
    private void setBtnRegister(){
        btnRegister.setOnClickListener(view -> {
            // Lấy dữ liệu MỚI NHẤT khi click
            String firstname = Objects.requireNonNull(edtFirstName.getText()).toString().trim();
            String lastname = Objects.requireNonNull(edtLastName.getText()).toString().trim();
            String phone = Objects.requireNonNull(edtPhone.getText()).toString().trim();
            String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(edtPassword.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(edtConfirmPassword.getText()).toString().trim();
            String birthday = Objects.requireNonNull(edtBirthday.getText()).toString().trim();
            String gender = genderDropdown.getText().toString().trim();
            String city = cityDropdown.getText().toString().trim();

            // Thực hiện Validation
            if (firstname.isEmpty() || lastname.isEmpty() || phone.isEmpty() || email.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty() || birthday.isEmpty()
                    || gender.isEmpty() || city.isEmpty()){
                Toast.makeText(RegisterActivity.this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
                return;
            }
            if (firstname.length() < 2 || lastname.length() < 2){
                Toast.makeText(RegisterActivity.this, getString(R.string.error_name_length), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidEmail(email)){
                Toast.makeText(RegisterActivity.this, getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6){
                Toast.makeText(RegisterActivity.this, getString(R.string.error_password_length), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidPhoneNumber(phone)){
                Toast.makeText(RegisterActivity.this, getString(R.string.error_invalid_phone), Toast.LENGTH_SHORT).show();
                return;
            }

            if(selectedBirthdayMillis == null){
                Toast.makeText(RegisterActivity.this, getString(R.string.error_select_birthday), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isAdult(selectedBirthdayMillis)) {
                Toast.makeText(RegisterActivity.this, getString(R.string.error_underage), Toast.LENGTH_LONG).show();
                return;
            }

            // Kiểm tra Checkbox
            if (!securityCheckbox.isChecked()) {
                Toast.makeText(RegisterActivity.this, getString(R.string.error_terms_unchecked), Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi ViewModel
            authViewModel.register(firstname, lastname, email, password, confirmPassword, phone, birthday, selectedBirthdayMillis, gender, city, new AuthViewModel.AuthCallBack() {
                @Override
                public void onSuccess() {
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    session.saveUserInfo(
                            firstname,
                            lastname,
                            email,
                            phone,
                            birthday,
                            selectedBirthdayMillis,
                            gender,
                            city,
                            true
                    );

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public static boolean isAdult(long birthdayMillis) {
        final int MINIMUM_AGE = 12;

        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(birthdayMillis);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age >= MINIMUM_AGE;
    }

    public boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        String phonePattern = "^(0|\\+84)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-6|8|9]|9[0-9])[0-9]{7}$";
        return phoneNumber != null && phoneNumber.matches(phonePattern);
    }

    private void showDatePickerDialog(){
        long defaultSelection = selectedBirthdayMillis != null
                ? selectedBirthdayMillis
                : MaterialDatePicker.todayInUtcMilliseconds();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.birthday_picker_title))
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

    private void mapping(){
        scrollViewRegisterScreen = findViewById(R.id.scrollViewRegisterScreen);
        genderDropdown = findViewById(R.id.genderDropdown);
        cityDropdown = findViewById(R.id.cityDropdown);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtBirthday = findViewById(R.id.edtBirthday);
        securityCheckbox = findViewById(R.id.securityCheckbox);
        btnRegister = findViewById(R.id.btnRegister);
        toolbar = findViewById(R.id.toolbar);
        textInputLayoutBirthday = findViewById(R.id.textInputLayoutBirthday);
        textInputLayoutFirstName = findViewById(R.id.textInputLayoutFirstName);
        textInputLayoutLastName = findViewById(R.id.textInputLayoutLastName);
        textInputLayoutPhone = findViewById(R.id.textInputLayoutPhone);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);
    }
}