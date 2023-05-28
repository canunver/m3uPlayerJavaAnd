package org.unver.m3uplayer;

import java.util.ArrayList;

public class M3UGrup {
    public String grupAdi;
    public boolean gelenGrup;
    public ArrayList<String> kanallar = new ArrayList<>();
    public M3UGrup(String grupAdi, boolean gelenGrup) {
        this.grupAdi = grupAdi;
        this.gelenGrup = gelenGrup;
    }
}
