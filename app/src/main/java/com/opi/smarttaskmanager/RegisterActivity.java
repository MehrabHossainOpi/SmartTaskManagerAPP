package com.opi.smarttaskmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etNewUsername;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private TextView tvRegError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvRegError = findViewById(R.id.tvRegError);

        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // closes this screen; the login screen is still waiting underneath
            }
        });
    }

    private void attemptRegister() {
        String username = etNewUsername.getText().toString().trim();
        String password = etNewPassword.getText().toString();
        String confirm = etConfirmPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all three fields.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            // Auth checks the other rules: no '|' character, username not already taken
            Session.getAuth().register(username, password);

            Toast.makeText(this, "Account created. You can now log in.", Toast.LENGTH_SHORT).show();
            finish();   // back to the login screen

        } catch (InvalidLoginException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        tvRegError.setText(message);
        tvRegError.setVisibility(View.VISIBLE);
    }
}