package com.example.kickboard.presenter;

import android.graphics.Bitmap;

import com.example.kickboard.model.BitmapModel;

public class Presenter implements Contract.Presenter {
    Contract.View view;
    BitmapModel bitmapModel;

    public Presenter(Contract.View view){
        this.view = view;
        bitmapModel = new BitmapModel(this);
    }

    @Override
    public void sendBitmapImage(Bitmap bitmap) {
        bitmapModel.setBitmap(bitmap);
    }

    @Override
    public void takeBitmapImage() {
        view.setCircleImage(bitmapModel.getBitmap());
    }
}
