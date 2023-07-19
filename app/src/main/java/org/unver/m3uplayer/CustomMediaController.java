package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.slider.RangeSlider;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IMedia;

import java.util.Date;

public class CustomMediaController implements View.OnClickListener {
    private final MediaPlayer mediaPlayer;
    private final YayinFragment yayinFragment;
    private final ViewGroup anchorView;

    private RangeSlider zamanCubugu;
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
    public M3UBilgi m3uBilgi = null;
    public String bolum;
    public String sezon;
    private long totalMs;
    private boolean aranaBilir;
    //private boolean sesCubuguGizli = true;
    //private SeekBar sesCubugu;

    private int birimW = 6;
    private int birimH = 6;
    public M3UBilgi m3uBilgiOynayan;
    private Bolum aktifBolum;
    private boolean oynuyor;
    private boolean pauseBaslat;
    private Media.Track[] sesler = null;
    private Media.Track[] altyazilar = null;

    public CustomMediaController(YayinFragment mainActivity, ViewGroup anchorView, MediaPlayer mediaPlayer) {
        this.yayinFragment = mainActivity;
        this.anchorView = anchorView;
        this.mediaPlayer = mediaPlayer;
        init();
    }

    private boolean GetWH() {
        int w = this.anchorView.getWidth();
        if (w > 0) {
            birimW = w / 60;
            if (birimW > 24) birimW = 24;
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

            new EkranYer(R.drawable.baseline_skip_previous_24, 6, Gravity.CENTER_VERTICAL | Gravity.START, 2, 0, 0, 0), //Sonraki Media
            new EkranYer(R.drawable.baseline_backward_5_24, 6, Gravity.CENTER_VERTICAL | Gravity.START, 9, 0, 0, 0), //Sonraki Media

            new EkranYer(R.drawable.baseline_pause_circle_outline_24, 7, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0),

            new EkranYer(R.drawable.baseline_forward_30_24, 6, Gravity.CENTER_VERTICAL | Gravity.END, 0, 9, 0, 0),
            new EkranYer(R.drawable.baseline_skip_next_24, 6, Gravity.CENTER_VERTICAL | Gravity.END, 0, 2, 0, 0),
            new EkranYer(R.drawable.konusma, 4, Gravity.TOP | Gravity.END, 0, 13, 0, 0),
            new EkranYer(R.drawable.baseline_subtitles_24, 4, Gravity.TOP | Gravity.END, 0, 18, 0, 0),
//            new EkranYer(R.drawable.baseline_music_note_24, 4, Gravity.TOP | Gravity.END, 0, 23, 0, 0),
    };

    private void init() {
        tamEkran = false;
        frameLayout = new FrameLayout(yayinFragment.mainActivity);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        GetWH();
        anchorView.addView(frameLayout);

        for (int i = 0; i < ekranYerler.length; i++)
            createButton(i);

        zamanCubugu = zamanCubuguOlustur();
//        sesCubugu = sesCubuguOlustur(Gravity.TOP | Gravity.END);
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

            FrameLayout.LayoutParams zamanCubuguLayoutParams = (FrameLayout.LayoutParams) zamanCubugu.getLayoutParams();
            zamanCubuguLayoutParams.rightMargin = (int) (anchorView.getWidth() * 0.01); // Ayarlamak istediğiniz oranı belirleyin
            zamanCubuguLayoutParams.leftMargin = (int) (anchorView.getWidth() * 0.01); // Ayarlamak istediğiniz oranı belirleyin
            zamanCubuguLayoutParams.bottomMargin = (int) (anchorView.getHeight() * 0.005); // Ayarlamak istediğiniz oranı belirleyin

            return true;
        }
        return false;
    }

//    private int SesSagBul() {
//        return 23 * birimW - birimW * 4;
//    }
//
//    private int SesUstBul() {
//        return 8 * birimH;
//    }

