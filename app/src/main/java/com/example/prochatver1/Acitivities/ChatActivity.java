package com.example.prochatver1.Acitivities;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.prochatver1.Adapters.mssege_adpater;
import com.example.prochatver1.Extras.DataModelType;
import com.example.prochatver1.MainRepository;
import com.example.prochatver1.Models.messege;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ActivityChatActicityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.permissionx.guolindev.PermissionX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity implements MainRepository.Listener{
ActivityChatActicityBinding binding;
mssege_adpater adapter;
ArrayList<messege> message;
String senderRoom, reciverRoom;
FirebaseDatabase database;
FirebaseStorage storage;
String recieveImage;
String senderUid;
String name;
String reciveruid;
String Callername;
String callerImage;
MainRepository mainRepository;
private Boolean isCameraMuted = false;
private Boolean isMicrophoneMuted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatActicityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        message = new ArrayList<>();
        adapter = new mssege_adpater(this,message,senderRoom,reciverRoom);
        binding.recyler.setLayoutManager(new LinearLayoutManager(this));
        binding.recyler.setAdapter(adapter);
        storage = FirebaseStorage.getInstance();
        name = getIntent().getStringExtra("name");
        reciveruid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid();
        recieveImage = getIntent().getStringExtra("profile");

        senderRoom = senderUid + reciveruid;
        reciverRoom = reciveruid + senderUid;
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = FirebaseDatabase.getInstance();
        database.getReference().child("chats").child(senderRoom).child("messages")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                message.clear();
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    messege message1 = dataSnapshot.getValue(messege.class);
                                    message1.setMessageId(dataSnapshot.getKey());
                                    message.add(message1);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgtxt = binding.msg.getText().toString();
                Date date = new Date();
                messege message1 = new messege(msgtxt,senderUid,date.getTime());
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
        binding.attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ChatActivity.this, binding.attach);

                // Inflating the Popup using xml file
                popupMenu.getMenuInflater().inflate(R.menu.attachment_menu, popupMenu.getMenu());

                // Registering the button click listener to handle menu item clicks
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override

                    public boolean onMenuItemClick(MenuItem item) {
                        // Handle menu item click
                        int id = item.getItemId();
                        if (id == R.id.photo) {
                            imageSent();
                            return true;
                        } else if (id == R.id.document) {
                            documentSent();
                            return true;
                        } else if (id == R.id.video) {
                            videoSent();
                            return true;
                        }
                        return false;
                    }
                });

                // Displaying the popup menu
                popupMenu.show();
            }
        });
        database.getReference().child("users").child(senderUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Callername=snapshot.child("name").getValue(String.class);
                    callerImage=snapshot.child("profileImage").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        init();
    }
    public void init(){
        mainRepository = MainRepository.getInstance();
        mainRepository.initLocalView(binding.localView);
        mainRepository.initRemoteView(binding.remoteView);
        mainRepository.listener = this;
        mainRepository.subscribeForLatestEvent(data -> {
            if (data.getType() == DataModelType.StartCall) {
                runOnUiThread(() -> {
                    binding.incomingNameTV.setText(name + " is Calling you");
                    if( callerImage!=null){
                        Glide.with(this).load(callerImage).placeholder(R.drawable.profile_pic)
                                .into(binding.callerprofile);
                    }
                    binding.chatlayout.setVisibility(View.GONE);
                    binding.incomingCallLayout.setVisibility(View.VISIBLE);
                    binding.acceptButton.setOnClickListener(v -> {
                        //star the call here
                        mainRepository.startCall(data.getSender());
                    });
                    binding.rejectButton.setOnClickListener(v -> {
                        mainRepository.endCall();
                        Toast.makeText(this,"call rejected",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    });
                });
            }
        });
        binding.CallendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainRepository.endCall();
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
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
    public void imageSent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,15);
    }
    public void videoSent(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,16);
    }
    public void documentSent() {
        String[] mimeTypes = {"application/msword", "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.ms-excel",  "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"};
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 17);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==15){
            if(data!=null){
                if(data.getData()!=null){
                    Uri selectedimg = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                    reference.putFile(selectedimg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();
                                        String msgtxt = binding.msg.getText().toString();
                                        Date date = new Date();
                                        messege message1 = new messege(msgtxt,senderUid,date.getTime());
                                        message1.setMessage("Photo");
                                        message1.setImageUrl(filepath);
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
                }
            }
        }
        if(requestCode==16){
            if(data!=null){
                if(data.getData()!=null){
                    Uri selectedVideo = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                    reference.putFile(selectedVideo).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();
                                        String msgtxt = binding.msg.getText().toString();
                                        Date date = new Date();
                                        messege message1 = new messege(msgtxt,senderUid,date.getTime());
                                        message1.setMessage("Video");
                                        message1.setVideoUrl(filepath);
                                        message1.setVideoThumbnail(filepath);
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
                }
            }
        }
        if(requestCode==17){
            if(data!=null){
                if(data.getData()!=null){
                    Uri selectedDoc = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                    reference.putFile(selectedDoc).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();
                                        String mimeType = getContentResolver().getType(selectedDoc);
                                        if (isMimeTypeAllowed(mimeType)) {
                                            String msgtxt = binding.msg.getText().toString();
                                            Date date = new Date();
                                            messege message1 = new messege(msgtxt,senderUid,date.getTime());
                                            message1.setMessage("Document");
                                            message1.setDocumentUrl(filepath);
                                            String documentName = getDocumentName(selectedDoc);
                                            message1.setDocumentName(documentName);
                                            if(mimeType.equals("application/msword") || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                                                message1.setDocumentType("Word");
                                            }
                                            else if(mimeType.equals("application/vnd.ms-powerpoint") || mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")){
                                                message1.setDocumentType("PPT");
                                            }
                                            else if(mimeType.equals("application/vnd.ms-excel")){
                                                message1.setDocumentType("Excel");
                                            }
                                            else if(mimeType.equals("application/pdf")){
                                                message1.setDocumentType("PDF");
                                            }
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
                                        else {
                                            Toast.makeText(ChatActivity.this, "Unsupported document type", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }
    private String getDocumentName(Uri uri) {
        String documentName = null;
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                // Check if the column exists before retrieving data
                if (displayNameIndex != -1) {
                    documentName = cursor.getString(displayNameIndex);
                } else {
                    // Handle the case where the DISPLAY_NAME column does not exist
                    Log.e("getDocumentName", "DISPLAY_NAME column not found in the cursor");
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return documentName;
    }
    private boolean isMimeTypeAllowed(String mimeType) {
        // Check against the allowed MIME types
        return Arrays.asList("application/msword", "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation").contains(mimeType);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.tool_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        int id = item.getItemId();
        if (id == R.id.calls) {
            return true;
        }
        if(id==R.id.video){
            mainRepository.sendCallRequest(reciveruid, () -> {
                Toast.makeText(this, "couldn't find the target", Toast.LENGTH_SHORT).show();
            });
            binding.outGoingNameTv.setText("Calling "+name);
            if(recieveImage !=null){
                Glide.with(this).load(recieveImage).placeholder(R.drawable.profile_pic)
                        .into(binding.recieverprofile);
            }
            binding.chatlayout.setVisibility(View.GONE);
            binding.outGoingCallLayout.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void webrtcConnected() {
        runOnUiThread(()->{
            binding.incomingCallLayout.setVisibility(View.GONE);
            binding.outGoingCallLayout.setVisibility(View.GONE);
            binding.callLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void webrtcClosed() {
        binding.chatlayout.setVisibility(View.GONE);
        runOnUiThread(this::finish);
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
    @Override
    protected void onDestroy() {
        // Release resources associated with local and remote surfaces
        mainRepository.releaseLocalView(binding.localView);
        mainRepository.releaseRemoteView(binding.remoteView);

        super.onDestroy();
    }
}