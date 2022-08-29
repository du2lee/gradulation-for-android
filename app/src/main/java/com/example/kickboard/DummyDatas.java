package com.example.kickboard;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DummyDatas {
    public ArrayList<MarkerOptions> markerOptions = new ArrayList<>();

    public DummyDatas(BitmapDrawable bitmapdraw) {
        ArrayList<LatLng> latlngs = new ArrayList<LatLng>(){{
            add(new LatLng(36.013273, 129.350668));
            add(new LatLng(36.012695, 129.356899));
            add(new LatLng(36.024987, 129.372794));
            add(new LatLng(36.018801, 129.369763));
            add(new LatLng(36.033349, 129.368891));
            add(new LatLng(36.019512, 129.361894));
            add(new LatLng(36.031299, 129.364658));
            add(new LatLng(36.015782, 129.354582));
            add(new LatLng(36.025677, 129.360192));
            add(new LatLng(36.027017, 129.367614));
            add(new LatLng(36.021936, 129.366012));
            add(new LatLng(36.014322, 129.366743));
            add(new LatLng(36.01348, 129.36147));
            add(new LatLng(36.009239, 129.358693));
            add(new LatLng(36.029073, 129.372534));
            add(new LatLng(36.0219947, 129.352262));
            add(new LatLng(36.008293, 129.346369));
            add(new LatLng(36.008836, 129.35419));
            add(new LatLng(36.026291, 129.350486));
            add(new LatLng(36.007872, 129.341374));
            add(new LatLng(36.029071, 129.355663));
            add(new LatLng(36.040603, 129.368069));
            add(new LatLng(36.038943, 129.374183));
            add(new LatLng(36.033258, 129.376203));
        }};
        ArrayList<String> names = new ArrayList<String>(){{
            add("시외버스터미널");
            add("남부시장");
            add("동해시장");
            add("대해시장");
            add("죽도동민복지회관");
            add("상대시장");
            add("홈플러스");
            add("쌍용사거리");
            add("GS슈퍼마켓");
            add("고속버스터미널");
            add("대해초등학교");
            add("초원아파트");
            add("상도중학교");
            add("남구청(야구장)");
            add("동아타운사거리");
            add("포항우리병원");
            add("상도코아루");
            add("뱃머리평생학습원");
            add("양학시장");
            add("SK뷰2차");
            add("포항남부초등학교");
            add("CGV북포항");
            add("영보빌라");
            add("송도사거리");
        }};
        for (int i=0; i<latlngs.size(); i++){
            MarkerOptions mo = new MarkerOptions();
            mo.position(latlngs.get(i));
            mo.title(names.get(i));
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
            mo.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            markerOptions.add(mo);
        }
    }
}
