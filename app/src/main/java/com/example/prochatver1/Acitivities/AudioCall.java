package com.example.prochatver1.Acitivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ActivityAudioCallBinding;

public class AudioCall extends AppCompatActivity {
    ActivityAudioCallBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAudioCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}