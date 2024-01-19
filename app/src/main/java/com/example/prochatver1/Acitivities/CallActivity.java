package com.example.prochatver1.Acitivities;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prochatver1.Adapters.CallAdapter;
import com.example.prochatver1.Adapters.UsersAdapter;
import com.example.prochatver1.Models.Users;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ActivityCallBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CallActivity extends AppCompatActivity {
    ActivityCallBinding binding;
    FirebaseDatabase database;
    ArrayList<Users> users;
    CallAdapter callAdapter;
    Users user;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 1000;
    static final int CAMERA_PERMISSION_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        callAdapter = new CallAdapter(this, users);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.recyler.setAdapter(callAdapter);


        database.getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    users.clear();
                    Users user = snapshot1.getValue(Users.class);
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                        users.add(user);
                }
                callAdapter.notifyDataSetChanged();
            }
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.bottomNavigation.setSelectedItemId(R.id.calls);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.status){
                    Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
//                    finish();
                    return true;
                }
                else if(id==R.id.chat){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
//                    finish();
                    return true;
                }
                else if(id==R.id.calls){
                    return true;
                }
                return false;
            }
        });
    }

}