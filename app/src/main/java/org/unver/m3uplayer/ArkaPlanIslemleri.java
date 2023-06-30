package org.unver.m3uplayer;

import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ArkaPlanIslemleri {

    private static Timer timer = null;
    private static boolean isPaused;
    private static final long INTERVAL = 10 * 60 * 1000; // 10 dakika
    private static MainActivity mainActivity;
    private int state = 0;
    public static void baslat(MainActivity mMainActivity) {
        mainActivity =  mMainActivity;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused && !mainActivity.VeriCekiliyorMu()) {
                    performBackgroundTask();
                }
            }
        }, 0, INTERVAL);
    }

    private static void performBackgroundTask() {
        Log.i("M3UVeri", "performBackgroundTask, sonCekilmeZamani: " + ProgSettings.sonCekilmeZamani);

        try {
            if(ProgSettings.sonCekilmeZamani == 0 || GecenSureSaat(Calendar.getInstance().getTimeInMillis(), ProgSettings.sonCekilmeZamani)>=24)
            {
                mainActivity.internettenCekiliyorYap(1);
                M3UVeri.CekBakalim();
            }
            else
            {
                mainActivity.internettenCekiliyorYap(2);
                M3UVeri.FilmBilgiCek();
            }
        } catch (Exception ex) {
            Log.d("M3UVeri", ex.getMessage());
        }
    }

    private static float GecenSureSaat(long simdi, long once) {
        return (float)(simdi-once)/3600000;
    }

    public static void kapat() {
        Log.i("M3UVeri", "performBackgroundTask bitiyor");
        if (timer != null)
            timer.cancel();
        timer = null;
    }

    // Servis durumu değiştiğinde
    public void setPaused(boolean paused) {
        isPaused = paused;
        //showGif();
    }

    // Veri türü değiştiğinde
    public void setState(int state) {
        this.state = state;
        //showGif();
    }
}
