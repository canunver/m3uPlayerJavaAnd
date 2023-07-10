package org.unver.m3uplayer;

import androidx.annotation.NonNull;

public class KodAd {
    public String kod;
    public String ad;
    public Object o;

    public boolean secili;

    public KodAd(String kod, String ad, Object o) {
        this.kod = kod;
        this.ad = ad;
        this.o = o;
        this.secili = false;
    }

    @NonNull
    @Override
    public String toString() {
        return ad;
    }
}
