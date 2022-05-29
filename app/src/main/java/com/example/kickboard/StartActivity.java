package com.example.kickboard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class StartActivity extends AppCompatActivity {
    Button nextButton;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    QrFragment qrFragment;
    InsFragment insFragment;
    HelmetFragment helmetFragment;
    ShareFragment shareFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_KickBoard);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        insFragment = new InsFragment();
        qrFragment = new QrFragment();
        helmetFragment = new HelmetFragment();
        shareFragment = new ShareFragment();

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addPage(insFragment);
        pagerAdapter.addPage(qrFragment);
        pagerAdapter.addPage(helmetFragment);
        pagerAdapter.addPage(shareFragment);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);

        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DescActivity.class);
                startActivity(intent);
            }
        });

    }
    public void onButton3Clicked(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.educar.co.kr/honeQ/product/kickboard/kickboardIntro?origin=m"));
        startActivity(intent);
    }
}