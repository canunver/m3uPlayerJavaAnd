package org.unver.m3uplayer;

import android.util.Log;

import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyZone;

public class MyAdColonyInterstitialListener extends AdColonyInterstitialListener {

    public MyAdColonyInterstitialListener() {
    }

    @Override
    public void onRequestFilled(AdColonyInterstitial ad) {
        // Reklam yüklendiğinde yapılacak işlemler
        // Tam ekran reklamı göster
        ad.show();
    }
}
