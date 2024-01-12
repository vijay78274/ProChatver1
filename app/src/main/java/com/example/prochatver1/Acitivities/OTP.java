package com.example.prochatver1.Acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.prochatver1.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukeshsolanki.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {
ActivityOtpBinding binding;
FirebaseAuth auth;
String verificationID;
ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(OTP.this);
        auth = FirebaseAuth.getInstance();
        String phone = getIntent().getStringExtra("PhoneNumber");
        binding.phone.setText("Verify "+ phone);

        dialog = new ProgressDialog(this);;
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);
        dialog.show();
//+91+phone
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phone).setTimeout(60L, TimeUnit.SECONDS).
        setActivity(OTP.this).setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        verificationID = verifyId;
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        binding.otpView.requestFocus();
                        dialog.dismiss();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,otp);
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(OTP.this,"Login",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OTP.this, Profile.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else{
                            Toast.makeText(OTP.this,"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}