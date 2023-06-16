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
    private SeekBar seekBar;
    private ImageButton tamEkranButton;
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

    public CustomMediaController(PlayerFragment mainActivity, ViewGroup anchorView, MediaPlayer mediaPlayer) {
        this.playerFragment = mainActivity;
        this.anchorView = anchorView;
        this.mediaPlayer = mediaPlayer;
        init();
    }

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
        tamEkranButton = createButton(R.drawable.baseline_fullscreen_24, 0);
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
                if(fromUser){
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

    private ImageButton createButton(int iconResId, int marginRight) {
        ImageButton button = new ImageButton(playerFragment.mainActivity);
        button.setImageResource(iconResId);
        button.setBackgroundColor(Color.TRANSPARENT);
        //button.setVisibility(View.GONE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP );
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.gravity = Gravity.TOP | Gravity.END;
        layoutParams.rightMargin = marginRight;

        frameLayout.addView(button, layoutParams);
        button.setOnClickListener(this);
        return button;
    }

    @Override
    public void onClick(View view) {
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

    private void TamEkranDegistir() {
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

    public void BilgiAyarla(boolean pausable, boolean seekable, long totalMs) {
        this.totalMs = totalMs;
        baslamaZamani = new Date();
        yeterinceSeyrettik = false;
        Log.d("PlayerFragment", m3uBilgi.tvgName + "Playing:" + ":" + totalMs + "/" + seekable);
        if (totalMs > 0) {
            seekBar.setVisibility(View.VISIBLE);
            seekBar.setMax((int) (totalMs / 1000));
            seekBar.setProgress(0);
        } else
            seekBar.setVisibility(View.GONE);
        durdurulabilir = pausable;
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
}
