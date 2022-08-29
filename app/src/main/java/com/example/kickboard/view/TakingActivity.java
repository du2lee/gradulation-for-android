package com.example.kickboard.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.kickboard.R;
import com.example.kickboard.presenter.Contract;
import com.example.kickboard.presenter.Presenter;

import de.hdodenhof.circleimageview.CircleImageView;

public class TakingActivity extends AppCompatActivity implements Contract.View{

    Presenter presenter;

    CircleImageView circleImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taking);

        presenter = new Presenter(this);
        circleImageView = findViewById(R.id.circleImage);

        presenter.takeBitmapImage();
    }

    @Override
    public void setCircleImage(Bitmap bitmap) {
        circleImageView.setImageBitmap(bitmap);
    }
}