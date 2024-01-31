package com.example.prochatver1.Acitivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    String currentPhotoPath;
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
                uploadImageToFirebase();
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if(currentPhotoPath!=null){
                    File file = new File(currentPhotoPath);
                    file.delete();
                }
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
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
//                Uri photoURI = Uri.fromFile(photoFile);
                File file = new File(currentPhotoPath);
                Uri fileUri = FileProvider.getUriForFile(ImageCapture.this, "com.example.prochatver1.fileprovider", file);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            // Handle the case where no camera app is available
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Display the captured image in the ImageView
            binding.imageView.setImageURI(Uri.parse(currentPhotoPath));
            binding.imageView.setVisibility(View.VISIBLE);
            // Upload the image to Firebase Storage
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Get the Downloads directory
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Create VSchat folder if it doesn't exist
        File vsChatFolder = new File(downloadsDirectory, "VSchat");
        if (!vsChatFolder.exists()) {
            vsChatFolder.mkdirs();
        }

        // Create audio subfolder if it doesn't exist
        File audioFolder = new File(vsChatFolder, "Images");
        if (!audioFolder.exists()) {
            audioFolder.mkdirs();
        }

        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                audioFolder
        );

        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }
    private void uploadImageToFirebase() {
        Uri imageUri = Uri.fromFile(new File(currentPhotoPath));
        String imageName = "image_" + System.currentTimeMillis() + ".jpg";
        Calendar calendar = Calendar.getInstance();
        StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
        reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
        File fileToDelete = new File(currentPhotoPath);
        fileToDelete.delete();
        finish();
    }
}