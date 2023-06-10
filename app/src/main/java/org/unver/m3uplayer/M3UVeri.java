package org.unver.m3uplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;

public class M3UVeri {


    public static Hashtable<String, M3UBilgi> tumM3Ular = new Hashtable<>();
    public static ArrayList<M3UGrup> tvGruplari = new ArrayList<>();
    public static ArrayList<M3UGrup> filmGruplari = new ArrayList<>();
    public static ArrayList<M3UGrup> seriGruplari = new ArrayList<>();
    public static Hashtable<String, String> tumSerilerAd = new Hashtable<>();
    private static MainActivity context;

    public static ArrayList<M3UGrup> GrupKodBul(int position) {
        if (position == 0) return tvGruplari;
        else if (position == 1) return filmGruplari;
        else return seriGruplari;
    }

    public static void OkuBakayim(MainActivity mcontext) {
        context = mcontext;
        M3U_DB dbHelper = new M3U_DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(M3U_DB.TABLE_M3U, null, null, null, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int IDIndex = cursor.getColumnIndex("ID");
                    int tvgIdIndex = cursor.getColumnIndex("tvgId");
                    int tvgNameIndex = cursor.getColumnIndex("tvgName");
                    int tvgLogoIndex = cursor.getColumnIndex("tvgLogo");
                    int groupTitleIndex = cursor.getColumnIndex("groupTitle");
                    int urlAdresIndex = cursor.getColumnIndex("urlAdres");
                    int eklemeTarihIndex = cursor.getColumnIndex("eklemeTarih");
                    int gizliIndex = cursor.getColumnIndex("gizli");
                    int adultIndex = cursor.getColumnIndex("adult");
                    int tmdbIdIndex = cursor.getColumnIndex("tmdbId");

                    do {
                        String ID = cursor.getString(IDIndex);
                        String tvgId = cursor.getString(tvgIdIndex);
                        String tvgName = cursor.getString(tvgNameIndex);
                        String tvgLogo = cursor.getString(tvgLogoIndex);
                        String groupTitle = cursor.getString(groupTitleIndex);
                        String urlAdres = cursor.getString(urlAdresIndex);
                        String eklemeTarih = cursor.getString(eklemeTarihIndex);
                        int gizli = cursor.getInt(gizliIndex);
                        int adult = cursor.getInt(adultIndex);
                        int tmdbId = cursor.getInt(tmdbIdIndex);

                        M3UBilgi m3u = new M3UBilgi(ID, tvgId, tvgName,
                                tvgLogo, groupTitle, urlAdres,
                                eklemeTarih, gizli, adult, tmdbId);
                        GruplaraIsle(m3u, true);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        if (tumM3Ular.size() == 0 || filmGruplari.size() == 0 || seriGruplari.size() == 0)
            CekBakalim();
        //else
        //TurSecildi(0);
    }

    private static M3UGrup GrupBulYoksaEkle(ArrayList<M3UGrup> anaGrup, String groupTitle, boolean gelenGrup, boolean yoksaEkle) {
        for (M3UGrup item : anaGrup
        ) {
            if (item.grupAdi.equalsIgnoreCase(groupTitle))
                return item;
        }
        M3UGrup yeni;
        if (yoksaEkle) {
            yeni = new M3UGrup(groupTitle, gelenGrup);
            anaGrup.add(yeni);
        } else
            yeni = null;
        return yeni;
    }


    private static void GrupaEkle(ArrayList<M3UGrup> anaGrup, M3UBilgi m3u, boolean gelenGrup) {
        M3UGrup grp = GrupBulYoksaEkle(anaGrup, m3u.groupTitle, gelenGrup, true);
        grp.kanallar.add(m3u.ID);
    }

    public static void GruplaraIsle(M3UBilgi m3u, boolean gelenGrup) {
        tumM3Ular.put(m3u.ID, m3u);
        if (m3u.Tur == M3UBilgi.M3UTur.tv) {
            GrupaEkle(tvGruplari, m3u, gelenGrup);
        } else if (m3u.Tur == M3UBilgi.M3UTur.film) {
            GrupaEkle(filmGruplari, m3u, gelenGrup);
        } else if (m3u.Tur == M3UBilgi.M3UTur.seri) {
            String m3uID;
            M3UBilgi seri;
            if (!tumSerilerAd.containsKey(m3u.seriAd)) {
                GrupaEkle(seriGruplari, m3u, gelenGrup);
                m3uID = m3u.ID;
                tumSerilerAd.put(m3u.seriAd, m3u.ID);
                seri = m3u;
            } else {
                m3uID = tumSerilerAd.get(m3u.seriAd);
                seri = tumM3Ular.get(m3uID);
            }
            Sezon sezon = SezonBulYoksaEkle(seri, m3u.sezon);
            sezon.bolumler.add(new Bolum(m3u.ID, m3u.bolum));
        }
    }
    public static Sezon SezonBulYoksaEkle(M3UBilgi seri, String sezonAd) {
        for (Sezon item : seri.seriSezonlari) {
            if (item.sezonAd.equalsIgnoreCase(sezonAd))
                return item;
        }
        Sezon s = new Sezon(sezonAd);
        seri.seriSezonlari.add(s);
        return s;
    }

    public static void CekBakalim() {
        try {
            M3U_DB dbHelper = new M3U_DB(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            new InternettenOku().performNetworkOperation((MainActivity) context, db, "A");
        } catch (Exception e) {
            Log.d("Hata", e.getMessage());
        }
    }

    public static int SiraBul(M3UBilgi.M3UTur aktifTur) {
        if (aktifTur == M3UBilgi.M3UTur.film) return 1;
        if (aktifTur == M3UBilgi.M3UTur.seri) return 2;
        return 0;
    }

    public static ArrayList<M3UGrup> GrupDegiskenBul(M3UBilgi.M3UTur aktifTur) {
        if (aktifTur == M3UBilgi.M3UTur.film)
            return filmGruplari;
        else if (aktifTur == M3UBilgi.M3UTur.seri)
            return seriGruplari;
        else
            return tvGruplari;
    }

    public static M3UGrup GrupBul(ArrayList<M3UGrup> grupListesi, String aktifGrupAd) {
        for (M3UGrup g : grupListesi) {
            if (g.grupAdi == aktifGrupAd)
                return g;
        }
        return null;
    }
}
