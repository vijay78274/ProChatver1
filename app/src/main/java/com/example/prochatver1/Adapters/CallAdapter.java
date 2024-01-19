package com.example.prochatver1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prochatver1.Acitivities.ChatActivity;
import com.example.prochatver1.Acitivities.MainActivity;
import com.example.prochatver1.MainRepository;
import com.example.prochatver1.Models.Users;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.CallUsersBinding;
import com.example.prochatver1.databinding.ConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder>{

    Context context;
    ArrayList<Users> users;
    public CallAdapter(Context context, ArrayList<Users> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.call_users,parent,false);
        return new CallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        Users pos =  users.get(position);
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + pos.getUid();
        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    long time1 = snapshot.child("lasttime").getValue(Long.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    holder.binding.time.setText(dateFormat.format(new Date(time1)));
                    holder.binding.callCount.setText("Tap to chat");
                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class CallViewHolder extends RecyclerView.ViewHolder{
        CallUsersBinding binding;
        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CallUsersBinding.bind(itemView);
        }
    }
}
