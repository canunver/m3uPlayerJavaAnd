package org.unver.m3uplayer;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.RangeSlider;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    public DrawerLayout drawerLayout;
    private PlayerFragment anaFragment = null;
    ;
    private AyarlarFragment ayarlarFragment = null;
    public M3UBilgi.M3UTur aktifTur = M3UBilgi.M3UTur.tv;
    AutoCompleteTextView turSecDDL;
    private EditText filtreAlan;
    private View menParolaGir;
    private View menAyarlar;
    private AlertDialog parolaAldialog;
    private TextView msTur;
    private AlertDialog turAldialog;
    private RangeSlider rangeSliderPuan;
    private RangeSlider rangeSliderYil;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        M3UVeri.OkuBakayim(this);

        AyarlariKapat();

        String[] turListesi = getResources().getStringArray(R.array.turListesi);
        ArrayAdapter<String> aaTur = new ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, turListesi);
        turSecDDL = findViewById(R.id.turSec);
        turSecDDL.setAdapter(aaTur);
        turSecDDL.setText(aaTur.getItem(0), false);

        filtreAlan = (EditText) findViewById(R.id.filtreAd);
        msTur = findViewById(R.id.msTur);
        turAldialog = DialogTanimlar.TurAl(this, FilmTurYonetim.TurIsimler(1), msTur);

        msTur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turAldialog.show();
            }
        });

        turSecDDL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                anaFragment.TurSecildi(position);
            }
        });

        parolaAldialog = DialogTanimlar.ParolaAl(this);
        menParolaGir = findViewById(R.id.parolaGir);
        menParolaGir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parolaAldialog.show();
            }
        });

        MainActivity that = this;
        menAyarlar = findViewById(R.id.ayarlar);
        menAyarlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                ayarlarFragment = new AyarlarFragment(that);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ayarlarFragment).commit();
            }
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (ayarlarFragment != null) {
                    AyarlariKapat();
                } else if (anaFragment.TamEkranMi()) {
                    anaFragment.TamEkrandanCik();
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        rangeSliderPuan = findViewById(R.id.rsPuan);
        rangeSliderPuan.setValues(0f, 100f);
        rangeSliderPuan.setStepSize(1);

        Calendar c = Calendar.getInstance();
        int yil = c.get(Calendar.YEAR);
        rangeSliderYil = findViewById(R.id.rsYil);
        rangeSliderYil.setValueTo(yil);
        rangeSliderYil.setValues((float) M3UVeri.minYil, (float) yil);
        rangeSliderYil.setValueFrom(M3UVeri.minYil);
        rangeSliderYil.setStepSize(1);
    }

    private void AyarlariKapat() {
        if(ayarlarFragment != null)
            ayarlarFragment = null;
        anaFragment = new PlayerFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, anaFragment).commit();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        anaFragment.YonlendirmeAyarla();
    }

    public void GoruntulenenYap() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public void YatayYap() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    public void Cekildi() {
    }

    public void btnAcClicked(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void btnAraClicked(View view) {
//        filtre.filtre = filtreAlan.getText().toString();
//        TurSecildi(M3UVeri.SiraBul(mainActivity.aktifTur));
//
//        imm.hideSoftInputFromWindow(filtreAlan.getWindowToken(), 0);
    }
}