package com.opi.smarttaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final int MAX_ATTEMPTS = 3;

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvError;
    private Button btnLogin;
    private Button btnRegister;
    private int attempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // connect the Java variables to the views in activity_login.xml
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter your username and password.");
            return;
        }

        try {
            User user = Session.getAuth().login(username, password);
            Session.setCurrentUser(user);

            startActivity(new Intent(this, MainActivity.class));
            finish();   // remove the login screen so Back does not return to it

        } catch (InvalidLoginException e) {
            attempts++;
            etPassword.setText("");

            int left = MAX_ATTEMPTS - attempts;
            if (left <= 0) {
                btnLogin.setEnabled(false);
                showError("Too many failed attempts. Close and reopen the app to try again.");
            } else {
                showError(e.getMessage() + " Attempts left: " + left);
            }
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}