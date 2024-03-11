package com.doan.music.activities;

import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.doan.music.R;
import com.doan.music.adapter.ViewPagerAdapter;
import com.doan.music.models.ModelManager;
import com.doan.music.views.MainPlayerView;
import com.doan.music.views.MiniControlView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sothree.slidinguppanel.PanelSlideListener;
import com.sothree.slidinguppanel.PanelState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1000;
    public static WeakReference<ModelManager> managerWeakReference;
    private SlidingUpPanelLayout slidingUpPanel;
    private LinearLayout miniControlLayout;
    private RelativeLayout mainPlayerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ViewPager2 vpMainContent;
    private DrawerLayout drawerLayout;
    private ModelManager modelManager;
    private MainPlayerView mainPlayerView;
    private MiniControlView miniControlView;

    public static ModelManager getModelManager() {
        return managerWeakReference.get();
    }

    public MainPlayerView getMainPlayerView() {
        return mainPlayerView;
    }

    public MiniControlView getMiniControlView() {
        return miniControlView;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("", "onOptionsItemSelected: " + item.getTitle());
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slidingUpPanel = findViewById(R.id.slidingUpPanel);
        vpMainContent = findViewById(R.id.content);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        mainPlayerLayout = findViewById(R.id.mainPlayer);
        miniControlLayout = findViewById(R.id.miniControl);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("SharedPreferences", "notifications: " + sharedPref.getBoolean("notifications", false));

        // init drawer |
        //             | init appbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //             | init actionbar
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        //             | init drawer nav
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //             | init navView click event
        NavigationView navigationView = findViewById(R.id.navigationView);

        if (sharedPref.getBoolean("IsLogin", false)) {
            navigationView.getMenu().setGroupVisible(R.id.id2, false);
            navigationView.getMenu().setGroupVisible(R.id.id3, true);
        } else {
            navigationView.getMenu().setGroupVisible(R.id.id2, true);
            navigationView.getMenu().setGroupVisible(R.id.id3, false);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            boolean result = true;

            if (itemId == R.id.nav_music) {
                vpMainContent.setCurrentItem(0);
            } else if (itemId == R.id.nav_settings) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                drawerLayout.close();
            } else if (itemId == R.id.nav_login) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                drawerLayout.close();
            } else if (itemId == R.id.nav_signup) {
                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);
                drawerLayout.close();
            } else if (itemId == R.id.nav_logout) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("IsLogin", false);
                editor.putString("Username", "");
                editor.putString("Password", "");
                editor.apply();

                Intent i = new Intent(MainActivity.this, SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                drawerLayout.close();
                finish();
            } else {
                result = false;
            }

            drawerLayout.close();
            return result;
        });
        //             | banner layout
        RelativeLayout headerLayout = (RelativeLayout) navigationView.getHeaderView(0);
        ImageView header_banner = headerLayout.findViewById(R.id.header_banner);

        String bannerUri = sharedPref.getString("BANNER_URI", "");
        if (!bannerUri.isEmpty()) {
            header_banner.setImageURI(Uri.parse(bannerUri));
        }

        ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                Intent intent = result.getData();
                if (intent == null) return;

                Uri imageUri = intent.getData();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("BANNER_URI", imageUri.toString());
                editor.apply();
                header_banner.setImageURI(imageUri);
            }
        });
        headerLayout.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult.launch(i);
        });
        // drawer done |

        slidingUpPanel.setDragView(R.id.miniControl);
        slidingUpPanel.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(@NonNull View view, float v) {
                miniControlLayout.setAlpha((-v + 1));
                miniControlLayout.setTranslationY(32 * v);
                mainPlayerLayout.setAlpha(Math.min(v * 2, 1f));
            }

            @Override
            public void onPanelStateChanged(@NonNull View view, @NonNull PanelState beforeState, @NonNull PanelState afterState) {
                if (beforeState == PanelState.DRAGGING) {
                    if (afterState == PanelState.EXPANDED) {
                        miniControlLayout.setVisibility(View.GONE);
                        vpMainContent.setVisibility(View.GONE);
                        slidingUpPanel.setDragView(R.id.holdSlide);
                    } else if (afterState == PanelState.COLLAPSED) {
                        slidingUpPanel.setDragView(R.id.miniControl);
                    } else if (afterState == PanelState.ANCHORED) {
                        slidingUpPanel.setPanelState(PanelState.EXPANDED);
                    }
                } else if (beforeState == PanelState.EXPANDED && afterState == PanelState.DRAGGING) {
                    miniControlLayout.setVisibility(View.VISIBLE);
                    vpMainContent.setVisibility(View.VISIBLE);
                }
            }
        });

        // permissions check, init music db
        if (
                ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        ) {
            modelManager = new ModelManager(this);
            managerWeakReference = new WeakReference<>(modelManager);
        } else {
            requestPermissions(new String[]{READ_MEDIA_IMAGES, READ_MEDIA_AUDIO}, REQUEST_PERMISSION_CODE);
        }

        // init tab
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        vpMainContent.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, vpMainContent, (tab, position) -> {
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
            }
        }).attach();

        mainPlayerView = new MainPlayerView(this, mainPlayerLayout, modelManager);
        miniControlView = new MiniControlView(this, miniControlLayout, modelManager);
        // Done layout init
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            modelManager = new ModelManager(this);
            managerWeakReference = new WeakReference<>(modelManager);
        } else {
            Toast.makeText(this, "Permission Denied. Exiting...", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanel.getPanelState() != PanelState.COLLAPSED) {
            slidingUpPanel.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modelManager.destroy();
        managerWeakReference = null;
    }
}