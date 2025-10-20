package nhn.ntech.yummybee.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import nhn.ntech.yummybee.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText edtEmail;
    private Button btnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        mapping();
        LoginActivity.setupUnfocusOnTouch(this, findViewById(R.id.main));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void mapping(){
        toolbar = findViewById(R.id.toolbar);
        edtEmail = findViewById(R.id.edtEmail);
        btnSend = findViewById(R.id.btnSend);
    }
}