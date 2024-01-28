package com.example.prochatver1.Acitivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.prochatver1.Models.messege;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ActivityImageCaptureBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ImageCapture extends AppCompatActivity {
ActivityImageCaptureBinding binding;
private static final int REQUEST_IMAGE_CAPTURE = 1;
    String senderRoom, reciverRoom;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String senderUid;
    String reciveruid;
    ArrayList<messege> message;
    Bitmap imageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageCaptureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        openCamera();
        message = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        senderUid = FirebaseAuth.getInstance().getUid();
        reciveruid = getIntent().getStringExtra("uid");
        senderRoom = senderUid + reciveruid;
        reciverRoom = reciveruid + senderUid;
        database = FirebaseDatabase.getInstance();
        binding.sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToFirebase(imageBitmap);
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }
    public void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            // Handle the case where no camera app is available
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            // Display the captured image in the ImageView
            binding.imageView.setImageBitmap(imageBitmap);
            binding.imageView.setVisibility(View.VISIBLE);
            // Upload the image to Firebase Storage
        }
    }
    private void uploadImageToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        String imageName = "image_" + System.currentTimeMillis() + ".jpg";
        Calendar calendar = Calendar.getInstance();
        StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
        reference.putBytes(imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String filepath = uri.toString();
                            String msgtxt = "";
                            Date date = new Date();
                            messege message1 = new messege(msgtxt,senderUid,date.getTime());
                            message1.setMessage("Photo");
                            message1.setImageUrl(filepath);
                            String document = imageName;
                            message1.setDocumentName(document);
                            String pushkey = database.getReference().push().getKey();
                            HashMap<String, Object> lastmsg = new HashMap<>();
                            lastmsg.put("lastmsg",message1.getMessage());
                            lastmsg.put("lasttime",date.getTime());
                            database.getReference().child("chats").child(senderRoom).updateChildren(lastmsg);
                            database.getReference().child("chats").child(reciverRoom).updateChildren(lastmsg);
                            database.getReference().child("chats").child(senderRoom).child("messages").child(pushkey).setValue(message1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference().child("chats").child(reciverRoom).child("messages").child(pushkey).setValue(message1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });
                                    HashMap<String, Object> lastmsg = new HashMap<>();
                                    lastmsg.put("lastmsg",message1.getMessage());
                                    lastmsg.put("lasttime",date.getTime());
                                    database.getReference().child("chats").child(senderRoom).updateChildren(lastmsg);
                                    database.getReference().child("chats").child(reciverRoom).updateChildren(lastmsg);
                                }
                            });
                        }
                    });
                }
            }
        });
        Intent intent = new Intent(ImageCapture.this,ChatActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}