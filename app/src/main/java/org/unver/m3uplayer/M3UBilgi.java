package org.unver.m3uplayer;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class M3UBilgi {
    public ArrayList<Sezon> seriSezonlari = new ArrayList<>();

    public boolean FiltreUygunMu(M3UFiltre filtre) {
        if (Tur == M3UTur.tv) {
            return AdUygunMu(filtre) && YeniUygunMu(filtre);
        } else {
            return AdUygunMu(filtre) && YilUygunMu(filtre) && PuanUygunMu(filtre) && TurUygunMu(filtre) && YeniUygunMu(filtre);
        }
    }

    private boolean AdUygunMu(M3UFiltre filtre) {
        //if (filtre == null || filtre.filtre == null) return true;
        if (M3UListeArac.IsNullOrWhiteSpace(filtre.filtre)) return true;
        if (tvgName.toLowerCase().contains(filtre.filtre.toLowerCase())) return true;
        return false;
    }

    private boolean YeniUygunMu(M3UFiltre filtre) {
        //if (filtre == null) return true;
        return !filtre.sadeceYeni || (this.eklemeTarih.compareTo(filtre.tarihStr) == 1);
    }

    private boolean TurUygunMu(M3UFiltre filtre) {
        return true;
    }

    private boolean PuanUygunMu(M3UFiltre filtre) {
        return true;
    }

    private boolean YilUygunMu(M3UFiltre filtre) {
        return true;
    }

    public Sezon SezonBul(String sezonAd) {
        for (Sezon s : seriSezonlari
        ) {
            if (s.sezonAd.equals(sezonAd))
                return s;
        }
        return null;
    }

    public enum M3UTur {
        seri, film, tv
    }

    public String ID;
    //public String m3uDosyaKod;
    public String tvgId;
    public String tvgName;
    public String tvgLogo;
    public String groupTitle;
    public String urlAdres;
    public String eklemeTarih;
    public int gizli = 0;
    public int adult = 0;
    public int tmdbId = 0;

    // Veri tabanında olmayacaklar
    public String uzanti;
    public M3UTur Tur;

    public String sezon = "";
    public String bolum = "";
    public String seriAd = "";
    public int filmYilInt = 0;
    public String filmYil = "";
    public String filmAd = "";

    public M3UBilgi(String m3uDosyaKod, String tvgId, String tvgName, String tvgLogo, String groupTitle, String urlAdres, String tar) {
        this.eklemeTarih = tar;
        this.tvgId = tvgId;
        this.tvgName = tvgName;
        this.tvgLogo = tvgLogo;
        this.groupTitle = groupTitle;
        this.urlAdres = urlAdres;
        this.tmdbId = 0;
        this.adult = 0;
        this.gizli = 0;
        DegerleriOlustur(m3uDosyaKod);
    }

    public M3UBilgi(String ID, String tvgId, String tvgName, String tvgLogo, String groupTitle, String urlAdres, String tar, int gizli, int adult, int tmdbId) {
        this.ID = ID;
        this.eklemeTarih = tar;
        this.tvgId = tvgId;
        this.tvgName = tvgName;
        this.tvgLogo = tvgLogo;
        this.groupTitle = groupTitle;
        this.urlAdres = urlAdres;
        this.tmdbId = tmdbId;
        this.adult = adult;
        this.gizli = gizli;
        DegerleriOlustur(null);
    }

    private void DegerleriOlustur(String m3uDosyaKod) {
        String[] urlParcalar = urlAdres.split("/");
        String fileName = urlParcalar[urlParcalar.length - 1];
        String[] fileNameParcalar = fileName.split("\\.");
        if (m3uDosyaKod != null)
            this.ID = m3uDosyaKod + "_" + fileNameParcalar[0];

        if (fileNameParcalar.length == 2) this.uzanti = fileNameParcalar[1];
        else this.uzanti = "";
        if (this.uzanti == "" || this.uzanti == null) {
            this.Tur = M3UTur.tv;
        } else {
            String[] isimParcalar = this.tvgName.split(" ");
            this.sezon = "";
            this.bolum = "";
            Boolean seri = false;
            if (isimParcalar.length > 2) {
                this.sezon = isimParcalar[isimParcalar.length - 2];
                this.bolum = isimParcalar[isimParcalar.length - 1];
                if (this.sezon.startsWith("S") && this.bolum.startsWith("E")) {
                    seri = true;
                }
            }
            if (seri) {
                this.Tur = M3UTur.seri;
                this.seriAd = this.tvgName.substring(
                        0,
                        this.tvgName.length() - this.sezon.length() - this.bolum.length() - 2
                );
            } else {
                this.Tur = M3UTur.film;
                if (isimParcalar.length > 1) {
                    this.filmYilInt = 0;
                    this.filmYil = isimParcalar[isimParcalar.length - 1];
                    if (this.filmYil.startsWith("(") && this.filmYil.endsWith(")")) {
                        this.filmAd = this.tvgName.substring(
                                0,
                                this.tvgName.length() - this.filmYil.length() - 1
                        );
                        this.filmYil = this.filmYil.substring(1, this.filmYil.length() - 1);
                    } else {
                        this.filmAd = this.tvgName;
                        this.filmYil = "";
                        this.filmYilInt = ProgSettings.ConvertToInt32(this.filmYil, -1);
                    }
                } else {
                    this.filmAd = this.tvgName;
                    this.filmYil = "";
                }
            }
        }
    }

    public long Yaz(SQLiteDatabase db) {
        long rowId;

        try {
            ContentValues values = new ContentValues();
            values.put("ID", ID);
            values.put("tvgId", tvgId);
            values.put("tvgName", tvgName);
            values.put("tvgLogo", tvgLogo);
            values.put("groupTitle", groupTitle);
            values.put("urlAdres", urlAdres);
            values.put("eklemeTarih", eklemeTarih);
            values.put("gizli", gizli);
            values.put("adult", adult);
            values.put("tmdbId", tmdbId);

            rowId = db.insertWithOnConflict(M3U_DB.TABLE_M3U, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception _) {
            rowId = -1;
        }
        return rowId;
    }
}