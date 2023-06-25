package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;


import androidx.annotation.NonNull;

import com.google.android.material.slider.RangeSlider;

import org.videolan.libvlc.MediaPlayer;

import java.util.Date;

public class CustomMediaController implements View.OnClickListener {
    private final MediaPlayer mediaPlayer;
    private PlayerFragment playerFragment;
    private ViewGroup anchorView;

    private RangeSlider seekBar;
    private long timeout;
    private int visibleHide;
    private Handler hideHandler;
    private FrameLayout frameLayout;
    public boolean tamEkran;
    private boolean durdurulabilir = true;
    private long currentMS;
    private int currentSec;
    private Date baslamaZamani;
    private int currentMin;
    private boolean yeterinceSeyrettik;
    private M3UBilgi m3uBilgi = null;
    private long totalMs;
    private boolean aranaBilir;

    private int birimW = 6;
    private int birimH = 6;

    public CustomMediaController(PlayerFragment mainActivity, ViewGroup anchorView, MediaPlayer mediaPlayer) {
        this.playerFragment = mainActivity;
        this.anchorView = anchorView;
        this.mediaPlayer = mediaPlayer;
        init();
    }

    private boolean GetWH() {
        int w = this.anchorView.getWidth();
        if (w > 0) {
            birimW = w / 60;
            if (birimW != birimH) {
                birimH = birimW;
                return true;
            }
        }
        return false;
    }

    private static EkranYer[] ekranYerler = new EkranYer[]{
            new EkranYer(R.drawable.baseline_fullscreen_24, 4, Gravity.TOP | Gravity.END, 0, 3, 0, 0), //0 Ekranı kapla tuşu yeri
            new EkranYer(R.drawable.baseline_autorenew_24, 4, Gravity.TOP | Gravity.END, 0, 8, 0, 0), //0 reLoad tuşu

            new EkranYer(R.drawable.baseline_skip_previous_24, 6, Gravity.CENTER_VERTICAL | Gravity.START, 4, 0, 0, 0), //Sonraki Media
            new EkranYer(R.drawable.baseline_backward_5_24, 6, Gravity.CENTER_VERTICAL | Gravity.START, 11, 03, 0, 0), //Sonraki Media

            new EkranYer(R.drawable.baseline_pause_circle_outline_24, 9, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0),

            new EkranYer(R.drawable.baseline_forward_30_24, 6, Gravity.CENTER_VERTICAL | Gravity.END, 0, 9, 0, 0),

            new EkranYer(R.drawable.baseline_skip_next_24, 6, Gravity.CENTER_VERTICAL | Gravity.END, 0, 2, 0, 0),
    };

    private void init() {
        tamEkran = false;
        frameLayout = new FrameLayout(playerFragment.mainActivity);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        GetWH();
        anchorView.addView(frameLayout);

        for (int i = 0; i < ekranYerler.length; i++)
            createButton(i);

        seekBar = createSeekBar();
        // Gizlenme işlemlerini yönetmek için Handler oluştur
        hideHandler = new Handler();
    }

    private boolean TuslariAyarla() {
        if (GetWH()) {
            for (int i = 0; i < ekranYerler.length; i++) {
                FrameLayout.LayoutParams layP = (FrameLayout.LayoutParams) ekranYerler[i].button.getLayoutParams();
                layP.width = ekranYerler[i].buyukluk * birimW;
                layP.height = ekranYerler[i].buyukluk * birimH;

                layP.rightMargin = ekranYerler[i].sagMargin * birimW;
                layP.leftMargin = ekranYerler[i].solMargin * birimW;
                layP.topMargin = ekranYerler[i].ustMargin * birimH;
                layP.bottomMargin = ekranYerler[i].altMargin * birimH;
                ekranYerler[i].button.requestLayout();
            }
            return true;
        }
        return false;
    }

    private void createButton(int buttonId) {
        ImageButton button = new ImageButton(playerFragment.mainActivity);
        button.setImageResource(ekranYerler[buttonId].resId);
        //button.setBackgroundResource(R.drawable.baseline_transparent_24);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //button.setVisibility(View.GONE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(birimW * ekranYerler[buttonId].buyukluk, birimH * ekranYerler[buttonId].buyukluk);

        layoutParams.gravity = ekranYerler[buttonId].gravity;
        layoutParams.rightMargin = ekranYerler[buttonId].sagMargin * birimW;
        layoutParams.leftMargin = ekranYerler[buttonId].solMargin * birimW;
        layoutParams.topMargin = ekranYerler[buttonId].ustMargin * birimH;
        layoutParams.bottomMargin = ekranYerler[buttonId].altMargin * birimH;

        frameLayout.addView(button, layoutParams);
        button.setOnClickListener(this);
        ekranYerler[buttonId].button = button;
    }

