package com.example.prochatver1.Acitivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import android.view.View;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.prochatver1.MainRepository;
import com.example.prochatver1.Extras.DataModelType;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ActivityMyVideoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MyVideo extends AppCompatActivity implements MainRepository.Listener {
    ActivityMyVideoBinding binding;
    String recieverName;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String recieverUid;
    String Profile;
    private MainRepository mainRepository;
    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        recieverName = getIntent().getStringExtra("callername");
        recieverUid = getIntent().getStringExtra("recieverUid");
        Profile = getIntent().getStringExtra("callerProfile");
        binding.targetUserNameEt.setText(recieverUid);
        init();
    }
    private void init() {
        mainRepository = MainRepository.getInstance();
        binding.callBtn.setOnClickListener(v -> {
            mainRepository.sendCallRequest(binding.targetUserNameEt.getText().toString(), () -> {
                Toast.makeText(this, "couldn't find the target", Toast.LENGTH_SHORT).show();
            });
        });
        mainRepository.initLocalView(binding.localView);
        mainRepository.initRemoteView(binding.remoteView);
        mainRepository.listener = this;

        mainRepository.subscribeForLatestEvent(data -> {
            if (data.getType() == DataModelType.StartCall) {
                runOnUiThread(() -> {
                    binding.incomingNameTV.setText(recieverName + " is Calling you");
                    if(Profile!=null){
                        Glide.with(this).load(Profile).placeholder(R.drawable.profile_pic)
                                .into(binding.callerprofile);
                    }
                    binding.incomingCallLayout.setVisibility(View.VISIBLE);
                    binding.acceptButton.setOnClickListener(v -> {
                        //star the call here
                        mainRepository.startCall(data.getSender());
                        binding.incomingCallLayout.setVisibility(View.GONE);
                    });
                    binding.rejectButton.setOnClickListener(v -> {
                        binding.incomingCallLayout.setVisibility(View.GONE);
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
        @Override
        public void webrtcConnected() {
            runOnUiThread(()->{
                binding.incomingCallLayout.setVisibility(View.GONE);
                binding.whoToCallLayout.setVisibility(View.GONE);
                binding.callLayout.setVisibility(View.VISIBLE);
            });
        }

        @Override
        public void webrtcClosed() {
            runOnUiThread(this::finish);
        }
}