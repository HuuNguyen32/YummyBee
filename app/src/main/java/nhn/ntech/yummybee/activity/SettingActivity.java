package nhn.ntech.yummybee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.divider.MaterialDivider;
import com.google.firebase.auth.FirebaseAuth;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.manager.UserSessionManager;
import nhn.ntech.yummybee.utils.DialogUtils;
import nhn.ntech.yummybee.viewmodel.AuthViewModel;

public class SettingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtDeleteAccount;
    private AuthViewModel authViewModel;
    private AppCompatButton btnLogout, btnLogin;
    private UserSessionManager userSessionManager;
    private MaterialDivider viewDividerDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userSessionManager = new UserSessionManager(this);
        mapping();
        updateLoginState();
        toolbar.setNavigationOnClickListener(view -> finish());
        btnLogout.setOnClickListener(view -> {
            DialogUtils.showCustomDialogBox(
                    this,
                    getString(R.string.app_name),
                    "Bạn chắc chắn muốn đăng xuất?",
                    false,
                    () -> {
                        authViewModel.logout();
                        userSessionManager.clearSession();
                        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
            );
        });
        setUpDeleteAccount();
    }

    private void updateLoginState() {
        boolean isLoggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
        btnLogin.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        btnLogout.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        txtDeleteAccount.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        viewDividerDeleteAccount.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);

        if (!isLoggedIn) {
            btnLogin.setOnClickListener(view ->
                    startActivity(new Intent(this, LoginActivity.class))
            );
        }
    }

    private void setUpDeleteAccount() {
        txtDeleteAccount.setOnClickListener(view -> {
            showDeleteAccountDialog();
        });
    }

    private void showDeleteAccountDialog() {
        DialogUtils.showCustomDialogBox(
                this,
                getString(R.string.app_name),
                "Bạn chắc chắn muốn xóa tài khoản?",
                true,
                ()->{
                    authViewModel.deleteAccount(DialogUtils.PasswordHolder.password, new AuthViewModel.DeleteCallback() {
                        @Override
                        public void onSuccess() {
                            userSessionManager.clearSession();
                            Toast.makeText(SettingActivity.this, "Tài khoản đã được xóa", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingActivity.this, LoginActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        );
    }

    private void mapping(){
        toolbar = findViewById(R.id.toolbar);
        txtDeleteAccount = findViewById(R.id.txtDeleteAccount);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogin = findViewById(R.id.btnLogin);
        viewDividerDeleteAccount = findViewById(R.id.viewDividerDeleteAccount);
    }
}