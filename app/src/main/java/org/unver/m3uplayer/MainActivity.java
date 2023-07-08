package org.unver.m3uplayer;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.RangeSlider;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    public boolean TVInfoOkundu = false;
    private NavigationView navigationView;
    public DrawerLayout drawerLayout;
    private YayinFragment anaFragment = null;
    private AyarlarFragment ayarlarFragment = null;
    private GrupFragment grupFragment;
    public M3UBilgi.M3UTur aktifTur = M3UBilgi.M3UTur.tv;
    AutoCompleteTextView turSecDDL;
    private EditText filtreAlan;
    private View menParolaGir;
    private View menAyarlar;
    private View menkullaniciGruplari;
    private AlertDialog parolaAldialog;
    private TextView msTur;
    private AlertDialog turAldialog;
    private RangeSlider rangeSliderPuan;
    private RangeSlider rangeSliderYil;
    private ArrayAdapter<String> aaTur;
    private int internettenCekiliyor;
    public Handler handler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        M3UVeri.OkuBakayim(this);
        ProgSettings.AyarlariOku();

        anaFragment = new YayinFragment(this);
        AyarlariKapat(true);

        String[] turListesi = getResources().getStringArray(R.array.turListesi);
        aaTur = new ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, turListesi);
        turSecDDL = findViewById(R.id.turSec);
        turSecDDL.setAdapter(aaTur);
        turSecDDL.setText(aaTur.getItem(0), false);

        filtreAlan = (EditText) findViewById(R.id.filtreAd);
        msTur = findViewById(R.id.msTur);
        turAldialog = DialogTanimlar.TurAl(this, FilmTurYonetim.TurIsimler(1), msTur);
        DialogTanimlar.TMDBDialogOl(this);
        msTur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turAldialog.show();
            }
        });

        turSecDDL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                anaFragment.TurSecildi(position, false);
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

        menkullaniciGruplari = findViewById(R.id.kullaniciGruplari);
        menkullaniciGruplari.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                grupFragment = new GrupFragment(that);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, grupFragment).commit();
            }
        });

        RangeSlider rsGunSay = findViewById(R.id.rsGunSay);
        CheckBox cbSadeceYeni = findViewById(R.id.cbSadeceYeni);

        ImageButton btnAra = findViewById(R.id.btnAra);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (grupFragment != null) {
                    AyarlariKapat(false);
                } else if (ayarlarFragment != null) {
                    AyarlariKapat(false);
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

        btnAra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anaFragment.filtre.SadeceYeniAyarla(cbSadeceYeni.isChecked(), rsGunSay.getValues());
                anaFragment.filtre.isimFiltreStr = filtreAlan.getText().toString();
                anaFragment.filtre.PuanAyarla(rangeSliderPuan.getValues());
                anaFragment.filtre.YilAyarla(rangeSliderYil.getValues(), M3UVeri.minYil, yil);
                anaFragment.filtre.TurAyarla(msTur.getText().toString());
                anaFragment.TurSecildi(M3UVeri.SiraBul(aktifTur),false);
            }
        });
    }

    public void AyarlariKapat(boolean baslangictan) {
        if (grupFragment != null)
            grupFragment = null;
        if (ayarlarFragment != null)
            ayarlarFragment = null;
        //anaFragment = new PlayerFragment(this, baslangictan);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, anaFragment).commit();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ArkaPlanIslemleri.kapat();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ayarlarFragment != null)
            ayarlarFragment.YonlendirmeAyarla();
        else
            anaFragment.YonlendirmeAyarla();
    }

    public void GoruntulenenYap() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public void YatayYap() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    public void Cekildi() {
        internettenCekiliyor = 0;
        anaFragment.InternettenCekmeIkon(internettenCekiliyor);
    }

    public void btnAcClicked(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void setAktifTur(int aktifTurPos) {
        turSecDDL.setText(aaTur.getItem(aktifTurPos), false);
        this.aktifTur = M3UVeri.TurBul(aktifTurPos);
    }

    public void internettenCekiliyorYap(int state) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                internettenCekiliyor = state;
                anaFragment.InternettenCekmeIkon(internettenCekiliyor);
            }
        }, 50);
    }

    public boolean M3UCekiliyorMu() {
        return this.internettenCekiliyor == 1;
    }
    public boolean VeriCekiliyorMu() {
        return this.internettenCekiliyor != 0;
    }
}