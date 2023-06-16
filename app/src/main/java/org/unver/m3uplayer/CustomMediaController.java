package org.unver.m3uplayer;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomMediaController implements View.OnClickListener {

    private final MediaPlayer mediaPlayer;
    private PlayerFragment playerFragment;
    private ViewGroup anchorView;
    private ImageButton subtitleButton;
    private ImageButton audioButton;
    private ImageButton speedButton;
    private ImageButton tamEkranButton;

    private ImageButton oncekiMedia;
    private ImageButton sonrakiMedia;

    private SeekBar seekBar;
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
    private ImageButton yenidenYukleButton;
    private ImageButton ileri30sec;
    private ImageButton geri5sec;
    private ImageButton pausePlay;

    public CustomMediaController(PlayerFragment mainActivity, ViewGroup anchorView, MediaPlayer mediaPlayer) {
        this.playerFragment = mainActivity;
        this.anchorView = anchorView;
        this.mediaPlayer = mediaPlayer;
        init();
    }

    private static EkranYer[] ekranYerler = new EkranYer[]{
            new EkranYer(R.drawable.baseline_fullscreen_24, 3, Gravity.TOP | Gravity.END, 0, 3, 0, 0), //0 Ekranı kapla tuşu yeri
            new EkranYer(R.drawable.baseline_autorenew_24, 3, Gravity.TOP | Gravity.END, 0, 7, 0, 0), //0 reLoad tuşu

            new EkranYer(R.drawable.baseline_skip_previous_24, 4, Gravity.CENTER_VERTICAL | Gravity.START, 2, 0, 0, 0), //Sonraki Media
            new EkranYer(R.drawable.baseline_skip_next_24, 4, Gravity.CENTER_VERTICAL | Gravity.END, 0, 2, 0, 0),

            new EkranYer(R.drawable.baseline_backward_5_24, 4, Gravity.CENTER_VERTICAL | Gravity.START, 7, 0, 0, 0), //Sonraki Media
            new EkranYer(R.drawable.baseline_forward_30_24, 4, Gravity.CENTER_VERTICAL | Gravity.END, 0, 7, 0, 0),
            new EkranYer(R.drawable.baseline_play_circle_outline_24, 4, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0),

    };//Önceki Media


    private void init() {
        tamEkran = false;
        frameLayout = new FrameLayout(playerFragment.mainActivity);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        anchorView.addView(frameLayout);

        //subtitleButton = createButton(R.drawable.baseline_subtitles_24, 0);
        //audioButton = createButton(R.drawable.baseline_volume_up_24, 80);
        //speedButton = createButton(R.drawable.baseline_speed_24, 160);
        tamEkranButton = createButton(0);
        yenidenYukleButton = createButton(1);
        oncekiMedia = createButton(2);
        sonrakiMedia = createButton(3);
        geri5sec = createButton(4);
        ileri30sec = createButton(5);
        pausePlay = createButton(6);

        seekBar = createSeekBar();
        // Gizlenme işlemlerini yönetmek için Handler oluştur
        hideHandler = new Handler();
    }

    private SeekBar createSeekBar() {
        SeekBar customSeekBar = new SeekBar(playerFragment.mainActivity);
        FrameLayout.LayoutParams seekBarLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        customSeekBar.setBackgroundColor(Color.TRANSPARENT);
        //customSeekBar.setVisibility(View.GONE);
        seekBarLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        seekBarLayoutParams.rightMargin = (int) (anchorView.getWidth() * 0.3); // Ayarlamak istediğiniz oranı belirleyin
        seekBarLayoutParams.leftMargin = (int) (anchorView.getWidth() * 0.3); // Ayarlamak istediğiniz oranı belirleyin
        seekBarLayoutParams.bottomMargin = (int) (anchorView.getHeight() * 0.05); // Ayarlamak istediğiniz oranı belirleyin
        frameLayout.addView(customSeekBar, seekBarLayoutParams);
        customSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.setTime(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return customSeekBar;
    }


    private ImageButton createButton(int buttonId) {
        ImageButton button = new ImageButton(playerFragment.mainActivity);
        button.setImageResource(ekranYerler[buttonId].resId);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setScaleType(ImageView.ScaleType.FIT_XY);
        //button.setVisibility(View.GONE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ekranYerler[buttonId].buyukluk * 24,
                ekranYerler[buttonId].buyukluk * 36
        );

        layoutParams.gravity = ekranYerler[buttonId].gravity;
        layoutParams.rightMargin = ekranYerler[buttonId].sagMargin * 24;
        layoutParams.leftMargin = ekranYerler[buttonId].solMargin * 24;
        layoutParams.topMargin = ekranYerler[buttonId].ustMargin * 36;
        layoutParams.bottomMargin = ekranYerler[buttonId].altMargin * 36;

        frameLayout.addView(button, layoutParams);
        button.setOnClickListener(this);
        return button;
    }

    @Override
    public void onClick(View view) {
        if(m3uBilgi == null) return;
        resetTimeout();
        // Tuşlara tıklandığında gerçekleştirilecek işlemleri burada tanımlayabilirsiniz
        if (view == subtitleButton) {
            // Altyazı tuşuna tıklandığında yapılacak işlemler
        } else if (view == audioButton) {
            // Ses tuşuna tıklandığında yapılacak işlemler
        } else if (view == speedButton) {
            // Hız tuşuna tıklandığında yapılacak işlemler
        } else if (view == tamEkranButton) {
            TamEkranDegistir();
            // Tam ekran tuşuna tıklandığında yapılacak işlemler
        }
    }

    public void TamEkrandanCik() {
        if(tamEkran)
            TamEkranDegistir();
    }

    public void TamEkranDegistir() {
        tamEkran = !tamEkran;
        if (tamEkran) {
            Log.d("M3U", "Tam Ekrana Geçiş Tuşu");
            tamEkranButton.setImageResource(R.drawable.baseline_fullscreen_exit_24);
        } else {
            Log.d("M3U", "Tam Ekrandan Çıkış Tuşu");
            tamEkranButton.setImageResource(R.drawable.baseline_fullscreen_24);
        }
        playerFragment.TamEkranDegistir(tamEkran);
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
        if (this.visibleHide == View.VISIBLE)
            hideHandler.postDelayed(hideRunnable, timeout);
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
        if (this.visibleHide == View.VISIBLE)
            resetTimeout();
    }

    public void GorunumDegistir() {
        if (this.visibleHide == View.GONE)
            showHide(View.VISIBLE);
        else
            showHide(View.GONE);
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
            seekBar.setMax((int) (totalMs / 1000));
            seekBar.setProgress(0);
        } else
            seekBar.setVisibility(View.GONE);
    }

    public void ZamanAyarla(long timeChanged) {
        currentMS = timeChanged;
        int lCurrentSec = (int) (timeChanged / 1000);
        if (lCurrentSec != currentSec) {
            currentSec = lCurrentSec;
            if (totalMs > 0)
                seekBar.setProgress(currentSec);
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


    private static class EkranYer {
        private final int buyukluk;
        public final int resId;
        public final int gravity;
        public final int solMargin;
        public final int sagMargin;
        public final int ustMargin;
        public final int altMargin;

        public EkranYer(int resId, int buyukluk, int gravity, int solMargin, int sagMargin, int ustMargin, int altMargin) {
            this.resId = resId;
            this.buyukluk = buyukluk;
            this.gravity = gravity;
            this.solMargin = solMargin;
            this.sagMargin = sagMargin;
            this.ustMargin = ustMargin;
            this.altMargin = altMargin;
        }
    }
}
