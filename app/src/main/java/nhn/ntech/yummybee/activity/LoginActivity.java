package nhn.ntech.yummybee.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.manager.UserSessionManager;
import nhn.ntech.yummybee.model.UserItem;
import nhn.ntech.yummybee.viewmodel.AuthViewModel;
import nhn.ntech.yummybee.viewmodel.MainViewModel;

public class LoginActivity extends AppCompatActivity {

    private TextView txtRegister, txtForgotPassword;
    private ScrollView scrollView;
    private TextInputEditText edtEmail, edtPassword;
    private Button btnLogin, btnNonLogin;
    private AuthViewModel authViewModel;
    private MainViewModel mainViewModel;
    private UserSessionManager userSessionManager;

    @SuppressLint("ClickableViewAccessibility")
    public static void setupUnfocusOnTouch(Activity activity, View rootView) {
        rootView.setOnTouchListener((v, event) -> {
            // Chỉ xử lý khi người dùng chạm nhẹ (không cuộn)
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View currentFocus = activity.getCurrentFocus();
                if (currentFocus instanceof EditText) {
                    Rect outRect = new Rect();
                    currentFocus.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        currentFocus.clearFocus();
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                        }
                    }
                }
            }
            return false; // Cho phép ScrollView tiếp tục xử lý cuộn
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        mapping();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setupUnfocusOnTouch(this, scrollView);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        userSessionManager = new UserSessionManager(this);


        // Xử lý sự kiện khi bấm vào nút Login
        setBtnLogin();

        // Xử lý sự kiện khi bấm vào dòng text Đăng ký ngay
        setTxtRegister();

        // Xử lý sự kiện khi bấm vào dòng text Quên mật khẩu
        setTxtForgotPassword();

        // Xử lý sự kiện khi bấm vào nút Tiếp tục với tài khoản không đăng ký
        setBtnNonLogin();

    }

    private void setBtnLogin(){
        btnLogin.setOnClickListener(view -> {
            String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(edtPassword.getText()).toString().trim();
            authViewModel.login(email, password, new AuthViewModel.AuthCallBack() {
                @Override
                public void onSuccess() {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    mainViewModel.loadUserInfo(email).observe(LoginActivity.this, userItem -> {
                        if (userItem!=null){
                            userSessionManager.saveUserInfo(
                                    userItem.getFirstName(),
                                    userItem.getLastName(),
                                    userItem.getEmail(),
                                    userItem.getPhoneNumber(),
                                    userItem.getBirthday(),
                                    userItem.getBirthdayMillis(),
                                    userItem.getGender(),
                                    userItem.getCity(),
                                    userItem.isNotificationEnabled()
                            );
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setTxtRegister(){
        txtRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void setTxtForgotPassword(){
        txtForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void setBtnNonLogin(){
        btnNonLogin.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void mapping(){
        txtRegister = findViewById(R.id.txtRegister);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        scrollView = findViewById(R.id.main);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnNonLogin = findViewById(R.id.btnNonLogin);
    }
}