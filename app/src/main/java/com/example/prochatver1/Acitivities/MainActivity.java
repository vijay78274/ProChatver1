package com.example.prochatver1.Acitivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.prochatver1.R;
import com.example.prochatver1.Models.Users;
import com.example.prochatver1.Adapters.UsersAdapter;
import com.example.prochatver1.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<Users> users;
    UsersAdapter usersAdapter;
    Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar();
        if (!Network.isNetworkAvailable(this)) {
            showNetworkError();
        }
            database = FirebaseDatabase.getInstance();
            users = new ArrayList<>();
            database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(Users.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            usersAdapter = new UsersAdapter(this, users);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            binding.recyler.setAdapter(usersAdapter);
            binding.recyler.showShimmerAdapter();
            database.getReference("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    users.clear();
                        Users user = snapshot1.getValue(Users.class);
                        if (!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                            users.add(user);
                    }
                    binding.recyler.hideShimmerAdapter();
                    usersAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            binding.bottomNavigation.setSelectedItemId(R.id.chat);
            binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.status) {
                        Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                        startActivity(intent);
//                    overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
//                    finish();
                        return true;
                    } else if (id == R.id.chat) {
                        return true;
                    } else if (id == R.id.calls) {
                        Intent intent = new Intent(getApplicationContext(), CallActivity.class);
                        startActivity(intent);
//                    overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
//                    finish();
                        return true;
                    }
                    return false;
                }
            });
    }
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.topmenu, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected (@NonNull MenuItem item){
            int id = item.getItemId();
            if (id == R.id.signout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SingUp.class);
                startActivity(intent);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        private void showNetworkError() {
            Snackbar.make(findViewById(android.R.id.content), "Network not available", Snackbar.LENGTH_LONG).show();
        }

}