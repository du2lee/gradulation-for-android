package com.example.kickboard.presenter;

import android.graphics.Bitmap;

public interface Contract {
    interface View {
        void setCircleImage(Bitmap bitmap);
    }
    interface Presenter {
        void sendBitmapImage(Bitmap bitmap);
        void takeBitmapImage();
    }


}
