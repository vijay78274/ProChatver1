package com.example.prochatver1.Acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.prochatver1.Models.DataModelType;
import com.example.prochatver1.Models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.permissionx.guolindev.PermissionX;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.prochatver1.Extras.MainRepository;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ActivityConnectingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ConnectingActivity extends AppCompatActivity implements MainRepository.Listener{
    ActivityConnectingBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    MainRepository mainRepository;
    String username;
    String Callername;
    String Recivername;
    String profile;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 1000;
    private static final int CAMERA_PERMISSION_CODE = 1001;
    boolean isAudio=true;
    boolean isCamera=true;
    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityConnectingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
//        checkAudioPermission();
//        checkCameraPermission();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        username = auth.getUid();

        Recivername = getIntent().getStringExtra("name");
        profile = getIntent().getStringExtra("Profile");
        if(profile==null){
            Glide.with(this).load(R.drawable.profile_pic).into(binding.profile);
        }
        else{
            Glide.with(this).load(profile).into(binding.profile);
        }

        binding.name.setText(Callername);
        binding.callername.setText(Callername);
        binding.micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAudio=!isAudio;
                if(isAudio){
                    binding.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                }
                else{
                    binding.micButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
                }
            }
        });
        binding.videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCamera=!isCamera;
                if(isCamera){
                    binding.videoButton.setImageResource(R.drawable.ic_baseline_videocam_24);
                }
                else{
                    binding.videoButton.setImageResource(R.drawable.ic_baseline_videocam_off_24);
                }
            }
        });
    }
    private void init() {
        mainRepository = MainRepository.getInstance();
        mainRepository.sendCallRequest(Recivername, () -> {
            Toast.makeText(this, "couldn't find the target", Toast.LENGTH_SHORT).show();
        });
        mainRepository.initLocalView(binding.localView);
        mainRepository.initRemoteView(binding.remoteView);
        mainRepository.listener = this;

        mainRepository.subscribeForLatestEvent(data -> {
            if (data.getType() == DataModelType.StartCall) {
                runOnUiThread(() -> {
                    binding.name.setText(data.getSender());
                    binding.recievingCall.setVisibility(View.VISIBLE);
                    binding.accept.setOnClickListener(v -> {
                        //star the call here
                        mainRepository.startCall(data.getSender());
                        binding.recievingCall.setVisibility(View.GONE);
                    });
                    binding.reject.setOnClickListener(v -> {
                        binding.recievingCall.setVisibility(View.GONE);
                    });
                });
            }
        });

        binding.switchCameraButton.setOnClickListener(v -> {
            mainRepository.switchCamera();
        });

        binding.micButton.setOnClickListener(v -> {
            if (isMicrophoneMuted) {
                binding.micButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
            } else {
                binding.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
            }
            mainRepository.toggleAudio(isMicrophoneMuted);
            isMicrophoneMuted = !isMicrophoneMuted;
        });

        binding.videoButton.setOnClickListener(v -> {
            if (isCameraMuted) {
                binding.videoButton.setImageResource(R.drawable.ic_baseline_videocam_off_24);
            } else {
                binding.videoButton.setImageResource(R.drawable.ic_baseline_videocam_24);
            }
            mainRepository.toggleVideo(isCameraMuted);
            isCameraMuted = !isCameraMuted;
        });

        binding.endCallButton.setOnClickListener(v -> {
            mainRepository.endCall();
            finish();
        });
    }
    public void webrtcConnected() {
        runOnUiThread(()->{
            binding.recievingCall.setVisibility(View.GONE);
            binding.callLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void webrtcClosed() {
        runOnUiThread(this::finish);
    }
}