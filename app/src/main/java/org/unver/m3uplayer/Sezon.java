package org.unver.m3uplayer;

import java.util.ArrayList;
import java.util.List;

public class Sezon {
    public String sezonAd;
    public List<Bolum> bolumler = new ArrayList<>();

    public Sezon(String sezonAd) {
        this.sezonAd = sezonAd;
    }

    public Bolum BolumBul(String bolum) {
        for (Bolum b : bolumler) {
            if (b.bolum.equals(bolum))
                return b;
        }
        return null;
    }

    public int seyredilenSonBul() {
        for (int bi = bolumler.size() - 1; bi >= 0; bi--)
            if (bolumler.get(bi).seyredilenSureBul() > 0) return bi;
        return 0;
    }
}
