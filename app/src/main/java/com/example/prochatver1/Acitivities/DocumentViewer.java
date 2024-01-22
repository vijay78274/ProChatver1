package com.example.prochatver1.Acitivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ActivityDocumentViewerBinding;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class DocumentViewer extends AppCompatActivity {
String documentUrl;
String documentType;
Uri uri;
ActivityDocumentViewerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocumentViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        documentUrl = getIntent().getStringExtra("documentUrl");
        documentType = getIntent().getStringExtra("documentType");
        uri = Uri.parse(documentUrl);
        if(documentType.equals("Word")){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            // Verify that there is an app to handle the intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Handle the case where there is no app to handle DOCX files

            }
        }
        else if(documentType.equals("PDF")){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            // Verify that there is an app to handle the intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Handle the case where there is no app to handle DOCX files
                binding.webView.setVisibility(View.GONE);
                binding.pdfView.setVisibility(View.VISIBLE);
                binding.pdfView.fromUri(uri)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .pageFitPolicy(FitPolicy.WIDTH)
                        .load();
            }
        }
        else if(documentType.equals("Excel")){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            // Verify that there is an app to handle the intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
//                HttpURLConnection connection = (HttpURLConnection) documentUrl.openConnection();
//                try {
//                    connection.connect();
//                    InputStream inputStream = connection.getInputStream();
//                    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
                // Handle the case where there is no app to handle Excel files
            }
        }
        else if(documentType.equals("PPT")){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            // Verify that there is an app to handle the intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Handle the case where there is no app to handle PPTX files
                WebSettings webSettings = binding.webView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                binding.webView.setWebViewClient(new WebViewClient());

                binding.webView.loadUrl(documentUrl);
            }
        }
    }
}