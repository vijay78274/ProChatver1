package com.example.prochatver1.Acitivities;

import android.Manifest;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.prochatver1.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {
    public DownloadService() {
        super("DownloadService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null){
            String fileUrl = intent.getStringExtra("fileUrl");
            String fileName = intent.getStringExtra("fileName");
            String type = intent.getStringExtra("type");
            if (type.equals("Photo")) {
                performdownloadImage(fileUrl, fileName);
            } else if (type.equals("Video")) {
                performdownloadVideo(fileUrl, fileName);
            }
            else if(type.equals("Document")){
                performdownloadDocument(fileUrl,fileName);
            }
            else if(type.equals("Audio")){
                performdownloadAudio(fileUrl,fileName);
            }
        }
        else{
            Toast.makeText(DownloadService.this,"Not Found",Toast.LENGTH_SHORT).show();
        }
    }
    private void performdownloadImage(String fileUrl, String fileName) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(fileUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        File vschatFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VSchat");
        File imagesSubfolder = new File(vschatFolder, "Images");
        if (!imagesSubfolder.exists()) {
            imagesSubfolder.mkdirs();
        }
        File destinationFile = new File(imagesSubfolder, fileName);
        request.setDestinationUri(Uri.fromFile(destinationFile));
        request.setTitle(fileName);

        long downloadId = downloadManager.enqueue(request);
        notifyFileDownloaded(fileName);
    }
    private void performdownloadAudio(String fileUrl, String fileName) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(fileUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        File vschatFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VSchat");
        File imagesSubfolder = new File(vschatFolder, "Audio");
        if (!imagesSubfolder.exists()) {
            imagesSubfolder.mkdirs();
        }
        File destinationFile = new File(imagesSubfolder, fileName);
        request.setDestinationUri(Uri.fromFile(destinationFile));
        request.setTitle(fileName);

        long downloadId = downloadManager.enqueue(request);
        notifyFileDownloaded(fileName);
    }

    private void performdownloadVideo(String fileUrl, String fileName) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(fileUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        File vschatFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VSchat");
        File videosSubfolder = new File(vschatFolder, "Videos");
        if (!videosSubfolder.exists()) {
            videosSubfolder.mkdirs();
        }
        File destinationFile = new File(videosSubfolder, fileName);
        request.setDestinationUri(Uri.fromFile(destinationFile));
        request.setTitle(fileName);

        long downloadId = downloadManager.enqueue(request);
        notifyFileDownloaded(fileName);
    }
    private void performdownloadDocument(String fileUrl, String fileName) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(fileUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // Create custom subfolders if they don't exist
        File vschatFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VSchat");
        File pdfSubfolder = new File(vschatFolder, "Documents");
        if (!pdfSubfolder.exists()) {
            pdfSubfolder.mkdirs();
        }

        // Set destination path including the custom subfolders
        File destinationFile = new File(pdfSubfolder, fileName);
        request.setDestinationUri(Uri.fromFile(destinationFile));
        request.setTitle(fileName);

        long downloadId = downloadManager.enqueue(request);
        notifyFileDownloaded(fileName);
    }
    private void notifyFileDownloaded(String fileName) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for devices running Android Oreo (API 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default_channel_id", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_channel_id")
                .setSmallIcon(R.drawable.vs_chat_notifi) // Set your notification icon
                .setContentTitle("File Downloaded")
                .setContentText("File: " + fileName + " has been downloaded")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Trigger the notification
        notificationManager.notify(1, builder.build()); // Use a unique notification ID
    }
}
