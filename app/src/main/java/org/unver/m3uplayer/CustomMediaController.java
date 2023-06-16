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

import java.util.ArrayList;
import java.util.List;

public class CustomMediaController implements View.OnClickListener {

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

    public CustomMediaController(PlayerFragment mainActivity, ViewGroup anchorView) {
        this.playerFragment = mainActivity;
        this.anchorView = anchorView;
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

    private SeekBar createSeekBar()
    {
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
        if(this.visibleHide == View.VISIBLE)
            hideHandler.postDelayed(hideRunnable, timeout);
    }

    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            showHide(View.GONE);;
        }
    };

    public void showHide(int visibleHide) {
        frameLayout.setVisibility(visibleHide);
//        for (View child: kontrolListesi) {
//            child.setVisibility(visibleHide);
//        }
        this.visibleHide = visibleHide;
        if(this.visibleHide == View.VISIBLE)
            resetTimeout();
    }

    public void GorunumDegistir() {
        if(this.visibleHide == View.GONE)
            showHide(View.VISIBLE);
        else
            showHide(View.GONE);
    }
}
