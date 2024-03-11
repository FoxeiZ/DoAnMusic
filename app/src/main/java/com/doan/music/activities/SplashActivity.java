package com.doan.music.activities;

import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.doan.music.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1000;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().post(() -> {
            if (ContextCompat.checkSelfPermission(this, READ_MEDIA_AUDIO) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{READ_MEDIA_IMAGES, READ_MEDIA_AUDIO}, REQUEST_PERMISSION_CODE);
            } else {
                checkLoginAndStart();
            }
        });
    }

    private void checkLoginAndStart() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("IsLogin", false)) {
            String username = sharedPref.getString("Username", "");
            String password = sharedPref.getString("Password", "");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            Query checkUserDatabase = reference.orderByChild("username").equalTo(username);

            checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String passwordFromDB = snapshot.child(username).child("password").getValue(String.class);
                        Intent i;
                        if (passwordFromDB != null && passwordFromDB.equals(password)) {
                            i = new Intent(SplashActivity.this, MainActivity.class);
                        } else {
                            i = new Intent(SplashActivity.this, LoginActivity.class);
                        }
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLoginAndStart();
            } else {
                Toast.makeText(this, "Permission Denied. Please granted to use the app.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}