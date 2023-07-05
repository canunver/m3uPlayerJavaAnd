package org.unver.m3uplayer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;

public class M3UVeri {
    public static Hashtable<String, TVInfo> tumTMDBler = new Hashtable<>();
    public static Hashtable<String, M3UBilgi> tumM3Ular = new Hashtable<>();
    public static ArrayList<M3UGrup> tvGruplari = new ArrayList<>();
    public static ArrayList<M3UGrup> filmGruplari = new ArrayList<>();
    public static ArrayList<M3UGrup> seriGruplari = new ArrayList<>();
    public static Hashtable<String, String> tumSerilerAd = new Hashtable<>();
    private static MainActivity mainActivity;
    public static int minYil = 10000;
    private static M3U_DB dbHelper = null;
    public static SQLiteDatabase db = null;

    public static ArrayList<M3UGrup> GrupKodBul(int position) {
        if (position == 0) return tvGruplari;
        else if (position == 1) return filmGruplari;
        else return seriGruplari;
    }

    public static void OkuBakayim(MainActivity mainActivity) {
        M3UVeri.mainActivity = mainActivity;
        if (dbHelper == null)
            dbHelper = new M3U_DB(M3UVeri.mainActivity);
        if (db == null)
            db = dbHelper.getWritableDatabase();

        Log.i("M3UVeri", "M3U cursor olacak");
        Cursor cursor = db.query(M3U_DB.TABLE_M3U, null, null, null, null, null, "groupTitle, tvgName");
        Log.i("M3UVeri", "M3U cursor oldu");
        tumM3Ular.clear();
        tumSerilerAd.clear();
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
                        int gizli = cursor.getInt(gizliIndex);
                        int adult = cursor.getInt(adultIndex);
                        int tmdbId = cursor.getInt(tmdbIdIndex);

                        String guncellemeTarih = cursor.getString(guncellemeTarihIndex);
                        long seyredilenSure = cursor.getLong(seyredilenSureIndex);

                        M3UBilgi m3u = new M3UBilgi(ID, tvgId, tvgName,
                                tvgLogo, groupTitle, urlAdres,
                                eklemeTarih, gizli, adult, tmdbId, guncellemeTarih, seyredilenSure);
                        if (m3u.filmYilInt > 0 && m3u.filmYilInt < minYil)
                            minYil = m3u.filmYilInt;
                        GruplaraIsle(m3u, true, true);
                    } while (cursor.moveToNext());
                    if (minYil > 3000) minYil = 0;
                }
            } finally {
                cursor.close();
            }
            Log.i("M3UVeri", "cursor kapandı");
