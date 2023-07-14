package org.unver.m3uplayer;

import java.util.ArrayList;

public class Bolum {
    public ArrayList<String> idler = new ArrayList<>();
    public String bolum;
    public int seciliIdIndex = 0;

    public Bolum(String id, String bolum) {
        this.idler.add(id);
        this.bolum = bolum;
    }

    public void AddId(String id) {
        if (!idler.contains(id))
            this.idler.add(id);
    }

    public String IDBul() {
        return this.idler.get(seciliIdIndex);
    }

    public String tmdbAciklamaBul() {
        String aciklama = null;
        String m3uId = this.idler.get(0);
        M3UBilgi bolumM3U = M3UVeri.tumM3Ular.getOrDefault(m3uId, null);
        if (bolumM3U != null) {
            if (bolumM3U.tmdbId > 0) {
                TVInfo ti = M3UVeri.TVInfoOku(9, bolumM3U.tmdbId);
                if (ti != null)
                    aciklama = ti.nameTitle() + "(" + ti.yayinTarihiBul() + "," + ti.voteAverage() + "): " + ti.overview;
            }
            if (aciklama == null)
                aciklama = m3uId + "_" + bolumM3U.tmdbId;
        }
        if (aciklama == null)
            aciklama = m3uId;
        return aciklama;
    }

    public long seyredilenSureBul() {
        String m3uId = this.idler.get(0);
        M3UBilgi bolumM3U = M3UVeri.tumM3Ular.getOrDefault(m3uId, null);
        if (bolumM3U != null)
            return bolumM3U.seyredilenSure;
        else
            return 0;
    }
}
