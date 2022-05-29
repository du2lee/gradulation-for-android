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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DescActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_KickBoard);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);

        // helmet
        TextView helmet_text = (TextView)findViewById(R.id.textView4); //텍스트 변수 선언
        String helmet_content = helmet_text.getText().toString(); //텍스트 가져옴.
        SpannableString helmet_spannableString = new SpannableString(helmet_content); //객체 생성

        String helmet_word ="2만원";
        int helmet_start = helmet_content.indexOf(helmet_word);
        int helmet_end = helmet_start + helmet_word.length();

        helmet_spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), helmet_start, helmet_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        helmet_spannableString.setSpan(new StyleSpan(Typeface.BOLD), helmet_start, helmet_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        helmet_spannableString.setSpan(new RelativeSizeSpan(1.3f), helmet_start, helmet_end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        helmet_text.setText(helmet_spannableString);

        // license
        TextView license_text = (TextView)findViewById(R.id.textView); //텍스트 변수 선언
        String license_content = license_text.getText().toString(); //텍스트 가져옴.
        SpannableString license_spannableString = new SpannableString(license_content); //객체 생성

        String license_word ="10만원";
        int license_start = license_content.indexOf(license_word);
        int license_end = license_start + license_word.length();

        license_spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), license_start, license_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        license_spannableString.setSpan(new StyleSpan(Typeface.BOLD), license_start, license_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        license_spannableString.setSpan(new RelativeSizeSpan(1.3f), license_start, license_end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        license_text.setText(license_spannableString);

        // child
        TextView child_text = (TextView)findViewById(R.id.textView2); //텍스트 변수 선언
        String child_content = child_text.getText().toString(); //텍스트 가져옴.
        SpannableString child_spannableString = new SpannableString(child_content); //객체 생성

        String child_word ="10만원";
        int child_start = child_content.indexOf(child_word);
        int child_end = child_start + child_word.length();

        child_spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), child_start, child_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        child_spannableString.setSpan(new StyleSpan(Typeface.BOLD), child_start, child_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        child_spannableString.setSpan(new RelativeSizeSpan(1.3f), child_start, child_end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        child_text.setText(child_spannableString);

        // passenger
        TextView passenger_text = (TextView)findViewById(R.id.textView3); //텍스트 변수 선언
        String passenger_content = passenger_text.getText().toString(); //텍스트 가져옴.
        SpannableString passenger_spannableString = new SpannableString(passenger_content); //객체 생성

        String passenger_word ="4만원";
        int passenger_start = passenger_content.indexOf(passenger_word);
        int passenger_end = passenger_start + passenger_word.length();

        passenger_spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), passenger_start, passenger_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        passenger_spannableString.setSpan(new StyleSpan(Typeface.BOLD), passenger_start, passenger_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        passenger_spannableString.setSpan(new RelativeSizeSpan(1.3f), passenger_start, passenger_end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        passenger_text.setText(passenger_spannableString);

        // drinking
        TextView drinking_text = (TextView)findViewById(R.id.textView5); //텍스트 변수 선언
        String drinking_content = drinking_text.getText().toString(); //텍스트 가져옴.
        SpannableString drinking_spannableString = new SpannableString(drinking_content); //객체 생성

        String drinking_word ="10만원";

        int drinking_start = drinking_content.indexOf(drinking_word);
        int drinking_end = drinking_start + drinking_word.length();

        drinking_spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), drinking_start, drinking_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        drinking_spannableString.setSpan(new StyleSpan(Typeface.BOLD), drinking_start, drinking_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        drinking_spannableString.setSpan(new RelativeSizeSpan(1.3f), drinking_start, drinking_end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        drinking_text.setText(drinking_spannableString);

        // violation
        TextView violation_text = (TextView)findViewById(R.id.textView6); //텍스트 변수 선언
        String violation_content = violation_text.getText().toString(); //텍스트 가져옴.
        SpannableString violation_spannableString = new SpannableString(violation_content); //객체 생성

        String violation_word ="3만원";

        int violation_start = violation_content.indexOf(violation_word);
        int violation_end = violation_start + violation_word.length();

        violation_spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), violation_start, violation_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        violation_spannableString.setSpan(new StyleSpan(Typeface.BOLD), violation_start, violation_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        violation_spannableString.setSpan(new RelativeSizeSpan(1.3f), violation_start, violation_end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        violation_text.setText(violation_spannableString);

        // violation2
        TextView violation2_text = (TextView)findViewById(R.id.textView7); //텍스트 변수 선언
        String violation2_content = violation2_text.getText().toString(); //텍스트 가져옴.
        SpannableString violation2_spannableString = new SpannableString(violation2_content); //객체 생성

        String violation2_word ="1만원";

        int violation2_start = violation2_content.indexOf(violation2_word);
        int violation2_end = violation2_start + violation2_word.length();

        violation2_spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), violation2_start, violation2_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        violation2_spannableString.setSpan(new StyleSpan(Typeface.BOLD), violation2_start, violation2_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        violation2_spannableString.setSpan(new RelativeSizeSpan(1.3f), violation2_start, violation2_end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        violation2_text.setText(violation2_spannableString);
    }
    public void onButton3Clicked(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.educar.co.kr/honeQ/product/kickboard/kickboardIntro?origin=m"));
        startActivity(intent);
    }
    public void onButton4Clicked(View view){
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(intent);
    }
}