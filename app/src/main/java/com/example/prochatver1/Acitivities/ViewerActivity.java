package com.example.prochatver1.Acitivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.media.Image;
import android.os.Bundle;

import com.example.prochatver1.Adapters.ImagePagerAdapter;
import com.example.prochatver1.R;

import java.util.List;

public class ViewerActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private List<String> imageUrls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

            viewPager = findViewById(R.id.viewPager);
            imageUrls = getIntent().getStringArrayListExtra("imageUrls");

            ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageUrls);
            viewPager.setAdapter(adapter);
        }
}