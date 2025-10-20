package nhn.ntech.yummybee.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import nhn.ntech.yummybee.R;

public class RegisterActivity extends AppCompatActivity {

    private ScrollView scrollViewRegisterScreen;
    private AutoCompleteTextView genderDropdown, cityDropdown;
    private ArrayAdapter<String> genderAdapter, cityAdapter;
    private TextInputEditText edtFirstName, edtLastName, edtPhone, edtEmail, edtPassword, edtConfirmPassword, edtBirthday;
    private CheckBox securityCheckbox;
    private Button btnRegister;
    private Toolbar toolbar;
    private List<String> genders, cities;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        mapping();
        LoginActivity.setupUnfocusOnTouch(this, scrollViewRegisterScreen);
        genders = List.of("Nam", "Nữ");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, genders);
        genderDropdown.setAdapter(adapter);

        cities = List.of("Hà Nội", "TP. Hồ Chí Minh", "Đà Nẵng", "Cần Thơ", "Huế");
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        cityDropdown.setAdapter(cityAdapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
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
    }
}