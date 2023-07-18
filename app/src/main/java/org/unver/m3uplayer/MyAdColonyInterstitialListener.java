package org.unver.m3uplayer;

import android.util.Log;

import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyZone;

public class MyAdColonyInterstitialListener extends AdColonyInterstitialListener {
    private final MainActivity mainActivity;

    public MyAdColonyInterstitialListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onRequestFilled(AdColonyInterstitial ad) {
        // Reklam yüklendiğinde yapılacak işlemler
        // Tam ekran reklamı göster
        Log.d("REKLAM", "onRequestFilled Interstitial: ");
        ad.show();
    }

    @Override
    public void onRequestNotFilled(AdColonyZone zone) {
        // Reklam yüklenemediğinde yapılacak işlemler
        // Ana fragmentı aç
        Log.d("REKLAM", "onRequestNotFilled Interstitial: ");
        openMainFragment();
    }

    @Override
    public void onClosed(AdColonyInterstitial ad) {
        // Reklam kapatıldığında yapılacak işlemler
        // Ana fragmentı aç
        Log.d("REKLAM", "onClosed Interstitial: ");
        openMainFragment();
    }

    private void openMainFragment() {
        //mainActivity.anaFragmentBaslat();
    }
}
