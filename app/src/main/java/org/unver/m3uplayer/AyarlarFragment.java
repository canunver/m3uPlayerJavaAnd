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
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AyarlarFragment extends Fragment {
    private final MainActivity mainActivity;
    private EditText txtm3uAdresA;
    private EditText txtm3uAdresB;
    private EditText txtm3uAdresC;
    private EditText txttmdbErisimAnahtar;
    private CheckBox chksonTvKanalBaslat;
    private CheckBox chktamEkranBaslat;
    private ConstraintSet landscapeConstraintSet;
    private ConstraintSet portraitConstraintSet;
    private ConstraintLayout ayarlarYerlersim;
    private Spinner spntmdbErisimDil;

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
        View currView = inflater.inflate(R.layout.fragment_ayarlar, container, false);

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
        int lw = yw - ow - 20;
        YonelimAyarla(R.id.lblm3uAdresA, lw, R.id.m3uAdresA, ow, ConstraintSet.PARENT_ID);
        YonelimAyarla(R.id.lblm3uAdresB, lw, R.id.m3uAdresB, ow, R.id.m3uAdresA);
        YonelimAyarla(R.id.lblm3uAdresC, lw, R.id.m3uAdresC, ow, R.id.m3uAdresB);
        YonelimAyarla(R.id.lbltmdbErisimAnahtar, lw, R.id.tmdbErisimAnahtar, ow, R.id.m3uAdresC);
        YonelimAyarla(R.id.lbltmdbErisimDil, lw, R.id.tmdbErisimDil, ow, R.id.tmdbErisimAnahtar);

        YonlendirmeAyarla();

        View iptalTus = currView.findViewById(R.id.btnIptal);
        iptalTus.setOnClickListener(v -> mainActivity.AyarlariKapat());

        txtm3uAdresA = currView.findViewById(R.id.m3uAdresA);
        txtm3uAdresB = currView.findViewById(R.id.m3uAdresB);
        txtm3uAdresC = currView.findViewById(R.id.m3uAdresC);
        txttmdbErisimAnahtar = currView.findViewById(R.id.tmdbErisimAnahtar);
        spntmdbErisimDil = currView.findViewById(R.id.tmdbErisimDil);
        chksonTvKanalBaslat = currView.findViewById(R.id.sonTvKanalBaslat);
        chktamEkranBaslat = currView.findViewById(R.id.tamEkranBaslat);

        txtm3uAdresA.setText(OrtakAlan.m3uAdresAl(1));
        txtm3uAdresB.setText(OrtakAlan.m3uAdresAl(2));
        txtm3uAdresC.setText(OrtakAlan.m3uAdresAl(3));
        txttmdbErisimAnahtar.setText(OrtakAlan.tmdb_erisim_anahtar);
        spntmdbErisimDil.setSelection(OrtakAlan.tmdb_erisim_dil);
        chksonTvKanalBaslat.setChecked(OrtakAlan.son_tv_kanalini_oynatarak_basla);
        chktamEkranBaslat.setChecked(OrtakAlan.tamEkranBaslat);

        View kaydetTus = currView.findViewById(R.id.btnKaydet);
        kaydetTus.setOnClickListener(v -> {
            OrtakAlan.adresDegerAta(1, txtm3uAdresA.getText().toString());
            OrtakAlan.adresDegerAta(2, txtm3uAdresB.getText().toString());
            OrtakAlan.adresDegerAta(3, txtm3uAdresC.getText().toString());
            OrtakAlan.tmdb_erisim_anahtar = txttmdbErisimAnahtar.getText().toString();
            OrtakAlan.tmdb_erisim_dil = spntmdbErisimDil.getSelectedItemPosition();
            OrtakAlan.son_tv_kanalini_oynatarak_basla = chksonTvKanalBaslat.isChecked();
            OrtakAlan.tamEkranBaslat = chktamEkranBaslat.isChecked();

            OrtakAlan.AyarlariYaz();
            mainActivity.AyarlariKapat();
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