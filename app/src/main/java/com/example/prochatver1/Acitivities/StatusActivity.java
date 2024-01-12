package com.example.prochatver1.Acitivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.prochatver1.Adapters.StatusAdapter;
import com.example.prochatver1.Adapters.UsersAdapter;
import com.example.prochatver1.Models.Status;
import com.example.prochatver1.Models.User_status;
import com.example.prochatver1.Models.Users;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ActivityStatusBinding;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class StatusActivity extends AppCompatActivity {
    ArrayList<User_status> user_statuses;
    ArrayList<Users> users;
    ActivityStatusBinding binding;
    FirebaseDatabase database;
    StatusAdapter adapter;
    ProgressDialog dialog;
    Users user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user_statuses = new ArrayList<>();
        users = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading");
        dialog.setCancelable(false);

        adapter = new StatusAdapter(this,user_statuses);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.recyler.setLayoutManager(layoutManager);
        binding.recyler.setAdapter(adapter);

        database.getReference("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        User_status status = new User_status();
                        status.setName(snapshot1.child("name").getValue(String.class));
                        status.setProfileImage(snapshot1.child("profileImage").getValue(String.class));
                        status.setLastUpdate(snapshot1.child("lastupdate").getValue(Long.class));
                        user_statuses.add(status);

                        ArrayList<Status> statuses = new ArrayList<>();
                        for(DataSnapshot snapshot2: snapshot1.child("statuses").getChildren()){
                            Status status1 = snapshot2.getValue(Status.class);
                            statuses.add(status1);
                        }
                        status.setStatuses(statuses);
                        user_statuses.add(status);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.bottomNavigation.setSelectedItemId(R.id.status);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.status){
                    return true;
                }
                else if(id==R.id.chat){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
//                    finish();
                    return true;
                }
                else if(id==R.id.calls){
                    Intent intent = new Intent(getApplicationContext(), CallActivity.class);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
//                    finish();
                    return true;
                }
                return false;
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,10);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(data!=null){
            if(data.getData()!=null){
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("statuses").child(date.getTime()+"");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    User_status user_status = new User_status();
                                    user_status.setName(user.getName());
                                    user_status.setProfileImage(user.getProfileImage());
                                    user_status.setLastUpdate(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name",user_status.getName());
                                    obj.put("profileImage",user_status.getProfileImage());
                                    obj.put("lastupdate",user_status.getLastUpdate());

                                    String imageUrl = uri.toString();
                                    Status status = new Status(imageUrl,user_status.getLastUpdate());
                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);
                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses").push().setValue(status);
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}