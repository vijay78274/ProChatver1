package com.example.prochatver1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prochatver1.Acitivities.ChatActivity;
import com.example.prochatver1.Acitivities.MainActivity;
import com.example.prochatver1.Models.Users;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{

    Context context;
    ArrayList<Users> users;
    public UsersAdapter(Context context, ArrayList<Users> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.conversation,parent,false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
       Users pos =  users.get(position);
       String senderId = FirebaseAuth.getInstance().getUid();
       String senderRoom = senderId + pos.getUid();
        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String lstmsg = snapshot.child("lastmsg").getValue(String.class);
                    long time1 = snapshot.child("lasttime").getValue(Long.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    holder.binding.time.setText(dateFormat.format(new Date(time1)));
                    holder.binding.lstmsg.setText(lstmsg);
                }
                else{
                    holder.binding.lstmsg.setText("Tap to chat");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       holder.binding.name.setText(pos.getName());
        Glide.with(context).load(pos.getProfileImage())
                .placeholder(R.drawable.profile_pic)
                .into(holder.binding.imageView2);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name",pos.getName());
                intent.putExtra("uid",pos.getUid());
                intent.putExtra("profile",pos.getProfileImage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        ConversationBinding binding;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ConversationBinding.bind(itemView);
        }
    }
}