//            db.beginTransaction();
//            tumSerilerAd.forEach(new BiConsumer<String, String>() {
//                @Override
//                public void accept(String s, String s2) {
//                    M3UBilgi seri = tumM3Ular.getOrDefault(s2, null);
//                    if (seri != null) {
//                        Log.i("M3UVeri", "s2:" + s2 + ", seri:" + seri.tvgName + " id:" + seri.ID);
//                        seri.Yaz(db);
//                    }
//                }
//            });
//            db.setTransactionSuccessful();
//            db.endTransaction();
//            Log.i("M3UVeri", "Bu sefer olacak mı, Serileri veri tabanına yazdık kardeşim, bu kodu sil be koçum");

        }
    }

    public static void TMDByeIsle(TVInfo tvInfo) {
        M3UVeri.tumTMDBler.put(tvInfo.anahtarBul(), tvInfo);
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

    private static void GrubaEkle(ArrayList<M3UGrup> anaGrup, M3UBilgi m3u, boolean gelenGrup) {
        M3UGrup grp = GrupBulYoksaEkle(anaGrup, m3u.groupTitle, gelenGrup, true);
        if (grp.ProgBul(m3u.ID) == null)
            grp.kanallar.add(m3u.ID);
    }

    public static void GruplaraIsle(M3UBilgi m3u, boolean gelenGrup, boolean veriTabanindan) {
        if (m3u.Tur == M3UBilgi.M3UTur.tv) {
            tumM3Ular.put(m3u.ID, m3u);
            GrubaEkle(tvGruplari, m3u, gelenGrup);
        } else if (m3u.Tur == M3UBilgi.M3UTur.film) {
            tumM3Ular.put(m3u.ID, m3u);
            GrubaEkle(filmGruplari, m3u, gelenGrup);
        } else if (m3u.Tur == M3UBilgi.M3UTur.seri) {
            M3UBilgi seri;
            String mevcutId = tumSerilerAd.getOrDefault(m3u.seriAd, null);
            if (m3u.ID.endsWith("_S")) {
                if (mevcutId == null) {
                    GrubaEkle(seriGruplari, m3u, gelenGrup);
                } else {
                    seri = tumM3Ular.get(mevcutId);
                    m3u.seriSezonlari = seri.seriSezonlari;
                    m3u.eklemeTarih = seri.eklemeTarih;
                }
                tumSerilerAd.put(m3u.seriAd, m3u.ID);
                tumM3Ular.put(m3u.ID, m3u);
            } else {
                tumM3Ular.put(m3u.ID, m3u);
                if (mevcutId == null) {
                    seri = new M3UBilgi(m3u);
                    seri.tmdbId = 0;
                    tumM3Ular.put(seri.ID, seri);
                    GrubaEkle(seriGruplari, seri, gelenGrup);
                    tumSerilerAd.put(seri.seriAd, seri.ID);
                    if (!veriTabanindan)
                        seri.Yaz(M3UVeri.db);
                } else {
                    seri = tumM3Ular.getOrDefault(mevcutId, null);
                }
                Sezon sezon = SezonBulYoksaEkle(seri, m3u.sezon);
                Bolum blm = sezon.BolumBul(m3u.bolum);
                if (blm == null)
                    sezon.bolumler.add(new Bolum(m3u.ID, m3u.bolum));
                else
                    blm.AddId(m3u.ID);
                if (m3u.eklemeTarih.compareTo(seri.eklemeTarih) > 0)
                    seri.eklemeTarih = m3u.eklemeTarih;
            }
        }
    }

//    private static void Logla(String int_sezon_ekle, M3UBilgi m3u, M3UBilgi seri) {
//        if (m3u == null && seri == null)
//            Log.d("M3USeri", int_sezon_ekle + " Hoppalaaa");
//        else {
//            if (m3u != null && m3u.seriAd.startsWith("White Line") || seri != null && seri.seriAd.startsWith("White Line"))
//                Log.d("M3USeri", int_sezon_ekle + " *** " + StringYap(m3u) + " *** " + StringYap(seri));
//        }
//    }

//    private static String StringYap(M3UBilgi m3u) {
//        if (m3u == null) return "null";
//        return "ID:" + m3u.ID + ", Ad:" + m3u.seriAd + ", sezon:" + m3u.seriSezonlari.size();
//    }

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
        Log.i("M3UVeri", "CekBakalim başlıyor");
        try {
            new InternettenOku().performNetworkOperation((MainActivity) mainActivity, db, "A");
        } catch (Exception e) {
            Log.d("Hata", e.getMessage());
        }
        Log.i("M3UVeri", "CekBakalim bitti");
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

    public static M3UGrup GrupBul(ArrayList<M3UGrup> grupListesi, String aktifGrupAd) {
        for (M3UGrup g : grupListesi) {
            if (g.grupAdi.equals(aktifGrupAd))
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
            new InternettenOku().performNetworkOperationTMDB((MainActivity) mainActivity, db, CekilecekKanllariBul());
        } catch (Exception ex) {
            Log.d("M3UVeri", ex.getMessage());
        }
        mainActivity.internettenCekiliyorYap(0);
    }

    public static ArrayList<M3UBilgi> CekilecekKanllariBul() {
        ArrayList<M3UBilgi> kanallar = new ArrayList<>();
        int yer = -1;
        boolean gruplarBitti = true;
        int grupIndex = 0;
        int progIndex = 0;
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
                progIndex = 0;
                gruplarBitti = false;
            }

            if (grupIndex >= grp.size()) {
                gruplarBitti = true;
            } else {
                M3UGrup knlGrup = grp.get(grupIndex);

                if (!knlGrup.gelenGrup || progIndex >= knlGrup.kanallar.size()) {
                    grupIndex++;
                    progIndex = 0;
                } else {
                    String knlId = knlGrup.kanallar.get(progIndex);
                    M3UBilgi m3u = tumM3Ular.getOrDefault(knlId, null);
                    if (m3u != null) {
                        if (m3u.tmdbId == 0) {
                            kanallar.add(m3u);
                            if (kanallar.size() >= 20) break;
                        }
                    }
                    progIndex++;
                }
            }
        }
        return kanallar;
    }

    public static void TMDBOku() {
        Log.i("M3UVeri", "TVInfo cursor olacak");
        Cursor cursor = db.query(M3U_DB.TABLE_TVINFO, null, null, null, null, null, null);
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
                        TMDByeIsle(tvInfo);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        Log.i("M3UVeri", "TVInfo cursor oldu");
        mainActivity.TVInfoOkundu = true;
    }
}