    private void createButton(int buttonId) {
        ImageButton button = new ImageButton(yayinFragment.mainActivity);
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

    private RangeSlider zamanCubuguOlustur() {
        RangeSlider zamanCubugu = new RangeSlider(yayinFragment.mainActivity);
        FrameLayout.LayoutParams zamanCubuguLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        zamanCubugu.setBackgroundColor(Color.TRANSPARENT);
        zamanCubuguLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        zamanCubuguLayoutParams.rightMargin = (int) (anchorView.getWidth() * 0.3); // Ayarlamak istediğiniz oranı belirleyin
        zamanCubuguLayoutParams.leftMargin = (int) (anchorView.getWidth() * 0.3); // Ayarlamak istediğiniz oranı belirleyin
        zamanCubuguLayoutParams.bottomMargin = (int) (anchorView.getHeight() * 0.05); // Ayarlamak istediğiniz oranı belirleyin
        zamanCubugu.setValueFrom(0);
        zamanCubugu.setValueTo(1f);
        zamanCubugu.setValues(0f);
        frameLayout.addView(zamanCubugu, zamanCubuguLayoutParams);
        zamanCubugu.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.setTime((long) value * 1000);
                    resetTimeout();
                }
            }
        });

        zamanCubugu.setLabelFormatter(value -> {
            long v = (long) value;
            int dak = (int) (v / 60);
            int sec = (int) (v % 60);

            return dak + ":" + String.format("%02d", sec);
        });

        return zamanCubugu;
    }

