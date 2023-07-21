package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

public class M3UVeri {
    public static Hashtable<String, TVInfo> tumTMDBListesi = new Hashtable<>();
    public static Hashtable<String, M3UBilgi> tumM3UListesi = new Hashtable<>();
    public static ArrayList<M3UGrup> tvGruplari = new ArrayList<>();
    public static ArrayList<M3UGrup> filmGruplari = new ArrayList<>();
    public static ArrayList<M3UGrup> seriGruplari = new ArrayList<>();
    public static Hashtable<String, String> tumSerilerAd = new Hashtable<>();
    @SuppressLint("StaticFieldLeak")
    public static MainActivity mainActivity;
    public static int minYil = 10000;
    private static M3U_DB dbHelper = null;
    public static SQLiteDatabase db = null;

    public static ArrayList<M3UGrup> GrupKodBul(int position) {
        if (position == 0) return tvGruplari;
        else if (position == 1) return filmGruplari;
        else return seriGruplari;
    }

    public static void OkuBakayim(MainActivity mainActivity) {

        Log.i("M3UVeri", "Oku bakayım");
        M3UVeri.mainActivity = mainActivity;
        tvGruplari.clear();
        seriGruplari.clear();
        filmGruplari.clear();
        tumM3UListesi.clear();
        tumSerilerAd.clear();
        if (dbHelper == null)
            dbHelper = new M3U_DB(M3UVeri.mainActivity);
        if (db == null)
            db = dbHelper.getWritableDatabase();

        String query = "SELECT GRUP.type_name, GRUP.gelenGrup, GRUP.gizli, GRUP.yetiskin, GRUPDETAY.ID  FROM GRUP LEFT OUTER JOIN GRUPDETAY ON GRUP.type_name = GRUPDETAY.type_name order by GRUP.gelenGrup, GRUP.type_name, GRUPDETAY.sira_No";
        Cursor cursorGrup = db.rawQuery(query, null);
        Log.i("M3UVeri", "Raw query bitti");

        if (cursorGrup != null) {
            try {
                if (cursorGrup.moveToFirst()) {
                    int type_nameIndex = cursorGrup.getColumnIndex("type_name");
                    int gelenGrupIndex = cursorGrup.getColumnIndex("gelenGrup");
                    int gizliIndex = cursorGrup.getColumnIndex("gizli");
                    int yetiskinIndex = cursorGrup.getColumnIndex("yetiskin");
                    int M3UIDIndex = cursorGrup.getColumnIndex("ID");
                    M3UGrup m3uGrp = null;
                    do {
                        String type_name = cursorGrup.getString(type_nameIndex);
                        boolean gelenGrup = cursorGrup.getInt(gelenGrupIndex) == 1;
                        boolean gizli = cursorGrup.getInt(gizliIndex) == 1;
                        boolean yetiskin = cursorGrup.getInt(yetiskinIndex) == 1;
                        String M3UID = cursorGrup.getString(M3UIDIndex);
                        if (m3uGrp == null || !type_name.equals(m3uGrp.anahtarBul())) {
                            m3uGrp = new M3UGrup(type_name, gelenGrup, gizli, yetiskin);
                            if (m3uGrp.turSira >= 0 && !OrtakAlan.StringIsNUllOrEmpty(m3uGrp.grupAdi)) {
                                M3UVeri.GrupKodBul(m3uGrp.turSira).add(m3uGrp);
                            } else
                                m3uGrp = null;
                        }
                        if (m3uGrp != null && !OrtakAlan.StringIsNUllOrEmpty(M3UID))
                            m3uGrp.kanallar.add(M3UID);

                    } while (cursorGrup.moveToNext());
                }
            } catch (Exception ex) {
                Log.e("M3UVeri", "Grup okunurken hata oldu:" + ex.getMessage());
            } finally {
                cursorGrup.close();
            }
        } else
            Log.e("M3UVeri", "Cursor null oldu...");
        Log.i("M3UVeri", "Kullanıcı grupları bitti");

        Cursor cursor = db.query(M3U_DB.TABLE_M3U, null, null, null, null, null, null);
        Log.i("M3UVeri", "M3U Table query bitti");
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
                    int guncellemeTarihIndex = cursor.getColumnIndex("guncellemeTarih");
                    int seyredilenSureIndex = cursor.getColumnIndex("seyredilenSure");

                    do {
                        String ID = cursor.getString(IDIndex);
                        String tvgId = cursor.getString(tvgIdIndex);
                        String tvgName = cursor.getString(tvgNameIndex);
                        String tvgLogo = cursor.getString(tvgLogoIndex);
                        String groupTitle = cursor.getString(groupTitleIndex);
                        String urlAdres = cursor.getString(urlAdresIndex);
                        String eklemeTarih = cursor.getString(eklemeTarihIndex);
                        boolean gizli = cursor.getInt(gizliIndex) == 1;
                        boolean adult = cursor.getInt(adultIndex) == 1;
                        int tmdbId = cursor.getInt(tmdbIdIndex);

                        String guncellemeTarih = cursor.getString(guncellemeTarihIndex);
                        long seyredilenSure = cursor.getLong(seyredilenSureIndex);

                        M3UBilgi m3u = new M3UBilgi(ID, tvgId, tvgName,
                                tvgLogo, groupTitle, urlAdres,
                                eklemeTarih, gizli, adult, tmdbId, guncellemeTarih, seyredilenSure);
                        if (m3u.filmYilInt > 0 && m3u.filmYilInt < minYil)
                            minYil = m3u.filmYilInt;
                        GruplaraIsle(m3u, true);
                    } while (cursor.moveToNext());
                }
            } catch (Exception ex) {
                Log.e("M3UVeriG", "M3U okunurken hata oldu:" + ex.getMessage());
            } finally {
                cursor.close();
            }
        }
        Log.i("M3UVeri", "Gruplar sıralanacak");
        if (minYil > 3000) minYil = 0;

        Comparator<? super M3UGrup> grupKiyasla = (Comparator<M3UGrup>) (o1, o2) -> {
            int result = (o1.gelenGrup == o2.gelenGrup) ? 0 : (o1.gelenGrup ? 1 : -1);
            if (result == 0) {
                result = o1.grupAdi.compareTo(o2.grupAdi);
            }
            return result;
        };
        tvGruplari.sort(grupKiyasla);
        filmGruplari.sort(grupKiyasla);
        seriGruplari.sort(grupKiyasla);
        Log.i("M3UVeri", "Okuma bitti");
    }

    public static void TMDByeIsle(TVInfo tvInfo) {
        M3UVeri.tumTMDBListesi.put(tvInfo.anahtarBul(), tvInfo);
    }

    @SuppressWarnings("all")
    private static M3UGrup GrupBulYoksaEkle(int aktifTurSira, ArrayList<M3UGrup> anaGrup, String groupTitle, boolean gelenGrup, boolean yoksaEkle) {
        for (M3UGrup item : anaGrup
        ) {
            if (item.grupAdi.equalsIgnoreCase(groupTitle))
                return item;
        }
        M3UGrup yeni;
        if (yoksaEkle) {
            yeni = new M3UGrup(aktifTurSira, groupTitle, gelenGrup);
            //Log.d("M3UVeriG", yeni.anahtarBul() + "::" + yeni.grupAdiBul(true, "G", "Y") + " Eklendi M3U");
            anaGrup.add(yeni);
        } else
            yeni = null;
        return yeni;
    }

    private static void GrubaEkle(int aktifTurSira, ArrayList<M3UGrup> anaGrup, M3UBilgi m3u, boolean gelenGrup) {
        M3UGrup grp = GrupBulYoksaEkle(aktifTurSira, anaGrup, m3u.groupTitle, gelenGrup, true);
        if (grp.progBul(m3u.ID) == null)
            grp.kanallar.add(m3u.ID);
    }

    public static void GruplaraIsle(M3UBilgi m3u, boolean gelenGrup) {
        if (m3u.Tur == M3UBilgi.M3UTur.tv) {
            tumM3UListesi.put(m3u.ID, m3u);
            GrubaEkle(0, tvGruplari, m3u, gelenGrup);
        } else if (m3u.Tur == M3UBilgi.M3UTur.film) {
            tumM3UListesi.put(m3u.ID, m3u);
            GrubaEkle(1, filmGruplari, m3u, gelenGrup);
        } else if (m3u.Tur == M3UBilgi.M3UTur.seri) {
            M3UBilgi seri;
            String mevcutId = tumSerilerAd.getOrDefault(m3u.seriAd, null);
            if (mevcutId == null) {
                if (m3u.ID.equals(m3u.seriAd)) {
                    seri = m3u;
                } else {
                    seri = new M3UBilgi(m3u);
                    seri.tmdbId = 0;
                }
                tumM3UListesi.put(seri.ID, seri);
                GrubaEkle(2, seriGruplari, seri, gelenGrup);
                tumSerilerAd.put(seri.seriAd, seri.ID);
            } else {
                seri = tumM3UListesi.getOrDefault(mevcutId, null);
                if (seri != null) {
                    if (!seri.groupTitle.equals(m3u.groupTitle)) {
                        GrubaEkle(2, seriGruplari, seri, gelenGrup);
                    }
                }
            }
            if (!m3u.ID.equals(m3u.seriAd)) {
                if (seri != null) {
                    Sezon sezon = SezonBulYoksaEkle(seri, m3u.sezon);
                    Bolum blm = sezon.BolumBul(m3u.bolum);
                    if (blm == null)
                        sezon.bolumler.add(new Bolum(m3u.ID, m3u.bolum));
                    else
                        blm.AddId(m3u.ID);
                    seri.eklemeTarih = BuyukBul(seri.eklemeTarih, m3u.eklemeTarih);
                    seri.guncellemeTarih = BuyukBul(seri.guncellemeTarih, m3u.guncellemeTarih);
                }
                tumM3UListesi.put(m3u.ID, m3u);
            } else {
                if(seri != null) {
                    seri.gizli = m3u.gizli;
                    seri.yetiskin = m3u.yetiskin;
                    seri.tmdbId = m3u.tmdbId;
                }
            }
        }
    }

    private static String BuyukBul(String tarih1, String tarih2) {
        if (OrtakAlan.StringIsNUllOrEmpty(tarih1)) return tarih2;
        if (OrtakAlan.StringIsNUllOrEmpty(tarih2)) return tarih1;
        if (tarih1.compareTo(tarih2) > 0) return tarih1;
        else return tarih2;
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
            new InternettenOku().performNetworkOperation(mainActivity, db);
        } catch (Exception e) {
            Log.e("Hata", e.getMessage());
        }
    }

    public static int SiraBul(M3UBilgi.M3UTur aktifTur) {
        //mainActivity.aktifTur = position == 2 ? M3UBilgi.M3UTur.seri : (position == 1 ? M3UBilgi.M3UTur.film : M3UBilgi.M3UTur.tv);
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

    public static M3UGrup GrupBul(ArrayList<M3UGrup> grupListesi, String aktifGrupAd, boolean ozelliklerle) {
        for (M3UGrup g : grupListesi) {
            if (g.grupAdiBul(ozelliklerle, OrtakAlan.GizliBul(mainActivity), OrtakAlan.YetiskinBul(mainActivity)).equals(aktifGrupAd))
                return g;
        }
        return null;
    }

    @SuppressWarnings("ReassignedVariable")
    public static String AyarOku(String kod) {
        String deger = null;
        try {
            Cursor cursor = db.query(M3U_DB.TABLE_AYARLAR, null, "KOD = ?", new String[]{kod}, null, null, null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int DEGERIndex = cursor.getColumnIndex("DEGER");
                        deger = cursor.getString(DEGERIndex);
                    }
                } catch (Exception ex) {
                    Log.d("M3UVeri", ex.getMessage());
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception ex) {
            Log.d("M3UVeri", ex.getMessage());
        }
        return deger;
    }

    public static void AyarYaz(String kod, String deger) {
        try {
            ContentValues values = new ContentValues();
            values.put("KOD", kod);
            values.put("DEGER", deger);
            db.insertWithOnConflict(M3U_DB.TABLE_AYARLAR, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception ex) {
            Log.d("M3UVeri", ex.getMessage());
        }
    }

    public static M3UBilgi.M3UTur TurBul(int position) {
        return position == 2 ? M3UBilgi.M3UTur.seri : (position == 1 ? M3UBilgi.M3UTur.film : M3UBilgi.M3UTur.tv);
    }

    public static void FilmBilgiCek() {
        try {
            new InternettenOku().performNetworkOperationTMDB(mainActivity, db, CekilecekKanallariBul());
        } catch (Exception ex) {
            Log.d("M3UVeri", ex.getMessage());
        }
        mainActivity.internettenCekiliyorYap(0);
    }

    public static ArrayList<M3UBilgi> CekilecekKanallariBul() {
        ArrayList<M3UBilgi> kanallar = new ArrayList<>();
        int yer = -1;
        boolean gruplarBitti = true;
        int grupIndex = 0;
        int itemIndex = 0;
        ArrayList<M3UGrup> grp = null;
        while (true) {
            if (gruplarBitti) {
                yer++;
                if (yer == 0)
                    grp = M3UVeri.filmGruplari;
                else if (yer == 1)
                    grp = M3UVeri.seriGruplari;
                else {
                    break;
                }
                grupIndex = 0;
                itemIndex = 0;
                gruplarBitti = false;
            }

            if (grupIndex >= grp.size()) {
                gruplarBitti = true;
            } else {
                M3UGrup knlGrup = grp.get(grupIndex);

                if (!knlGrup.gelenGrup || itemIndex >= knlGrup.kanallar.size()) {
                    grupIndex++;
                    itemIndex = 0;
                } else {
                    String knlId = knlGrup.kanallar.get(itemIndex);
                    M3UBilgi m3u = tumM3UListesi.getOrDefault(knlId, null);
                    if (m3u != null) {
                        if (m3u.tmdbId == 0) {
                            kanallar.add(m3u);
                            if (kanallar.size() >= 20) break;
                        }
                    }
                    itemIndex++;
                }
            }
        }
        return kanallar;
    }

    public static TVInfo TMDBOkuIc(String selection) {
        Cursor cursor = db.query(M3U_DB.TABLE_TVINFO, null, selection, null, null, null, null);
        TVInfo son = null;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int typeIdIndex = cursor.getColumnIndex("type_id");
                    int nameIndex = cursor.getColumnIndex("name");
                    int titleIndex = cursor.getColumnIndex("title");
                    int original_nameIndex = cursor.getColumnIndex("original_name");
                    int original_titleIndex = cursor.getColumnIndex("original_title");
                    int poster_pathIndex = cursor.getColumnIndex("poster_path");
                    int adultIndex = cursor.getColumnIndex("adult");
                    int popularityIndex = cursor.getColumnIndex("popularity");
                    int backdrop_pathIndex = cursor.getColumnIndex("backdrop_path");
                    int vote_averageIndex = cursor.getColumnIndex("vote_average");
                    int overviewIndex = cursor.getColumnIndex("overview");
                    int first_air_dateIndex = cursor.getColumnIndex("first_air_date");
                    int release_dateIndex = cursor.getColumnIndex("release_date");
                    int original_languageIndex = cursor.getColumnIndex("original_language");
                    int vote_countIndex = cursor.getColumnIndex("vote_count");
                    int origin_countryIndex = cursor.getColumnIndex("origin_country");
                    int genre_idsIndex = cursor.getColumnIndex("genre_ids");

                    do {
                        String typeId = cursor.getString(typeIdIndex);

                        String name = cursor.getString(nameIndex);
                        String title = cursor.getString(titleIndex);
                        String original_name = cursor.getString(original_nameIndex);
                        String original_title = cursor.getString(original_titleIndex);
                        String poster_path = cursor.getString(poster_pathIndex);
                        int adult = cursor.getInt(adultIndex);

                        double popularity = cursor.getDouble(popularityIndex);
                        String backdrop_path = cursor.getString(backdrop_pathIndex);

                        double vote_average = cursor.getDouble(vote_averageIndex);
                        String overview = cursor.getString(overviewIndex);
                        String first_air_date = cursor.getString(first_air_dateIndex);
                        String release_date = cursor.getString(release_dateIndex);
                        String original_language = cursor.getString(original_languageIndex);

                        int vote_count = cursor.getInt(vote_countIndex);
                        String origin_country = cursor.getString(origin_countryIndex);
                        String genre_ids = cursor.getString(genre_idsIndex);

                        TVInfo tvInfo = new TVInfo(typeId, name,
                                title, original_name, original_title,
                                poster_path, adult, popularity, backdrop_path,
                                vote_average, overview, first_air_date, release_date, original_language,
                                vote_count, origin_country, genre_ids);
                        if (selection == null)
                            TMDByeIsle(tvInfo);
                        else
                            son = tvInfo;
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        Log.i("M3UVeri", "TVInfo cursor oldu");
        return son;
    }

    public static void TMDBOku() {
        TMDBOkuIc(null);
        mainActivity.TVInfoOkundu = true;
    }

    public static TVInfo TVInfoOku(int type, long tmdbId) {
        return TMDBOkuIc("type_id ='" + TVInfo.anahtarBul(type, tmdbId) + "'");
    }

    public static boolean GrupIsmiVarMi(M3UBilgi.M3UTur aktifTur, String ad) {
        M3UGrup grup = GrupBul(GrupDegiskenBul(aktifTur), ad, false);
        return grup != null;
    }

    public static int GrupIsminiYaz(M3UBilgi.M3UTur aktifTur, String eskiAd, String yeniAd) {
        if (OrtakAlan.StringIsNUllOrEmpty(yeniAd))
            return R.string.AdBosOlamaz;
        if (yeniAd.startsWith(" "))
            return R.string.AdBosluklaBaslamaz;
        if (Character.isDigit(yeniAd.charAt(0)))
            return R.string.AdRakamlaBaslamaz;
        if (M3UVeri.GrupIsmiVarMi(aktifTur, yeniAd))
            return R.string.ZatenVarOlanBirIsimKullanilamaz;

        if (eskiAd != null) {
            M3UGrup grup = GrupBul(GrupDegiskenBul(aktifTur), eskiAd, false);
            if (grup == null)
                return R.string.EskiGrupBulunamadi;
            if (grup.gelenGrup)
                return R.string.GelenGrupAdDegismez;
            grup.adDegistir(M3UVeri.db, yeniAd);
        } else {
            M3UGrup grup = new M3UGrup(SiraBul(aktifTur), yeniAd, false);
            grup.kaydet(db);
            GrupDegiskenBul(aktifTur).add(grup);
        }
        return 0;
    }

}
