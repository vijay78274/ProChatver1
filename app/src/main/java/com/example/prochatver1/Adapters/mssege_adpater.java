package com.example.prochatver1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.prochatver1.Acitivities.FullVideoActivity;
import com.example.prochatver1.Acitivities.ViewerActivity;
import com.example.prochatver1.Models.messege;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ItemRecieveBinding;
import com.example.prochatver1.databinding.ItemsendBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class mssege_adpater extends RecyclerView.Adapter{
    Context context;
    ArrayList<messege> messages;
    final int item_send = 1;
    final int item_recieve = 2;
    ArrayList<String> imageUrls = new ArrayList<>();
    String senderRoom;
    String reciverRoom;
    public mssege_adpater(Context context, ArrayList<messege> messages, String senderRoom, String reciverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.reciverRoom=reciverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == item_send){
            View view = LayoutInflater.from(context).inflate(R.layout.itemsend,parent,false);
            return new sendViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve,parent,false);
            return new recieverViewHolder(view);
        }
    }
    int[] reaction = new int[]{
        R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
    };
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        messege message = messages.get(position);
        if(holder.getClass() == sendViewHolder.class){
            sendViewHolder viewHolder = (sendViewHolder)holder;
            if(message.getMessage().equals("Photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.sendmsg.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.image);
                imageUrls.add(message.getImageUrl());
            }
            if(message.getMessage().equals("Video")){
                viewHolder.binding.frame.setVisibility(View.VISIBLE);
                viewHolder.binding.sendmsg.setVisibility(View.GONE);
                Glide.with(context).load(message.getVideoThumbnail()).into(viewHolder.binding.video);
            }
            viewHolder.binding.sendmsg.setText(message.getMessage());
            if(message.getFeeling()>=0){
                viewHolder.binding.feeling.setImageResource(reaction[(int) message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.image);
            }
            else{
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewerActivity.class);
                    intent.putStringArrayListExtra("imageUrls", imageUrls);
                    context.startActivity(intent);
                }
            });
            viewHolder.binding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FullVideoActivity.class);
                    intent.putExtra("videoUrl",message.getVideoUrl());
                    context.startActivity(intent);
                }
            });
//            viewHolder.binding.sendmsg.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    popup.onTouch(v,event);
//                    return false;
//                }
//            });
        }
        else{
            recieverViewHolder viewHolder = (recieverViewHolder) holder;
            viewHolder.binding.recMsg.setText(message.getMessage());

            if(message.getMessage().equals("Photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.recMsg.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.image);
                imageUrls.add(message.getImageUrl());
            }
            if(message.getMessage().equals("Video")){
                viewHolder.binding.frame.setVisibility(View.VISIBLE);
                viewHolder.binding.recMsg.setVisibility(View.GONE);
                Glide.with(context).load(message.getVideoThumbnail()).into(viewHolder.binding.video);
            }
            if(message.getFeeling()>=0){
                viewHolder.binding.feeling.setImageResource(reaction[(int) message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else{
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewerActivity.class);
                    intent.putStringArrayListExtra("imageUrls", imageUrls);
                    context.startActivity(intent);
                }
            });
            viewHolder.binding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FullVideoActivity.class);
                    intent.putExtra("videoUrl",message.getVideoUrl());
                    context.startActivity(intent);
                }
            });
//            viewHolder.binding.recMsg.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    popup.onTouch(v,event);
//                    return false;
//                }
//            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        messege message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return  item_send;
        }
        else{
            return item_recieve;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class sendViewHolder extends RecyclerView.ViewHolder{
        ItemsendBinding binding;
        public sendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemsendBinding.bind(itemView);
        }
    }
    public class recieverViewHolder extends RecyclerView.ViewHolder{
        ItemRecieveBinding binding;

        public recieverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecieveBinding.bind(itemView);
        }
    }

}
