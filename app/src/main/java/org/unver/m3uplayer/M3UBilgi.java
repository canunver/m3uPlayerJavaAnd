package org.unver.m3uplayer;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class M3UBilgi {

    public String afisBul(int size) {
        tmdbBul();
        if (tmdbBag == null)
            return tvgLogo;
        else
            return "https://image.tmdb.org/t/p/w" + size + tmdbBag.poster_path;
    }

    public String aciklamaBul() {
        tmdbBul();
        if (tmdbBag != null)
            return tmdbBag.overview;
        else
            return this.filmYil + " yılında çekilmiştir.";
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
    public String guncellemeTarih;
    public int gizli = 0;
    public int adult = 0;
    public long tmdbId = 0;
    public long seyredilenSure = 0;
    public String uzanti;
    public M3UTur Tur;
    public String sezon = "";
    public String bolum = "";
    public String seriAd = "";
    public int filmYilInt = 0;
    public String filmYil = "";
    public String filmAd = "";
    TVInfo tmdbBag = null;

    public void tmdbBul() {
        if (tmdbBag == null && tmdbId > 0) {
            tmdbBag = M3UVeri.tumTMDBler.getOrDefault(TVInfo.anahtarBul(M3UVeri.SiraBul(Tur), tmdbId), null);
        }
    }

    private int tmdbPuan() {
        tmdbBul();
        if (tmdbBag == null) return 0;
        return tmdbBag.voteAverage();
    }

    private int tmdbYil() {
        tmdbBul();
        if (tmdbBag == null) return 0;
        return tmdbBag.FilmYil();
    }

    private String tmdbName() {
        tmdbBul();
        if (tmdbBag == null) return "";
        return tmdbBag.nameTitle();
    }


    private int[] tmdbGenres() {
        tmdbBul();
        if (tmdbBag == null) return null;
        return tmdbBag.genre_ids;
    }


    public M3UBilgi(String m3uDosyaKod, String tvgId, String tvgName, String tvgLogo, String groupTitle, String urlAdres, String tar) {
        this.eklemeTarih = tar;
        this.guncellemeTarih = tar;
        this.tvgId = tvgId;
        this.tvgName = tvgName;
        this.tvgLogo = tvgLogo;
        this.groupTitle = groupTitle;
        this.urlAdres = urlAdres;
        this.tmdbId = 0;
        this.adult = 0;
        this.gizli = 0;
        this.seyredilenSure = 0;
        DegerleriOlustur(m3uDosyaKod);
    }

    public M3UBilgi(String ID, String tvgId, String tvgName, String tvgLogo, String groupTitle, String urlAdres, String tar, int gizli, int adult, int tmdbId, String guncellemeTarih, long seyredilenSure) {
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
        this.guncellemeTarih = guncellemeTarih;
        this.seyredilenSure = seyredilenSure;
        DegerleriOlustur(null);
    }

    public M3UBilgi(M3UBilgi m3u) {
        this.ID = m3u.ID + "_S";
        this.eklemeTarih = m3u.eklemeTarih;
        this.tvgId = m3u.tvgId;
        this.tvgName = m3u.tvgName;
        this.tvgLogo = m3u.tvgLogo;
        this.groupTitle = m3u.groupTitle;
        this.urlAdres = m3u.urlAdres;
        this.tmdbId = m3u.tmdbId;
        this.adult = m3u.adult;
        this.gizli = m3u.gizli;
        this.guncellemeTarih = m3u.guncellemeTarih;
        this.seyredilenSure = m3u.seyredilenSure;
        DegerleriOlustur(null);
    }

    public ArrayList<Sezon> seriSezonlari = new ArrayList<>();

    public boolean FiltreUygunMu(M3UFiltre filtre) {
        if (Tur == M3UTur.tv) {
            return AdUygunMu(filtre) && YeniUygunMu(filtre);
        } else {
            return AdUygunMu(filtre) && YilUygunMu(filtre) && PuanUygunMu(filtre) && TurUygunMu(filtre) && YeniUygunMu(filtre);
        }
    }

    private boolean AdUygunMu(M3UFiltre filtre) {
        if (M3UListeArac.IsNullOrWhiteSpace(filtre.isimFiltreStr)) return true;
        if (tvgName.toLowerCase().contains(filtre.isimFiltreStr.toLowerCase())) return true;
        if (tmdbName().toLowerCase().contains(filtre.isimFiltreStr.toLowerCase())) return true;
        return false;
    }

    private boolean YeniUygunMu(M3UFiltre filtre) {
        return !filtre.sadeceYeni || (this.eklemeTarih.compareTo(filtre.yeniTarihBaslaStr) == 1);
    }

    private boolean TurUygunMu(M3UFiltre filtre) {
        if (filtre.filmTurler == null || filtre.filmTurler.length == 0) return true;

        int[] tmdbTurler = tmdbGenres();
        if (tmdbTurler == null || tmdbTurler.length == 0) return false;
        for (int item : tmdbTurler) {
            for (int ic : filtre.filmTurler) {
                if (ic == item) return true;
            }
        }
        return false;
    }

    private boolean PuanUygunMu(M3UFiltre filtre) {
        if (filtre.maxPuan <= 0 || filtre.maxPuan < filtre.minPuan) return true;
        if (Math.round(tmdbPuan()) <= filtre.maxPuan && Math.round(tmdbPuan()) >= filtre.minPuan)
            return true;
        return false;
    }

    private boolean YilUygunMu(M3UFiltre filtre) {

        if (filtre.maxYil <= 0 || filtre.maxYil < filtre.minYil) return true;
        int yil = tmdbYil();
        if (yil < 0) yil = this.filmYilInt;
        if (yil <= filtre.maxYil && yil >= filtre.minYil) return true;
        return false;
    }

    public Sezon SezonBul(String sezonAd) {
        for (Sezon s : seriSezonlari
        ) {
            if (s.sezonAd.equals(sezonAd))
                return s;
        }
        return null;
    }

    public Sezon sezonBul(String sezon) {
        if (seriSezonlari == null) return null;
        if (sezon == null) return null;
        for (Sezon s : seriSezonlari)
            if (s.sezonAd.equals(sezon)) return s;
        return null;
    }

    public Bolum bolumBul(String sezon, String bolum) {
        if (!ProgSettings.StringIsNUllOrEmpty(sezon) && !ProgSettings.StringIsNUllOrEmpty(bolum)) {
            Sezon aktifSezon = sezonBul(sezon);
            if (aktifSezon != null) {
                String[] bolumParca = bolum.split("\\s+");
                if (bolumParca.length > 0) {
                    Bolum aktifBolum = aktifSezon.BolumBul(bolumParca[0]);
                    if (aktifBolum != null) {
                        if (bolumParca.length == 2) {
                            aktifBolum.seciliIdIndex = ProgSettings.ConvertToInt32(bolumParca[1].substring(1, bolumParca[1].length() - 1), 0);
                        } else
                            aktifBolum.seciliIdIndex = 0;
                        return aktifBolum;
                    }
                }
            }
        }
        return null;
    }

    public String TMDBTur() {
        if (Tur == M3UTur.film)
            return "movie";
        return "tv";
    }

    public String SorguYap() {
        if (Tur == M3UTur.film)
            return filmAd;
        else
            return tvgName.replace(" ", "+");
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
                    }
                } else {
                    this.filmAd = this.tvgName;
                    this.filmYil = "";
                }
                this.filmYilInt = ProgSettings.ConvertToInt32(this.filmYil, -1);
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
            values.put("guncellemeTarih", guncellemeTarih);
            values.put("seyredilenSure", seyredilenSure);

            rowId = db.insertWithOnConflict(M3U_DB.TABLE_M3U, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception ex) {
            rowId = -1;
        }
        return rowId;
    }
}