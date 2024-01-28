package com.example.prochatver1.Acitivities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.prochatver1.Extras.DataModelType;
import com.example.prochatver1.MainRepository;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ActivityMyVideoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MyVideo extends AppCompatActivity implements MainRepository.Listener {
    ActivityMyVideoBinding binding;
    String callerUid;
    String callerName;
    FirebaseAuth auth;
    String recieverName;
    String recieverUid;
    String callerProfile;
    String recieverProfile;
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
        callerName = getIntent().getStringExtra("callername");
        recieverUid = getIntent().getStringExtra("recieverUid");
        callerUid = auth.getUid();
        callerProfile = getIntent().getStringExtra("callerProfile");
        recieverProfile = getIntent().getStringExtra("recieverProfile");
        recieverName = getIntent().getStringExtra("recievername");
        binding.targetUserNameEt.setText(recieverUid);
        binding.acceptButton.setScaleX(0f);
        binding.acceptButton.setScaleY(0f);
        init();
    }
    private void init() {
        mainRepository = MainRepository.getInstance();
        binding.callBtn.setOnClickListener(v -> {
            mainRepository.sendCallRequest(binding.targetUserNameEt.getText().toString(), () -> {
                Toast.makeText(this, "couldn't find the target", Toast.LENGTH_SHORT).show();
            });
            binding.outGoingNameTv.setText("Calling "+recieverName);
            if(recieverProfile !=null){
                Glide.with(this).load(recieverProfile).placeholder(R.drawable.profile_pic)
                        .into(binding.recieverprofile);
            }
            binding.whoToCallLayout.setVisibility(View.GONE);
            binding.outGoingCallLayout.setVisibility(View.VISIBLE);
        });
        mainRepository.initLocalView(binding.localView);
        mainRepository.initRemoteView(binding.remoteView);
        mainRepository.listener = this;

        mainRepository.subscribeForLatestEvent(data -> {
            if (data.getType() == DataModelType.StartCall) {
                runOnUiThread(() -> {
                    binding.incomingNameTV.setText(recieverName + " is Calling you");
                    if(recieverName !=null){
                        Glide.with(this).load(recieverProfile).placeholder(R.drawable.profile_pic)
                                .into(binding.recieverprofile);
                    }
                    startAcceptButtonAnimation();
                    binding.incomingCallLayout.setVisibility(View.VISIBLE);
                    binding.acceptButton.setOnClickListener(v -> {
                        //star the call here
                        mainRepository.startCall(data.getSender());
                        binding.incomingCallLayout.setVisibility(View.GONE);
                        binding.whoToCallLayout.setVisibility(View.GONE);
                        binding.callLayout.setVisibility(View.VISIBLE);
                    });
                    binding.rejectButton.setOnClickListener(v -> {
                        Toast.makeText(this,"call rejected",Toast.LENGTH_SHORT).show();
                    });
                });
            }
        });
        binding.CallendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyVideo.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
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
            webrtcClosed();
        });
    }
        @Override
        public void webrtcConnected() {
            runOnUiThread(()->{
                binding.incomingCallLayout.setVisibility(View.GONE);
                binding.outGoingCallLayout.setVisibility(View.GONE);
                binding.whoToCallLayout.setVisibility(View.GONE);
                binding.callLayout.setVisibility(View.VISIBLE);
            });
        }

        @Override
        public void webrtcClosed() {
            runOnUiThread(this::finish);
            Intent intent = new Intent(MyVideo.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        }
    private void startAcceptButtonAnimation() {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(binding.acceptButton, View.SCALE_X, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(binding.acceptButton, View.SCALE_Y, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private void onError() {
        Toast.makeText(this, "couldn't find the target", Toast.LENGTH_SHORT).show();
    }
}