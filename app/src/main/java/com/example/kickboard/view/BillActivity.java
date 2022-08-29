package com.example.kickboard.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kickboard.view.fragments.Bill1Fragment;
import com.example.kickboard.view.fragments.Bill2Fragment;
import com.example.kickboard.view.fragments.Bill3Fragment;
import com.example.kickboard.view.fragments.Bill4Fragment;
import com.example.kickboard.view.fragments.Bill5Fragment;
import com.example.kickboard.view.fragments.Bill6Fragment;
import com.example.kickboard.R;

public class BillActivity extends FragmentActivity {
    private static final int NUM_PAGES=6;
    private ViewPager2 viewPager2;
    private FragmentStateAdapter pagerAdapter;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);

        viewPager2 = findViewById(R.id.viewPager2);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager2.setAdapter(pagerAdapter);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch(position){
                case 0:
                    return new Bill1Fragment();
                case 1:
                    return new Bill2Fragment();
                case 2:
                    return new Bill3Fragment();
                case 3:
                    return new Bill4Fragment();
                case 4:
                    return new Bill5Fragment();
                case 5:
                    return new Bill6Fragment();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}