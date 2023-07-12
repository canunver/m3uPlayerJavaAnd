package org.unver.m3uplayer;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AyarlarFragment extends Fragment {
    private final MainActivity mainActivity;
    private View currView;
    private View iptalTus;
    private View kaydetTus;
    private EditText txtm3uAdresA;
    private EditText txtm3uAdresB;
    private EditText txtm3uAdresC;
    private EditText txttmdbErisimAnahtar;
    private CheckBox chksonTvKanalBaslat;
    private CheckBox chktamEkranBaslat;
    private ConstraintSet landscapeConstraintSet;
    private ConstraintSet portraitConstraintSet;
    private ConstraintLayout ayarlarYerlersim;

    public AyarlarFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currView = inflater.inflate(R.layout.fragment_ayarlar, container, false);

        ayarlarYerlersim = currView.findViewById(R.id.ayarlarLayout);

        landscapeConstraintSet = new ConstraintSet();
        portraitConstraintSet = new ConstraintSet();

        landscapeConstraintSet.clone(ayarlarYerlersim);
        portraitConstraintSet.clone(ayarlarYerlersim);

        int sw = getResources().getConfiguration().screenWidthDp;
        int sh = getResources().getConfiguration().screenHeightDp;
        int yw = Math.max(sw, sh);

        yw = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, yw, getResources().getDisplayMetrics());

        int ow = (int) (yw * 0.75f);
        int lw = yw - ow;
        YonelimAyarla(R.id.lblm3uAdresA, lw, R.id.m3uAdresA, ow, ConstraintSet.PARENT_ID);
        YonelimAyarla(R.id.lblm3uAdresB, lw, R.id.m3uAdresB, ow, R.id.m3uAdresA);
        YonelimAyarla(R.id.lblm3uAdresC, lw, R.id.m3uAdresC, ow, R.id.m3uAdresB);
        YonelimAyarla(R.id.lbltmdbErisimAnahtar, lw, R.id.tmdbErisimAnahtar, ow, R.id.m3uAdresC);

        YonlendirmeAyarla();

        iptalTus = currView.findViewById(R.id.btnIptal);
        iptalTus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.AyarlariKapat(false);
            }
        });

        txtm3uAdresA = currView.findViewById(R.id.m3uAdresA);
        txtm3uAdresB = currView.findViewById(R.id.m3uAdresB);
        txtm3uAdresC = currView.findViewById(R.id.m3uAdresC);
        txttmdbErisimAnahtar = currView.findViewById(R.id.tmdbErisimAnahtar);
        chksonTvKanalBaslat = currView.findViewById(R.id.sonTvKanalBaslat);
        chktamEkranBaslat= currView.findViewById(R.id.tamEkranBaslat);

        txtm3uAdresA.setText(ProgSettings.m3u_internet_adresi_1);
        txtm3uAdresB.setText(ProgSettings.m3u_internet_adresi_2);
        txtm3uAdresC.setText(ProgSettings.m3u_internet_adresi_3);
        txttmdbErisimAnahtar.setText(ProgSettings.tmdb_erisim_anahtar);
        chksonTvKanalBaslat.setChecked(ProgSettings.son_tv_kanalini_oynatarak_basla);
        chktamEkranBaslat.setChecked(ProgSettings.tamEkranBaslat);

        kaydetTus = currView.findViewById(R.id.btnKaydet);
        kaydetTus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgSettings.m3u_internet_adresi_1 = txtm3uAdresA.getText().toString();
                ProgSettings.m3u_internet_adresi_2 = txtm3uAdresB.getText().toString();
                ProgSettings.m3u_internet_adresi_3 = txtm3uAdresC.getText().toString();
                ProgSettings.tmdb_erisim_anahtar = txttmdbErisimAnahtar.getText().toString();
                ProgSettings.son_tv_kanalini_oynatarak_basla = chksonTvKanalBaslat.isChecked();
                ProgSettings.tamEkranBaslat = chktamEkranBaslat.isChecked();

                ProgSettings.AyarlariYaz();
                mainActivity.AyarlariKapat(false);
            }
        });

        return currView;
    }

    private void YonelimAyarla(int lblId, int lw, int editId, int ew, int usttekiId) {
        landscapeConstraintSet.constrainWidth(lblId, lw);
        landscapeConstraintSet.constrainWidth(editId, ew);
        if (usttekiId == ConstraintSet.PARENT_ID) {
            landscapeConstraintSet.connect(lblId, ConstraintSet.TOP, usttekiId, ConstraintSet.TOP, 40);
            landscapeConstraintSet.connect(editId, ConstraintSet.TOP, usttekiId, ConstraintSet.TOP, 0);
        } else {
            landscapeConstraintSet.connect(lblId, ConstraintSet.TOP, usttekiId, ConstraintSet.BOTTOM, 40);
            landscapeConstraintSet.connect(editId, ConstraintSet.TOP, usttekiId, ConstraintSet.BOTTOM, 0);
        }

        landscapeConstraintSet.connect(lblId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        landscapeConstraintSet.connect(editId, ConstraintSet.START, lblId, ConstraintSet.END, 0);
    }

    public void YonlendirmeAyarla() {
        int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscapeConstraintSet.applyTo(ayarlarYerlersim);
        } else {
            portraitConstraintSet.applyTo(ayarlarYerlersim);
        }
        ayarlarYerlersim.requestLayout();
    }
}