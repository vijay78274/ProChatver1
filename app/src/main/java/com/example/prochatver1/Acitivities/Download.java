package com.example.prochatver1.Acitivities;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.prochatver1.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Download extends IntentService {
    Context context;
    String documentUrl;
    String documentName;
    public Download() {
        super("Download");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            documentUrl = intent.getStringExtra("docurl");
            documentName = intent.getStringExtra("docname");
            downloadPdf(documentUrl,documentName);
        }
    }
    private boolean isPdfFile(String fileName) {
        return fileName.toLowerCase().endsWith(".pdf");
    }
    private void downloadPdf(String fileUrl, String fileName) {
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

        // PDF download started, you can notify or perform any required actions
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