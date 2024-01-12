package com.example.prochatver1.Acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.prochatver1.databinding.ActivitySingUpBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SingUp extends AppCompatActivity {
ActivitySingUpBinding binding;
private static final int SMS_PERMISSION_REQUEST_CODE = 123;
FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Request SMS permissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.READ_SMS}, SMS_PERMISSION_REQUEST_CODE);
        } else {
            // SMS permissions are already granted
            // You can proceed with Firebase Phone Authentication or any SMS-related functionality
        }
        binding.sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingUp.this, OTP.class);
                intent.putExtra("PhoneNumber",binding.mynum.getText().toString());
                startActivity(intent);
            }
        });
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // SMS permissions granted
                // You can proceed with Firebase Phone Authentication or any SMS-related functionality
            } else {
                // SMS permissions denied
                // Handle the case where the user denied SMS permissions
            }
        }
    }
}