    private RangeSlider createSeekBar() {
        RangeSlider customSeekBar = new RangeSlider(playerFragment.mainActivity);
        FrameLayout.LayoutParams seekBarLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customSeekBar.setBackgroundColor(Color.TRANSPARENT);
        //customSeekBar.setVisibility(View.GONE);
        seekBarLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        seekBarLayoutParams.rightMargin = (int) (anchorView.getWidth() * 0.3); // Ayarlamak istediğiniz oranı belirleyin
        seekBarLayoutParams.leftMargin = (int) (anchorView.getWidth() * 0.3); // Ayarlamak istediğiniz oranı belirleyin
        seekBarLayoutParams.bottomMargin = (int) (anchorView.getHeight() * 0.05); // Ayarlamak istediğiniz oranı belirleyin
        customSeekBar.setValueFrom(0);
        customSeekBar.setValueTo(1f);
        customSeekBar.setValues(0f);
        frameLayout.addView(customSeekBar, seekBarLayoutParams);
        customSeekBar.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.setTime((long)value * 1000);
                    resetTimeout();
                }
            }
        });
        customSeekBar.setLabelFormatter(value -> {
            long v = (long)value;
            int dak = (int) (v/60);
            int sec = (int) (v%60);

            return dak+":"+String.format("%02d", sec);
        });

        return customSeekBar;
    }


    @Override
    public void onClick(View view) {
        if (m3uBilgi == null) return;
        resetTimeout();
        // Tuşlara tıklandığında gerçekleştirilecek işlemleri burada tanımlayabilirsiniz
        if (view == ekranYerler[0].button) {
            TamEkranDegistir();
            // Tam ekran tuşuna tıklandığında yapılacak işlemler
        }
//        else if (view == ekranYerler[1].button) {
//            // Altyazı tuşuna tıklandığında yapılacak işlemler
//        } else if (view == ekranYerler[1].button) {
//            // Ses tuşuna tıklandığında yapılacak işlemler
//        } else if (view == ekranYerler[1].button) {
//            // Hız tuşuna tıklandığında yapılacak işlemler
//        }
    }

    public void TamEkrandanCik() {
        if (tamEkran) TamEkranDegistir();
    }

    public void TamEkranDegistir() {
        tamEkran = !tamEkran;
        if (tamEkran) {
            Log.d("M3U", "Tam Ekrana Geçiş Tuşu");
            ekranYerler[0].button.setImageResource(R.drawable.baseline_fullscreen_exit_24);
        } else {
            Log.d("M3U", "Tam Ekrandan Çıkış Tuşu");
            ekranYerler[0].button.setImageResource(R.drawable.baseline_fullscreen_24);
        }
        playerFragment.TamEkranDegistir(tamEkran);
        TuslariAyarla();
    }


    public void show(long timeout) {
        // Timeout süresini ayarla
        // Kontrolleri göster
        this.timeout = timeout;
        showHide(View.VISIBLE);

        // Timeout süresi bitince kontrolleri otomatik olarak gizle
    }

    public void resetTimeout() {
        // Timeout süresini sıfırla
        hideHandler.removeCallbacks(hideRunnable);
        if (this.visibleHide == View.VISIBLE) hideHandler.postDelayed(hideRunnable, timeout);
    }

    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            showHide(View.GONE);
            ;
        }
    };

    public void showHide(int visibleHide) {
        frameLayout.setVisibility(visibleHide);
//        for (View child: kontrolListesi) {
//            child.setVisibility(visibleHide);
//        }
        this.visibleHide = visibleHide;
        if (this.visibleHide == View.VISIBLE) resetTimeout();
    }

    public void GorunumDegistir() {
        if (this.visibleHide == View.GONE) showHide(View.VISIBLE);
        else showHide(View.GONE);
    }

    public void BilgiAyarla() {
        this.durdurulabilir = mediaPlayer.isSeekable();
        this.aranaBilir = mediaPlayer.isSeekable();
        this.totalMs = mediaPlayer.getLength();
        baslamaZamani = new Date();
        yeterinceSeyrettik = false;
        Log.d("PlayerFragment", m3uBilgi.tvgName + "Playing:" + ":" + totalMs + "/" + aranaBilir);
        if (totalMs > 0) {
            seekBar.setVisibility(View.VISIBLE);
            seekBar.setValueTo((float) (totalMs / 1000));
            seekBar.setValues(0f);
        } else seekBar.setVisibility(View.GONE);
    }

    public void ZamanAyarla(long timeChanged) {
        currentMS = timeChanged;
        int lCurrentSec = (int) (timeChanged / 1000);
        if (lCurrentSec != currentSec) {
            currentSec = lCurrentSec;
            if (totalMs > 0) seekBar.setValues((float)currentSec);
            if (!yeterinceSeyrettik) {
                Date simdZaman = new Date();
                long farkZaman = (simdZaman.getTime() - baslamaZamani.getTime()) / 60000;
                if (farkZaman >= 5) {
                    yeterinceSeyrettik = true;
                    TarihceyeEkle();
                }
            }
            int lCurrentMin = currentSec / 60;
            if (currentMin != lCurrentMin) {
                currentMin = lCurrentMin;
            }
        }
    }

    private void TarihceyeEkle() {
    }

    public void m3uBilgiAyarla(M3UBilgi m3uBilgi) {
        this.m3uBilgi = m3uBilgi;
    }

    public void BuyuklukAyarla() {
        if (TuslariAyarla()) {
            this.anchorView.requestLayout();
            Log.d("CustomMediaController", "BuyuklukAyarla");
        }
    }

    private static class EkranYer {
        private final int buyukluk;
        public final int resId;
        public final int gravity;
        public final int solMargin;
        public final int sagMargin;
        public final int ustMargin;
        public final int altMargin;
        public ImageButton button;

        public EkranYer(int resId, int buyukluk, int gravity, int solMargin, int sagMargin, int ustMargin, int altMargin) {
            this.resId = resId;
            this.buyukluk = buyukluk;
            this.gravity = gravity;
            this.solMargin = solMargin;
            this.sagMargin = sagMargin;
            this.ustMargin = ustMargin;
            this.altMargin = altMargin;
            button = null;
        }
    }
}
