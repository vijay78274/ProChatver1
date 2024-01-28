package com.example.prochatver1.Acitivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.example.prochatver1.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class toolOptions extends AppCompatActivity {
    private long downloadId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_options);
        String file = getIntent().getStringExtra("documentUrl");
        openFile(file);
    }

    private void openFile(String fileUrl) {
        // Create an Intent with ACTION_VIEW to open the file
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Parse the file URL to get the file type
        String mimeType = getMimeTypeFromUrl(fileUrl);

        // Set the MIME type for the Intent
        intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");

        // Start the activity based on the user's installed apps
        startActivity(intent);
    }

    private String getMimeTypeFromUrl(String url) {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
    }
}