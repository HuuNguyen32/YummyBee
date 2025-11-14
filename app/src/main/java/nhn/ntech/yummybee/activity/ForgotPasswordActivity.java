package nhn.ntech.yummybee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import nhn.ntech.yummybee.R;
import nhn.ntech.yummybee.viewmodel.AuthViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText edtEmail;
    private Button btnSend;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        mapping();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        LoginActivity.setupUnfocusOnTouch(this, findViewById(R.id.main));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().setLanguageCode("vi");
                String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
                authViewModel.resetPassword(email, new AuthViewModel.AuthCallBack() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ForgotPasswordActivity.this, "Gửi mail thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, ConfirmSendingActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void mapping(){
        toolbar = findViewById(R.id.toolbar);
        edtEmail = findViewById(R.id.edtEmail);
        btnSend = findViewById(R.id.btnSend);
    }
}