package com.example.prochatver1.Acitivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.example.prochatver1.R;
import com.example.prochatver1.databinding.ActivityDocumentViewerBinding;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocumentViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        documentUrl = getIntent().getStringExtra("documentUrl");
        documentType = getIntent().getStringExtra("documentType");
        uri = Uri.parse(documentUrl);
        if(documentType.equals("Word")){
            openDocument(documentUrl,"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//
//            // Verify that there is an app to handle the intent
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                // Handle the case where there is no app to handle DOCX files
//
//            }
        }
        else if(documentType.equals("PDF")){
            openDocument(documentUrl,"application/pdf");
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, "application/pdf");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//
//            // Verify that there is an app to handle the intent
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                // Handle the case where there is no app to handle DOCX files
//                binding.webView.setVisibility(View.GONE);
//                binding.pdfView.setVisibility(View.VISIBLE);
//                displayPdfFromUrl(documentUrl);
//            }
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
    private void openDocument(String url, String type) {
        // Create an intent with the ACTION_VIEW action
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Set the data and type for the intent
        intent.setDataAndType(Uri.parse(url), type);

        // Add flags to handle different scenarios
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            // Start the activity with the intent
            Intent chooserIntent = Intent.createChooser(intent, "Open PDF with");

            // Verify that the intent will resolve to an activity
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Start the activity with the chooser intent
                startActivity(chooserIntent);
            } else {
                // Handle the case where no app can handle the PDF
                // This could occur if there are no PDF viewer apps installed
                // or if the device doesn't support opening PDFs
                // You might want to provide user feedback in this case
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
