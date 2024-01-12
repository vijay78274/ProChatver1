package com.example.prochatver1.Acitivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.prochatver1.R;
import com.google.firebase.auth.FirebaseAuth;

public class Splash_activity extends AppCompatActivity {
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash_activity.this,MainActivity.class);
                    startActivity(intent);
                }
            },4000);
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash_activity.this,SingUp.class);
                    startActivity(intent);
                }
            },4000);
        }
    }
}