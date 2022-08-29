package com.example.kickboard.model;

import android.graphics.Bitmap;

import com.example.kickboard.presenter.Contract;

public class BitmapModel {
    Contract.Presenter presenter;
    public static Bitmap bitmap;

    public BitmapModel(Contract.Presenter presenter){
        this.presenter = presenter;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }
    public Bitmap getBitmap(){ return this.bitmap; }

}
