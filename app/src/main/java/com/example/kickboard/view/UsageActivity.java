package com.example.kickboard.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kickboard.R;
import com.example.kickboard.view.fragments.Usage1Fragment;
import com.example.kickboard.view.fragments.Usage2Fragment;

public class UsageActivity extends FragmentActivity {
    private static final int NUM_PAGES=2;
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
                Intent intent = new Intent(getApplicationContext(), BillActivity.class);
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
                    return new Usage1Fragment();
                case 1:
                    return new Usage2Fragment();
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