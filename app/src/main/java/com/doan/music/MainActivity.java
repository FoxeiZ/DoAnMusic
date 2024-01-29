package com.doan.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sothree.slidinguppanel.PanelSlideListener;
import com.sothree.slidinguppanel.PanelState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity {

    private SlidingUpPanelLayout slidingUpPanel;
    private LinearLayout miniControl;
    private RelativeLayout mainPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slidingUpPanel = findViewById(R.id.slidingUpPanel);
        ViewPager2 content = findViewById(R.id.content);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        mainPlayer = findViewById(R.id.mainPlayer);
        miniControl = findViewById(R.id.miniControl);

        slidingUpPanel.setDragView(R.id.miniControl);
        slidingUpPanel.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(@NonNull View view, float v) {
                miniControl.setAlpha((-v+1));
                mainPlayer.setAlpha(Math.min(v*2, 1f));
                if (v == 1f) {
                    miniControl.setVisibility(View.GONE);
                } else if (v > 0f) {
                    if (miniControl.getVisibility() == View.GONE) miniControl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPanelStateChanged(@NonNull View view, @NonNull PanelState panelState, @NonNull PanelState panelState1) {
            }
        });

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        content.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, content, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Songs");
                    break;
                case 1:
                    tab.setText("Playlists");
                    break;
                case 2:
                    tab.setText("Albums");
                    break;
                case 3:
                    tab.setText("Artists");
                    break;
                case 4:
                    tab.setText("Genres");
                    break;
            }
        }).attach();
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanel.getPanelState() != PanelState.COLLAPSED) {
            slidingUpPanel.setPanelState(PanelState.COLLAPSED);
        }
        else {
            super.onBackPressed();
        }
    }
}