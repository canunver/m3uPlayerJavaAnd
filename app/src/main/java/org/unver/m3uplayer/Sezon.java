package org.unver.m3uplayer;

import java.util.ArrayList;
import java.util.List;

public class Sezon {
    public String sezonAd;
    public List<Bolum> bolumler= new ArrayList<>();

    public Sezon(String sezonAd) {
        this.sezonAd = sezonAd;
    }
}
