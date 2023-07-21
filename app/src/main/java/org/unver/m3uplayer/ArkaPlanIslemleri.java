package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ArkaPlanIslemleri {

    private static Timer timer = null;
    private static final long INTERVAL = 10 * 60 * 1000; // 10 dakika
    @SuppressLint("StaticFieldLeak")
    private static MainActivity mainActivity;

    public static void baslat(MainActivity mMainActivity) {
        mainActivity = mMainActivity;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!mainActivity.VeriCekiliyorMu()) {
                    performBackgroundTask(true);
                }
            }
        }, 0, INTERVAL);
    }

    public static void performBackgroundTask(boolean inTask) {
        try {
            if (!mainActivity.TVInfoOkundu) {
                if (!OrtakAlan.StringIsNUllOrEmpty(OrtakAlan.tmdb_erisim_anahtar)) {
                    mainActivity.internettenCekiliyorYap(3, inTask);
                    M3UVeri.TMDBOku();
                }
            }
            if (OrtakAlan.sonCekilmeZamani == 0 || GecenSureSaat(Calendar.getInstance().getTimeInMillis(), OrtakAlan.sonCekilmeZamani) >= 24) {
                if (!mainActivity.M3UCekiliyorMu()) {
                    mainActivity.internettenCekiliyorYap(1, inTask);
                    M3UVeri.CekBakalim();
                }
            } else {
                if (!OrtakAlan.StringIsNUllOrEmpty(OrtakAlan.tmdb_erisim_anahtar)) {
                    mainActivity.internettenCekiliyorYap(2, inTask);
                    M3UVeri.FilmBilgiCek();
                }
            }
        } catch (Exception ex) {
            Log.e("M3UVeri", ex.getMessage());
        }
    }

    private static float GecenSureSaat(long simdi, long once) {
        return (float) (simdi - once) / 3600000;
    }

    public static void kapat() {
        if (timer != null)
            timer.cancel();
        timer = null;
    }
}
