package org.unver.m3uplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private PlayerFragment currFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        M3UVeri.OkuBakayim(this);
        currFragment = new PlayerFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("M3U", "onConfigurationChanged");
        currFragment.YonlendirmeAyarla();
    }

    public void GoruntulenenYap() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public void YatayYap() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    public void Cekildi() {
        //Toast.makeText(this, "Çekildi", Toast.LENGTH_LONG).show();
        //OkuBakayim();
    }

    public void btnAcClicked(View view) {

        // Set up the ActionBarDrawerToggle
        //anaYerlesim.setLeft(400);
        drawerLayout.openDrawer(GravityCompat.START);
        //navigationView.setVisibility(View.VISIBLE);

    }
}