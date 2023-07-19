package org.unver.m3uplayer;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class M3UGrup {
    public final int turSira;
    public String grupAdi;
    public boolean gelenGrup;
    public boolean gizli;
    public boolean yetiskin;
    public boolean veriTabanindan;
    public ArrayList<String> kanallar = new ArrayList<>();

    public M3UGrup(int turSira, String grupAdi, boolean gelenGrup) {
        this.turSira = turSira;
        this.grupAdi = grupAdi;
        this.gelenGrup = gelenGrup;
        this.gizli = false;
        this.yetiskin = false;
        this.veriTabanindan = false;
    }

    public M3UGrup(String type_name, boolean gelenGrup, boolean gizli, boolean yetiskin) {
        if (OrtakAlan.StringIsNUllOrEmpty(type_name))
            this.turSira = -1;
        else {
            String[] parcalar = type_name.split("_");
            this.turSira = OrtakAlan.ConvertToInt32(parcalar[0], -1);
            this.grupAdi = birlestir(parcalar, 1, '_');
            this.gelenGrup = gelenGrup;
            this.gizli = gizli;
            this.yetiskin = yetiskin;
            this.veriTabanindan = true;
        }
    }

    private String birlestir(String[] parcalar, int basla, char ulamaKar) {
        String donDeger = "";
        for (int i = basla; i < parcalar.length; i++) {
            if (i > basla)
                donDeger += ulamaKar;
            donDeger += parcalar[i];
        }
        return donDeger;
    }

    public boolean filtreyeUygunMu(M3UFiltre filtre) {
        if (filtre == null) return true;
        for (String m3uId : kanallar) {
            M3UBilgi item = M3UVeri.tumM3UListesi.get(m3uId);
            if (item.FiltreUygunMu(filtre, false)) return true;
        }
        return false;
    }

    public String progBul(String id) {
        for (String i : kanallar)
            if (i.equals(id)) return i;
        return null;
    }

    public boolean grupTurUygunMu(int hepsi0Kul1Inen2) {
        if (hepsi0Kul1Inen2 == 0)
            return true;
        if (hepsi0Kul1Inen2 == 1 && !this.gelenGrup)
            return true;
        if (hepsi0Kul1Inen2 == 2 && this.gelenGrup)
            return true;
        return false;
    }

    public boolean gizliYetiskinDegilse(boolean gizlilerOlsun) {
        if (!gizlilerOlsun && !OrtakAlan.gizlilerVar) {
            if (gizli) return false;
        }
        if (!OrtakAlan.yetiskinlerVar) {
            if (yetiskin) return false;
        }
        return true;
    }

    public int adDegistir(SQLiteDatabase db, String yeniAd) {
        int rowsAffected;

        try {
            this.grupAdi = yeniAd;
            ContentValues values = new ContentValues();

            values.put("type_name", anahtarBul(turSira, grupAdi));

            String whereClause = "type_name = ?";
            String[] whereArgs = {anahtarBul()};
            rowsAffected = db.update(M3U_DB.TABLE_GRUP, values, whereClause, whereArgs);
        } catch (Exception ex) {
            rowsAffected = -1;
        }
        return rowsAffected;
    }

    public long kaydet(SQLiteDatabase db) {
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

    public String grupAdiBul(boolean ozelliklerOlsun, String gizliKod, String yetiskinKod) {
        if (ozelliklerOlsun)
            return OrtakAlan.DogruysaDondur(gizli, gizliKod) + OrtakAlan.DogruysaDondur(yetiskin, yetiskinKod) + " " + grupAdi;
        else
            return grupAdi;
    }

    public long kanalEkle(String kod) {
        this.kanallar.add(kod);
        ContentValues values = new ContentValues();
        ContentValueDoldur(values, kod, this.kanallar.size());
        long rowId = M3UVeri.db.insertWithOnConflict(M3U_DB.TABLE_GRUPDETAY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return rowId;
    }

    private void ContentValueDoldur(ContentValues values, String kod, int siraNo) {
        values.put("type_name_ID", anahtarBul() + "_" + kod);
        values.put("type_name", anahtarBul());
        values.put("ID", kod);
        values.put("sira_No", siraNo);
    }

    public void listeYenile(List<KodAd> grupKanalListesi) {
        M3UVeri.db.beginTransaction();
        try {
            // Kayıtları silme işlemi
            M3UVeri.db.delete(M3U_DB.TABLE_GRUPDETAY, "type_name=?", new String[]{String.valueOf(anahtarBul())});

            // Yeni kayıtları ekleme işlemi
            ContentValues values = new ContentValues();
            int sira = 1;
            for (KodAd data : grupKanalListesi) {
                ContentValueDoldur(values, data.kod, sira++);
                M3UVeri.db.insert(M3U_DB.TABLE_GRUPDETAY, null, values);
                values.clear();
            }

            M3UVeri.db.setTransactionSuccessful();
        } finally {
            M3UVeri.db.endTransaction();
        }
    }

    public void sil() {
        M3UVeri.db.delete(M3U_DB.TABLE_GRUP, "type_name=?", new String[]{String.valueOf(anahtarBul())});
    }

    public boolean ozellikDegistir(boolean gizliDegistir, boolean yetiskinDegistir) {
        if (gizliDegistir || yetiskinDegistir) {
            if (gizliDegistir)
                gizli = !gizli;
            if (yetiskinDegistir)
                yetiskin = !yetiskin;
            if (!veriTabanindan) {
                kaydet(M3UVeri.db);
                veriTabanindan = true;
            }
            ContentValues values = new ContentValues();
            values.put("gizli", gizli ? 1 : 0);
            values.put("yetiskin", yetiskin ? 1 : 0);

            M3UVeri.db.update(M3U_DB.TABLE_GRUP, values, "type_name=?", new String[]{String.valueOf(anahtarBul())});

            return true;
        } else
            return false;
    }

    public boolean detaydaYetiskinVarMi() {
        if (kanallar == null) return false;
        for (String m3uId : kanallar) {
            M3UBilgi item = M3UVeri.tumM3UListesi.get(m3uId);
            if (item.yetiskin) return true;
        }
        return false;
    }
}
