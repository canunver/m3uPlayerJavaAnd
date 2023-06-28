package org.unver.m3uplayer;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class ArkaPlanIslemleri {

    private static Timer timer = null;
    private static boolean isPaused;
    private static final long INTERVAL = 10 * 60 * 1000; // 10 dakika
    private static boolean calisiyor = false;
    private int state = 0;
    public static void baslat() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused && !calisiyor) {
                    performBackgroundTask();
                }
            }
        }, 0, INTERVAL);
    }

    private static void performBackgroundTask() {
        calisiyor = true;
        try {
            throw new Exception("Hazır değil");
        } catch (Exception ex) {
            Log.d("ArkaPlanIslemleri", ex.getMessage());
        }
        calisiyor = false;
    }

    public static void kapat() {
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
