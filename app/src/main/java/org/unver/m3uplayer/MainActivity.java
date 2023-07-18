package org.unver.m3uplayer;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.google.android.material.slider.RangeSlider;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    public boolean TVInfoOkundu = false;
    public DrawerLayout drawerLayout;
    private YayinFragment anaFragment = null;
    private AyarlarFragment ayarlarFragment = null;
    private GrupFragment grupFragment;
    public M3UBilgi.M3UTur aktifTur = M3UBilgi.M3UTur.tv;
    AutoCompleteTextView turSecDDL;
    private EditText filtreAlan;
    private AlertDialog parolaAlDialog;
    private TextView msTur;
    private AlertDialog turAlDialog;
    private RangeSlider rangeSliderPuan;
    private RangeSlider rangeSliderYil;
    private ArrayAdapter<String> aaTur;
    private int internettenCekiliyor;
    public Handler handler;
    public InputMethodManager imm;
    public View switchAdultRL;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        InitAd();
        super.onCreate(savedInstanceState);
        handler = new Handler();

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        //NavigationView navigationView = findViewById(R.id.navigationView);
        M3UVeri.OkuBakayim(this);
        OrtakAlan.AyarlariOku(Resources.getSystem().getConfiguration().locale.getLanguage());

        AyarlariKapat();

        String[] turListesi = getResources().getStringArray(R.array.turListesi);
        aaTur = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, turListesi);
        turSecDDL = findViewById(R.id.turSec);
        turSecDDL.setAdapter(aaTur);
        turSecDDL.setText(aaTur.getItem(0), false);

        filtreAlan = (EditText) findViewById(R.id.filtreAd);
        msTur = findViewById(R.id.msTur);
        turAlDialog = DialogTanimlar.TurAl(this, FilmTurYonetim.TurIsimler(1), msTur);
        DialogTanimlar.TMDBDialogOl(this);
        msTur.setOnClickListener(v -> turAlDialog.show());

        turSecDDL.setOnItemClickListener((parent, view, position, id) -> anaFragment.TurSecildi(position, false));

        parolaAlDialog = DialogTanimlar.ParolaAl(this);
        View parolaGir = findViewById(R.id.parolaGir);
        parolaGir.setOnClickListener(v -> parolaAlDialog.show());

        MainActivity that = this;
        View menAyarlar = findViewById(R.id.ayarlar);
        menAyarlar.setOnClickListener(v -> {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            ayarlarFragment = new AyarlarFragment(that);
            anaFragment = null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ayarlarFragment).commit();
        });

        View kullaniciGruplari = findViewById(R.id.kullaniciGruplari);
        kullaniciGruplari.setOnClickListener(v -> {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            grupFragment = new GrupFragment(that);
            anaFragment = null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, grupFragment).commit();
        });

        RangeSlider rsGunSay = findViewById(R.id.rsGunSay);
        CheckBox cbSadeceYeni = findViewById(R.id.cbSadeceYeni);

        ImageButton btnAra = findViewById(R.id.btnAra);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (grupFragment != null) {
                    AyarlariKapat();
                } else if (ayarlarFragment != null) {
                    AyarlariKapat();
                } else if (anaFragment != null && anaFragment.TamEkranMi()) {
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

        imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        btnAra.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(filtreAlan.getWindowToken(), 0);
            anaFragment.filtre.SadeceYeniAyarla(cbSadeceYeni.isChecked(), rsGunSay.getValues());
            anaFragment.filtre.isimFiltreStr = filtreAlan.getText().toString();
            anaFragment.filtre.PuanAyarla(rangeSliderPuan.getValues());
            anaFragment.filtre.YilAyarla(rangeSliderYil.getValues(), M3UVeri.minYil, yil);
            anaFragment.filtre.TurAyarla(msTur.getText().toString());
            anaFragment.TurSecildi(M3UVeri.SiraBul(aktifTur), false);
        });

        SwitchCompat switchHidden = findViewById(R.id.switchHidden);
        switchHidden.setOnCheckedChangeListener((buttonView, isChecked) -> {
            OrtakAlan.gizlilerVar = isChecked;
            anaFragment.TurSecildi(M3UVeri.SiraBul(aktifTur), false);
        });
        switchAdultRL = findViewById(R.id.switchAdultRL);

        SwitchCompat switchAdult = findViewById(R.id.switchAdult);
        switchAdult.setOnCheckedChangeListener((buttonView, isChecked) -> {
            OrtakAlan.yetiskinlerVar = isChecked;
            anaFragment.TurSecildi(M3UVeri.SiraBul(aktifTur), false);
        });
    }


    public static final String APP_ID = "app2b8ae3a5c3b8457283";
    public static final String ZONE_ID = "vz1cc03d2d9b2a46fcab";
    public static final String ZONE_ID_TAM = "vzd72f3e456e404d4f93";

    //    public static final  String TAG = "AdColony Banner";
    private void InitAd() {
        AdColonyAppOptions appOptions = new AdColonyAppOptions()
                .setKeepScreenOn(true)
                .setTestModeEnabled(true); // Test modunu etkinleştirin

        AdColony.configure(this, appOptions, APP_ID);

        //AdColony.configure(this, APP_ID); //, ZONE_ID
    }

    public void AyarlariKapat() {
        boolean reklamGoster;
        if (ayarlarFragment != null || grupFragment != null) {
            ayarlarFragment = null;
            grupFragment = null;
            reklamGoster = true;
        } else {
            reklamGoster = false;
        }
        anaFragmentBaslat();
        if(reklamGoster)
            AdColony.requestInterstitial(ZONE_ID_TAM, new MyAdColonyInterstitialListener(this));
    }

    public void anaFragmentBaslat() {
        Log.d("REKLAM", "anaFragmentBaslat1 ");

        if(anaFragment == null) {
            Log.d("REKLAM", "anaFragmentBaslat2");
            anaFragment = new YayinFragment(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, anaFragment).commit();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            Log.d("REKLAM", "anaFragmentBaslat3");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ArkaPlanIslemleri.kapat();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (grupFragment != null)
            grupFragment.YonlendirmeAyarla();
        else if (ayarlarFragment != null)
            ayarlarFragment.YonlendirmeAyarla();
        else if (anaFragment != null)
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
        if (anaFragment != null)
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
        handler.postDelayed(() -> {
            internettenCekiliyor = state;
            if (anaFragment != null)
                anaFragment.InternettenCekmeIkon(internettenCekiliyor);
        }, 50);
    }

    @SuppressWarnings("unused")
    public boolean M3UCekiliyorMu() {
        return this.internettenCekiliyor == 1;
    }

    public boolean VeriCekiliyorMu() {
        return this.internettenCekiliyor != 0;
    }
}