package org.unver.m3uplayer;

import java.util.ArrayList;
import java.util.Hashtable;

public class M3UGrup {
    public String grupAdi;
    public boolean gelenGrup;
    public ArrayList<String> kanallar = new ArrayList<>();
    public M3UGrup(String grupAdi, boolean gelenGrup) {
        this.grupAdi = grupAdi;
        this.gelenGrup = gelenGrup;
    }

    public boolean FiltreyeUygunMu(Hashtable<String, M3UBilgi> tumM3Ular, M3UFiltre filtre) {

        for  (String m3uId: kanallar)
        {
            M3UBilgi item = tumM3Ular.get(m3uId);
            if (item.FiltreUygunMu(filtre)) return true;
        }

        return false;
    }

    public String ProgBul(String id) {
        for (String i:kanallar)
            if(i.equals(id)) return i;
        return null;
    }
}
