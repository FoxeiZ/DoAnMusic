package com.doan.music.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.doan.music.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class SignUpModel {
    private String username;
    private String password;
    private String email;

    public SignUpModel(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public SignUpModel() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

public class SignupActivity extends AppCompatActivity {

    private EditText signup_username, signup_email, signup_password;
    private FirebaseDatabase database;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signup_username = findViewById(R.id.signup_username);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);
        Button signup_button = findViewById(R.id.signup_button);
        TextView signup_redirect = findViewById(R.id.signup_redirect);

        Intent i = getIntent();
        String usernameExtra = i.getStringExtra("username");
        if (usernameExtra != null && !usernameExtra.isEmpty()) {
            signup_username.setText(usernameExtra);
        }

        signup_button.setOnClickListener(v -> {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");

            String username = signup_username.getText().toString();
            String email = signup_email.getText().toString();
            String password = signup_password.getText().toString();

            if (username.isEmpty()) {
                signup_username.setError("Username can not be empty");
                signup_username.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                signup_password.setError("Password can not be empty");
                signup_password.requestFocus();
                return;
            }

            SignUpModel model = new SignUpModel(username, password, email);
            reference.child(username).setValue(model);

            Toast.makeText(SignupActivity.this, "You have signup successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        signup_redirect.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }
}