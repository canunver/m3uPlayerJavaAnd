package org.unver.m3uplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private PlayerFragment currFragment;
    public M3UBilgi.M3UTur aktifTur = M3UBilgi.M3UTur.tv;
    AutoCompleteTextView actv;
    private EditText filtreAlan;

    @SuppressLint("MissingInflatedId")
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
        String[] turListesi = getResources().getStringArray(R.array.turListesi);
        ArrayAdapter<String> aaTur = new ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, turListesi);
        actv = findViewById(R.id.turSec);
        actv.setAdapter(aaTur);
        actv.setText(aaTur.getItem(0), false);

        filtreAlan = (EditText) findViewById(R.id.filtreAd);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currFragment.TurSecildi(position);
            }
        });
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

    public void btnAraClicked(View view) {
//        filtre.filtre = filtreAlan.getText().toString();
//        TurSecildi(M3UVeri.SiraBul(mainActivity.aktifTur));
//
//        imm.hideSoftInputFromWindow(filtreAlan.getWindowToken(), 0);
    }
}