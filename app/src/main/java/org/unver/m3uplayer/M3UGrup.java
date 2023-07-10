package org.unver.m3uplayer;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Hashtable;

public class M3UGrup {
    public final int turSira;
    public String grupAdi;
    public boolean gelenGrup;
    public boolean gizli;
    public boolean yetiskin;
    public ArrayList<String> kanallar = new ArrayList<>();

    public M3UGrup(int turSira, String grupAdi, boolean gelenGrup) {
        this.turSira = turSira;
        this.grupAdi = grupAdi;
        this.gelenGrup = gelenGrup;
        this.gizli = false;
        this.yetiskin = false;
    }

    public M3UGrup(String type_name, boolean gelenGrup, boolean gizli, boolean yetiskin) {
        if (ProgSettings.StringIsNUllOrEmpty(type_name))
            this.turSira = -1;
        else {
            String[] parcalar = type_name.split("_");
            this.turSira = ProgSettings.ConvertToInt32(parcalar[0], -1);
            this.grupAdi = Birlestir(parcalar, 1, '_');
            this.gelenGrup = gelenGrup;
            this.gizli = gizli;
            this.yetiskin = yetiskin;
        }
    }

    private String Birlestir(String[] parcalar, int basla, char ulamaKar) {
        String donDeger = "";
        for (int i = basla; i < parcalar.length; i++) {
            if (i > basla)
                donDeger += ulamaKar;
            donDeger += parcalar[i];
        }
        return donDeger;
    }

    public boolean FiltreyeUygunMu(Hashtable<String, M3UBilgi> tumM3Ular, M3UFiltre filtre) {
        if (filtre == null) return true;
        for (String m3uId : kanallar) {
            M3UBilgi item = tumM3Ular.get(m3uId);
            if (item.FiltreUygunMu(filtre)) return true;
        }
        return false;
    }

    public String ProgBul(String id) {
        for (String i : kanallar)
            if (i.equals(id)) return i;
        return null;
    }

    public boolean GrupTurUygunMu(int hepsi0Kul1Inen2) {
        if (hepsi0Kul1Inen2 == 0)
            return true;
        if (hepsi0Kul1Inen2 == 1 && !this.gelenGrup)
            return true;
        if (hepsi0Kul1Inen2 == 2 && this.gelenGrup)
            return true;
        return false;
    }

    public boolean GizlilikKontrol() {
        return true;
    }

    public int AdDegistir(SQLiteDatabase db, String yeniAd) {
        int rowsAffected;

        try {
            ContentValues values = new ContentValues();

            values.put("type_name", anahtarBul(turSira, yeniAd));

            String whereClause = "type_name = ?";
            String[] whereArgs = {anahtarBul()};
            rowsAffected = db.update(M3U_DB.TABLE_GRUP, values, whereClause, whereArgs);
        } catch (Exception ex) {
            rowsAffected = -1;
        }
        return rowsAffected;
    }

    public long Kaydet(SQLiteDatabase db) {
        long rowId;

        try {
            ContentValues values = new ContentValues();

            values.put("type_name", anahtarBul());
            values.put("gelenGrup", gelenGrup ? 1 : 0);
            values.put("gizli", gizli ? 1 : 0);
            values.put("yetiskin", yetiskin ? 1 : 0);
            rowId = db.insertWithOnConflict(M3U_DB.TABLE_GRUP, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception ex) {
            rowId = -1;
        }
        return rowId;
    }

    public String anahtarBul() {
        return anahtarBul(turSira, grupAdi);
    }

    public static String anahtarBul(int turSira, String grupAdi) {
        return turSira + "_" + grupAdi;
    }

}
