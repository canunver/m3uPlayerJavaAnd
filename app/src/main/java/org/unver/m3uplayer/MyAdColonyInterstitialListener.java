package org.unver.m3uplayer;

import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;

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
