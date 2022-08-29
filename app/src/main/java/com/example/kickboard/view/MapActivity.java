package com.example.kickboard.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kickboard.DummyDatas;
import com.example.kickboard.R;
import com.example.kickboard.StationAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private Button button;
    private GoogleMap map;
    private LinearLayout showMarker;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        showMarker = findViewById(R.id.showMarker);
        showMarker.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        button = findViewById(R.id.qrButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(MapActivity.this).initiateScan();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        map = googleMap;

        LatLng userPosition = new LatLng(36.020629, 129.361420);

        MarkerOptions mo = new MarkerOptions();
        mo.position(userPosition);
        map.addMarker(mo);

        DummyDatas dummyDatas = new DummyDatas((BitmapDrawable)getResources().getDrawable(R.drawable.charging_station));
        for (int i=0; i<dummyDatas.markerOptions.size(); i++){
            map.addMarker(dummyDatas.markerOptions.get(i));
        }
        map.setOnMarkerClickListener(this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "QR인증 다시 해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Toast.makeText(this, "QR인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        showMarker.setVisibility(View.VISIBLE);
        TextView tv = showMarker.findViewById(R.id.textView5);
        tv.setText(marker.getTitle());
        StationAdapter adapter = new StationAdapter();
        for(Integer i=1; i<10; i++){
            adapter.addItem(i);
        }
        recyclerView.setAdapter(adapter);
        return true;
    }
}