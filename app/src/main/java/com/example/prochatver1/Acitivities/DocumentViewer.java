package com.example.prochatver1.Acitivities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prochatver1.databinding.ActivityDocumentViewerBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class DocumentViewer extends AppCompatActivity {
String documentUrl;
String documentType;
Uri uri;
ActivityDocumentViewerBinding binding;
FirebaseStorage storage;
StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocumentViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        documentUrl = getIntent().getStringExtra("documentUrl");
        documentType = getIntent().getStringExtra("documentType");
        uri = Uri.parse(documentUrl);
        binding.webView.clearCache(true);
        if(documentType.equals("Word")){
            WebSettings webSettings = binding.webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAllowFileAccess(true);
            String htmlPath = "file:///android_asset/viewer.html?documentUrl=" + documentUrl;
            binding.webView.loadUrl(htmlPath);
            openFile(htmlPath);
        }
        else if(documentType.equals("PDF")){
            openDocument(documentUrl,"application/pdf");
        }
        else if(documentType.equals("Excel")){

        }
        else if(documentType.equals("PPT")){

        }
    }
    private void openFile(String htmlPath){
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewConsole", consoleMessage.message());
                return true;
            }
        });
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "Page finished loading: " + url);

                // Inject JavaScript code to check if Viewer.js is loaded
                binding.webView.evaluateJavascript(
                        "javascript: " +
                                "if (typeof Viewer !== 'undefined') {" +
                                "   console.log('Viewer.js is loaded');" +
                                "} else {" +
                                "   console.log('Viewer.js is NOT loaded');" +
                                "}",
                        null
                );
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e("WebView", "Error loading page: " + error.getDescription());
            }
        });
    }
    private void openDocument(String url, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setDataAndType(Uri.parse(url), type);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {

            Intent chooserIntent = Intent.createChooser(intent, "Open PDF with");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooserIntent);
            } else {
                binding.webView.setVisibility(View.GONE);
                binding.pdfView.setVisibility(View.VISIBLE);
                displayPdfFromUrl(documentUrl);
            }
        } catch (ActivityNotFoundException e) {
            // Handle the case where no PDF viewer app is installed
            e.printStackTrace();
        }
    }
    private void displayPdfFromUrl(String pdfUrl) {
        // Load PDF from URL using the PDFView library
        new RetrievePdfStream().execute(pdfUrl);
    }
    class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputStream;
        }
        @Override
        protected void onPostExecute(InputStream inputStream) {
            binding.pdfView.fromStream(inputStream).load();
        }
    }
}
