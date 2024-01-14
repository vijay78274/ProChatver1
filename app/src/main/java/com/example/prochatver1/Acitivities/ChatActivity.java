package com.example.prochatver1.Acitivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.prochatver1.Adapters.mssege_adpater;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {
ActivityChatActicityBinding binding;
mssege_adpater adapter;
ArrayList<messege> message;
String senderRoom, reciverRoom;
FirebaseDatabase database;
FirebaseStorage storage;
String profileImage;
String senderUid;
String name;
String reciveruid;
String Callername;
MainRepository mainRepository;
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
        profileImage = getIntent().getStringExtra("profile");

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
                            Toast.makeText(getApplicationContext(),"Document",Toast.LENGTH_SHORT).show();
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
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    // Perform background tasks here
                    Intent intent = new Intent(ChatActivity.this, ConnectingActivity.class);
                    intent.putExtra("RecieverUid",reciveruid);
                    intent.putExtra("Profile",profileImage);
                    startActivity(intent);
                }
            });
            return true;
        }
        if(id==R.id.video){
            mainRepository = MainRepository.getInstance();
                PermissionX.init(this)
                        .permissions(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
                        .request((allGranted, grantedList, deniedList) -> {
                            if (allGranted) {
                                mainRepository.login(
                                        senderUid, getApplicationContext(), () -> {
                                            Toast.makeText(ChatActivity.this,"calls login",Toast.LENGTH_SHORT).show();
                                            Executors.newSingleThreadExecutor().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // Perform background tasks here
                                                    Intent intent = new Intent(ChatActivity.this, MyVideo.class);
                                                    intent.putExtra("callername",Callername);
                                                    intent.putExtra("recieverUid",reciveruid);
                                                    startActivity(intent);
                                                    finishAffinity();
                                                }
                                            });
                                        });
                            }
                        });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}