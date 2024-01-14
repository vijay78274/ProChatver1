package com.example.prochatver1.Acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prochatver1.Models.Calls;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.permissionx.guolindev.PermissionX;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.prochatver1.MainRepository;
import com.example.prochatver1.databinding.ActivityConnectingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.Executors;

public class ConnectingActivity extends AppCompatActivity{
    ActivityConnectingBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    MainRepository mainRepository;
    String Callername;
    String recieverUid;
    String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityConnectingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        recieverUid = getIntent().getStringExtra("RecieverUid");
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        database.getReference().child("users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Callername=snapshot.child("name").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        init();
    }
private void init(){
    mainRepository = MainRepository.getInstance();
    binding.button.setOnClickListener(v -> {
        PermissionX.init(this)
                .permissions(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        mainRepository.login(
                                Uid, getApplicationContext(), () -> {
                                    Toast.makeText(ConnectingActivity.this,"calls login",Toast.LENGTH_SHORT).show();
                                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Perform background tasks here
                                            Intent intent = new Intent(ConnectingActivity.this, MyVideo.class);
                                            intent.putExtra("callername",Callername);
                                            intent.putExtra("recieverUid",recieverUid);
                                            startActivity(intent);
                                            finishAffinity();
                                        }
                                    });
                                    //if success then we want to move to call activity
                                });
                    }
                });

    });
}
}