//    private SeekBar sesCubuguOlustur(int gravity) {
//        SeekBar sesCubugu = new SeekBar(playerFragment.mainActivity);
//        FrameLayout.LayoutParams sesCubuguLayoutParams = new FrameLayout.LayoutParams(birimW * 12, birimH * 3);
//        sesCubugu.setBackgroundColor(Color.GREEN);
//        sesCubuguLayoutParams.gravity = gravity;
//        sesCubuguLayoutParams.topMargin = SesUstBul();
//        sesCubuguLayoutParams.rightMargin = SesSagBul();
//        sesCubugu.setRotation(90);
//
//        sesCubugu.setPadding (0, 0 ,0 , 0);
//
//        sesCubugu.setMax(10);
//        sesCubugu.setProgress(0);
//        frameLayout.addView(sesCubugu, sesCubuguLayoutParams);
//        sesCubugu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(fromUser)
//                {
//                    resetTimeout();
//                    mediaPlayer.setVolume(progress*20);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        return sesCubugu;
//    }


    @Override
    public void onClick(View view) {
        if (m3uBilgi == null) return;
        resetTimeout();
        // Tuşlara tıklandığında gerçekleştirilecek işlemleri burada tanımlayabilirsiniz
        if (view == ekranYerler[0].button) { // Tam ekran tuşuna tıklandığında yapılacak işlemler
            TamEkranDegistir();
        } else if (view == ekranYerler[4].button) { // Oynat/Durdur tuşuna tıklandığında yapılacak işlemler
            OynatDurdurDegistir();
        } else if (view == ekranYerler[7].button) { // Konuşma butonu
            SecenekleriYap(7);
        } else if (view == ekranYerler[8].button) { // Konuşma butonu
            SecenekleriYap(8);
        }
//        else if (view == ekranYerler[9].button) { // Konuşma butonu
//            Log.d("M3UVeri", "ses:" + mediaPlayer.getVolume());
//            try {
//                int resId;
//                if (mediaPlayer.getVolume() == 0) {
//                    mediaPlayer.setVolume(100);
//                    resId = R.drawable.baseline_music_note_24;
//                } else {
//                    mediaPlayer.setVolume(0);
//                    resId = R.drawable.baseline_music_off_24;
//                }
//                Log.d("M3UVeri", "res:" + resId);
//                ekranYerler[9].button.setImageResource(resId);
//            }
//            catch (Exception ex)
//            {
//                Log.d("M3UVeri", ex.getMessage());
//            }
//        }

//        else if (view == ekranYerler[1].button) {
//            // Altyazı tuşuna tıklandığında yapılacak işlemler
//        } else if (view == ekranYerler[1].button) {
//            // Ses tuşuna tıklandığında yapılacak işlemler
//        } else if (view == ekranYerler[1].button) {
//            // Hız tuşuna tıklandığında yapılacak işlemler
//        }
    }

    private void SecenekleriYap(int sira) {
        PopupMenu popupMenu = new PopupMenu(yayinFragment.mainActivity, ekranYerler[sira].button);

        // Menü öğelerini dinamik olarak düzenleyin
        Menu menu = popupMenu.getMenu();
        menu.clear();

        // Dil seçeneklerini içeren bir liste veya dizi oluşturun
        Media.Track[] secenekler = SecenekleriBul(sira);

        // Dil seçeneklerini menüye ekleyin
        for (int i = 0; i < secenekler.length; i++) {
            Media.Track secenek = secenekler[i];
            menu.add(Menu.NONE, i, Menu.NONE, secenek.language + ":" + secenek.name)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int secId = item.getItemId();
                            mediaPlayer.selectTrack(secenekler[secId].id);
                            return true;
                        }
                    });
        }

        // PopupMenu'yu gösterin
        popupMenu.show();
    }

    private Media.Track[] SecenekleriBul(int sira) {
        if (sira == 7)
            return sesler;
        else
            return altyazilar;
    }

    public void OynatDurdurDegistir() {
        int resId;
        if (oynuyor) {
            mediaPlayer.pause();
            resId = R.drawable.baseline_play_circle_outline_24;
        } else {
            mediaPlayer.play();
            resId = R.drawable.baseline_pause_circle_outline_24;
        }
        oynuyor = !oynuyor;
        ekranYerler[4].button.setImageResource(resId);
    }

    public void TamEkrandanCik() {
        if (tamEkran) TamEkranDegistir();
    }

    public void TamEkrandanYap() {
        if (!tamEkran) TamEkranDegistir();
    }

    public void TamEkranDegistir() {
        tamEkran = !tamEkran;
        if (tamEkran) {
            ekranYerler[0].button.setImageResource(R.drawable.baseline_fullscreen_exit_24);
        } else {
            ekranYerler[0].button.setImageResource(R.drawable.baseline_fullscreen_24);
        }
        yayinFragment.TamEkranDegistir(tamEkran);
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

    public void BilgiAyarla(boolean pauseBaslat) {
        this.durdurulabilir = mediaPlayer.isSeekable();
        this.aranaBilir = mediaPlayer.isSeekable();
        this.totalMs = mediaPlayer.getLength();
        this.pauseBaslat = pauseBaslat;
        baslamaZamani = new Date();
        yeterinceSeyrettik = false;
        //Log.d("PlayerFragment", m3uBilgi.tvgName + "Playing:" + ":" + totalMs + "/" + aranaBilir + BolumSezonAd());
        oynuyor = true;
        if (totalMs > 0) {
            zamanCubugu.setVisibility(View.VISIBLE);
            zamanCubugu.setValueTo((float) (totalMs / 1000));
            zamanCubugu.setValues(0f);
            if (m3uBilgiOynayan.seyredilenSure > 0)
                mediaPlayer.setTime(m3uBilgiOynayan.seyredilenSure * 60 * 1000);
        } else
            zamanCubugu.setVisibility(View.GONE);
    }

    private String BolumSezonAd() {
        if (bolum == null || sezon == null) return "";
        else return "(" + sezon + "/" + bolum + ")";
    }

    public void ZamanAyarla(long timeChanged) {
        currentMS = timeChanged;
        int lCurrentSec = (int) (timeChanged / 1000);
        if (lCurrentSec != currentSec) {
            currentSec = lCurrentSec;
            if (totalMs > 0) zamanCubugu.setValues((float) currentSec);
            if (!yeterinceSeyrettik) {
                Date simdZaman = new Date();
                long farkZaman = (simdZaman.getTime() - baslamaZamani.getTime()) / 6000;
                if (farkZaman >= 5) {
                    yeterinceSeyrettik = true;
                    this.yayinFragment.TarihceyeEkle(sezon, bolum);
                }
            }
            int lCurrentMin = currentSec / 60;
            if (currentMin != lCurrentMin) {
                currentMin = lCurrentMin;
                this.yayinFragment.ZamaniYaz(m3uBilgiOynayan, aktifBolum, currentMin);
            }
        }
        if (this.pauseBaslat) {
            this.pauseBaslat = false;
            this.OynatDurdurDegistir();
        }
    }

    public void m3uBilgiAyarla(M3UBilgi m3uBilgi, String sezon, String bolum) {
        this.bolum = bolum;
        this.sezon = sezon;
        this.m3uBilgi = m3uBilgi;
        aktifBolum = m3uBilgi.bolumBul(sezon, bolum);
        if (aktifBolum == null) {
            this.m3uBilgiOynayan = m3uBilgi;
        } else {
            this.m3uBilgiOynayan = M3UVeri.tumM3UListesi.getOrDefault(aktifBolum.IDBul(), null);
            if (this.m3uBilgiOynayan == null) {
                this.m3uBilgiOynayan = m3uBilgi;
                aktifBolum = null;
            }
        }
    }

    public void BuyuklukAyarla() {
        if (TuslariAyarla()) {
            this.anchorView.requestLayout();
        }
    }

    public void SesleriAyarla(IMedia.Track[] tracks) {
        this.sesler = tracks;
    }

    public void AltyazilariAyarla(IMedia.Track[] tracks) {
        this.altyazilar = tracks;
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
