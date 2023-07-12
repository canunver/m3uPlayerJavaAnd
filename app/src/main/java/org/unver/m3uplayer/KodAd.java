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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KodAd myObject = (KodAd) o;
        return kod.equals(myObject.kod);
    }
}
