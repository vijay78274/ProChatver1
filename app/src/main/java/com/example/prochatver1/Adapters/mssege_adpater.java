package com.example.prochatver1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prochatver1.Acitivities.AudioPlay;
import com.example.prochatver1.Acitivities.DocumentViewer;
import com.example.prochatver1.Acitivities.DownloadService;
import com.example.prochatver1.Acitivities.FullVideoActivity;
import com.example.prochatver1.Acitivities.ViewerActivity;
import com.example.prochatver1.Acitivities.toolOptions;
import com.example.prochatver1.Models.messege;
import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ItemRecieveBinding;
import com.example.prochatver1.databinding.ItemsendBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class mssege_adpater extends RecyclerView.Adapter{
    Context context;
    ArrayList<messege> messages;
    final int item_send = 1;
    final int item_recieve = 2;
    ArrayList<String> imageUrls = new ArrayList<>();
    String senderRoom;
    String reciverRoom;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
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
                String fileName = message.getDocumentName();
                boolean isImageDownloaded = isImageDownloaded(fileName);
                viewHolder.binding.imageFrame.setVisibility(View.VISIBLE);
                viewHolder.binding.downloadImage.setVisibility(isImageDownloaded ? View.GONE : View.VISIBLE);
                viewHolder.binding.sendmsg.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.image);
                imageUrls.add(message.getImageUrl());
            }
            if(message.getMessage().equals("Video")){
                viewHolder.binding.frame.setVisibility(View.VISIBLE);
                String fileName = message.getDocumentName();
                boolean isVideoDownloaded = isVideoDownloaded(fileName);
                viewHolder.binding.downloadVideo.setVisibility(isVideoDownloaded? View.GONE : View.VISIBLE);
                viewHolder.binding.sendmsg.setVisibility(View.GONE);
                Glide.with(context).load(message.getVideoThumbnail()).into(viewHolder.binding.video);
            }
            if(message.getMessage().equals("Document")){
                viewHolder.binding.documentFrame.setVisibility(View.VISIBLE);
                String fileName = message.getDocumentName();
                boolean isFileDownloaded = isFileDownloaded(fileName);
                viewHolder.binding.downloadDoc.setVisibility(isFileDownloaded ? View.GONE : View.VISIBLE);
                viewHolder.binding.sendmsg.setVisibility(View.GONE);
                viewHolder.binding.textDocumentName.setText(message.getDocumentName());
                if(message.getDocumentType().equals("Word")){
                    viewHolder.binding.imageDocumentIcon.setImageResource(R.drawable.doc);
                    viewHolder.binding.textDocumentType.setText("Word");
                }
                else if(message.getDocumentType().equals("PPT")){
                    viewHolder.binding.imageDocumentIcon.setImageResource(R.drawable.ppt);
                    viewHolder.binding.textDocumentType.setText("PPT");
                }
                else if(message.getDocumentType().equals("Excel")){
                    viewHolder.binding.imageDocumentIcon.setImageResource(R.drawable.xls);
                    viewHolder.binding.textDocumentType.setText("Excel");
                }
                else if(message.getDocumentType().equals("PDF")){
                    viewHolder.binding.imageDocumentIcon.setImageResource(R.drawable.pdf);
                    viewHolder.binding.textDocumentType.setText("PDF");
                }
            }
            if(message.getMessage().equals("Audio")){
                viewHolder.binding.audioFrame.setVisibility(View.VISIBLE);
                viewHolder.binding.sendmsg.setVisibility(View.GONE);
                String fileName = message.getDocumentName();
                boolean isAudioDownloaded = isAudioDownloaded(fileName);
                viewHolder.binding.audioName.setText(message.getDocumentName());
                viewHolder.binding.downloadAudio.setVisibility(isAudioDownloaded? View.GONE : View.VISIBLE);
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
            viewHolder.binding.document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DocumentViewer.class);
                    intent.putExtra("documentUrl",message.getDocumentUrl());
                    intent.putExtra("documentType",message.getDocumentType());
                    context.startActivity(intent);
                }
            });
            viewHolder.binding.document.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(context, toolOptions.class);
                    intent.putExtra("documentUrl",message.getDocumentUrl());
                    intent.putExtra("documentType",message.getDocumentType());
                    context.startActivity(intent);

                    return false;
                }
            });
            viewHolder.binding.audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AudioPlay.class);
                    intent.putExtra("documentUrl",message.getAudioUrl());
                    context.startActivity(intent);
                }
            });
            viewHolder.binding.downloadDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("type",message.getMessage());
                    intent.putExtra("fileUrl",message.getDocumentUrl());
                    intent.putExtra("fileName",message.getDocumentName());
                    context.startService(intent);
                }
            });
            viewHolder.binding.downloadVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("type",message.getMessage());
                    intent.putExtra("fileUrl",message.getVideoUrl());
                    intent.putExtra("fileName",message.getDocumentName());
                    context.startService(intent);
                }
            });
            viewHolder.binding.downloadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("type",message.getMessage());
                    intent.putExtra("fileUrl",message.getImageUrl());
                    intent.putExtra("fileName",message.getDocumentName());
                    context.startService(intent);
                }
            });
            viewHolder.binding.downloadAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("type",message.getMessage());
                    intent.putExtra("fileUrl",message.getAudioUrl());
                    intent.putExtra("fileName",message.getDocumentName());
                    context.startService(intent);
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
                String fileName = message.getDocumentName();
                boolean isImageDownloaded = isImageDownloaded(fileName);
                viewHolder.binding.imageFrame.setVisibility(View.VISIBLE);
                viewHolder.binding.recMsg.setVisibility(View.GONE);
                viewHolder.binding.downloadImage.setVisibility(isImageDownloaded ? View.GONE : View.VISIBLE);
                Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.image);
                imageUrls.add(message.getImageUrl());
            }
            if(message.getMessage().equals("Video")){
                String fileName = message.getDocumentName();
                boolean isVideoDownloaded = isVideoDownloaded(fileName);
                viewHolder.binding.frame.setVisibility(View.VISIBLE);
                viewHolder.binding.recMsg.setVisibility(View.GONE);
                viewHolder.binding.downloadVideo.setVisibility(isVideoDownloaded ? View.GONE : View.VISIBLE);
                Glide.with(context).load(message.getVideoThumbnail()).into(viewHolder.binding.video);
            }
            if(message.getMessage().equals("Document")){
                String fileName = message.getDocumentName();
                boolean isFileDownloaded = isFileDownloaded(fileName);
                viewHolder.binding.documentFrame.setVisibility(View.VISIBLE);
                viewHolder.binding.recMsg.setVisibility(View.GONE);
                viewHolder.binding.downloadDoc.setVisibility(isFileDownloaded ? View.GONE : View.VISIBLE);
                viewHolder.binding.textDocumentName.setText(message.getDocumentName());
                if(message.getDocumentType().equals("Word")){
                    viewHolder.binding.imageDocumentIcon.setImageResource(R.drawable.doc);
                    viewHolder.binding.textDocumentType.setText("Word");
                }
                else if(message.getDocumentType().equals("PPT")){
                    viewHolder.binding.imageDocumentIcon.setImageResource(R.drawable.ppt);
                    viewHolder.binding.textDocumentType.setText("PPT");
                }
                else if(message.getDocumentType().equals("Excel")){
                    viewHolder.binding.imageDocumentIcon.setImageResource(R.drawable.xls);
                    viewHolder.binding.textDocumentType.setText("Excel");
                }
                else if(message.getDocumentType().equals("PDF")){
                    viewHolder.binding.imageDocumentIcon.setImageResource(R.drawable.pdf);
                    viewHolder.binding.textDocumentType.setText("PDF");
                }
            }
            if(message.getMessage().equals("Audio")){
                viewHolder.binding.audioFrame.setVisibility(View.VISIBLE);
                viewHolder.binding.recMsg.setVisibility(View.GONE);
                String fileName = message.getDocumentName();
                boolean isAudioDownloaded = isAudioDownloaded(fileName);
                viewHolder.binding.audioName.setText(message.getDocumentName());
                viewHolder.binding.downloadAudio.setVisibility(isAudioDownloaded? View.GONE : View.VISIBLE);
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
            viewHolder.binding.document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DocumentViewer.class);
                    intent.putExtra("documentUrl",message.getDocumentUrl());
                    intent.putExtra("documentType",message.getDocumentType());
                    context.startActivity(intent);
                }
            });
            viewHolder.binding.document.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(context, toolOptions.class);
                    intent.putExtra("documentUrl",message.getDocumentUrl());
                    intent.putExtra("documentType",message.getDocumentType());
                    context.startActivity(intent);
                    return false;
                }
            });
            viewHolder.binding.audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AudioPlay.class);
                    intent.putExtra("documentUrl",message.getAudioUrl());
                    context.startActivity(intent);
                }
            });
            viewHolder.binding.downloadDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("type",message.getMessage());
                    intent.putExtra("fileUrl",message.getDocumentUrl());
                    intent.putExtra("fileName",message.getDocumentName());
                    context.startService(intent);
                }
            });
            viewHolder.binding.downloadVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("type",message.getMessage());
                    intent.putExtra("fileUrl",message.getVideoUrl());
                    intent.putExtra("fileName",message.getDocumentName());
                    context.startService(intent);
                }
            });
            viewHolder.binding.downloadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("type",message.getMessage());
                    intent.putExtra("fileUrl",message.getImageUrl());
                    intent.putExtra("fileName",message.getDocumentName());
                    context.startService(intent);
                }
            });
            viewHolder.binding.downloadAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("type",message.getMessage());
                    intent.putExtra("fileUrl",message.getAudioUrl());
                    intent.putExtra("fileName",message.getDocumentName());
                    context.startService(intent);
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
    private boolean isFileDownloaded(String fileName) {
        File vschatFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VSchat");
        File documentSubfolder = new File(vschatFolder, "Documents");
        File localFile = new File(documentSubfolder, fileName);

        return localFile.exists();
    }
    private boolean isVideoDownloaded(String fileName){
        File vschatFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VSchat");
        File documentSubfolder = new File(vschatFolder, "Videos");
        File localFile = new File(documentSubfolder, fileName);

        return localFile.exists();
    }
    private boolean isImageDownloaded(String fileName){
        File vschatFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VSchat");
        File documentSubfolder = new File(vschatFolder, "Images");
        File localFile = new File(documentSubfolder, fileName);

        return localFile.exists();
    }
    private boolean isAudioDownloaded(String fileName){
        File vschatFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VSchat");
        File documentSubfolder = new File(vschatFolder, "Audio");
        File localFile = new File(documentSubfolder, fileName);

        return localFile.exists();
    }
}
