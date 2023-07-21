package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class InternettenOku {

    private void Ekle(SQLiteDatabase db, String kod, String ilkSatir, String ikinciSatir, String suAn) {
        M3UBilgi m3u = new M3UBilgi(kod,
                M3UListeArac.DegerBul(ilkSatir, "tvg-id"),
                M3UListeArac.DegerBul(ilkSatir, "tvg-name"),
                M3UListeArac.DegerBul(ilkSatir, "tvg-logo"),
                M3UListeArac.DegerBul(ilkSatir, "group-title"),
                ikinciSatir,
                suAn);
        M3UBilgi m3uEski = M3UVeri.tumM3UListesi.getOrDefault(m3u.ID, null);

        if (m3uEski != null) {
            m3u.eklemeTarih = m3uEski.eklemeTarih;
            m3u.yetiskin = m3uEski.yetiskin;
            m3u.gizli = m3uEski.gizli;
            m3u.seyredilenSure = m3uEski.seyredilenSure;
            m3u.tmdbId = m3uEski.tmdbId;
        }
        m3u.Yaz(db);
        M3UVeri.GruplaraIsle(m3u, true);
    }

    public void performNetworkOperation(MainActivity mainActivity, SQLiteDatabase db) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            for (int i = 1; i <= 3; i++) {
                try {
                    String kod;
                    String urlAddress;
                    if (i == 1) {
                        kod = "A";
                        urlAddress = OrtakAlan.m3uAdresAl(1);
                    } else if (i == 2) {
                        kod = "B";
                        urlAddress = OrtakAlan.m3uAdresAl(2);
                    } else { //3
                        kod = "C";
                        urlAddress = OrtakAlan.m3uAdresAl(3);
                    }
                    if (OrtakAlan.StringIsNUllOrEmpty(urlAddress)) continue;
                    //Log.i("M3UVeri", "Internetten veri alınacak");
                    URL url = new URL(urlAddress);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        //Log.i("M3UVeri", "Internetten veri alındı");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        boolean hataVar;
                        String line;
                        db.beginTransaction();
                        try {
///                            int say = 0;
                            String ilkSatir = null;
                            String suAn = OrtakAlan.TarihYAGOl(new Date());

                            while ((line = reader.readLine()) != null) {
                                if (line.startsWith("#EXTINF:")) {
                                    ilkSatir = line;
                                } else if (ilkSatir != null) {
                                    Ekle(db, kod, ilkSatir, line, suAn);
///                                    say++;
///                                    if (say % 1000 == 1)
///                                        Log.i("M3UVeri", "Internet verisi işleniyor:" + say);
                                }
                            }
                            hataVar = false;
                        } catch (Exception ex) {
                            hataVar = true;
                            Log.e("M3UVeri", "Internet verisi işlenemedi:" + ex.getMessage());
                        }
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        reader.close();
                        if (!hataVar) {
                            //Log.i("M3UVeri", "Internet verisi işlendi");
                            OrtakAlan.sonCekilmeZamaniniSimdiYap();
                        }
                    }
                    connection.disconnect();
                    mainActivity.Cekildi();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        executor.shutdown();
    }

    public void performNetworkOperationTMDB(MainActivity mainActivity, SQLiteDatabase db, ArrayList<M3UBilgi> kanallar) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            boolean globHata = false;
            ArrayList<TVResponse> cevaplar = new ArrayList<>();
            try {
                for (M3UBilgi m3u : kanallar) {
                    TVResponse ti;
                    try {
                        ti = TMDBInfoBul(m3u);
                    } catch (Exception ex) {
                        Log.d("M3UVeri", "Hata:" + ex.getMessage());
                        ti = null;
                    }
                    cevaplar.add(ti);
                }
            } catch (Exception e) {
                globHata = true;
                e.printStackTrace();
            }

            if (!globHata) {
                db.beginTransaction();
                for (int i = 0; i < kanallar.size(); i++) {
                    M3UBilgi m3u = kanallar.get(i);
                    TVResponse ti = cevaplar.get(i);
                    TVInfo tvInfo;
                    if (ti == null || ti.resultsSay() != 1) {
                        m3u.tmdbId = -1;
                        Log.d("M3UVeri", "-1 olarak yazılacak");
                        tvInfo = null;
                    } else {
                        tvInfo = ti.InfoAl(0);
                        tvInfo.type = M3UVeri.SiraBul(m3u.Tur);
                        m3u.tmdbId = tvInfo.id;
                        Log.d("M3UVeri", tvInfo.anahtarBul() + " olarak yazılacak");
                    }
                    if (tvInfo != null) {
                        tvInfo.Yaz(M3UVeri.db);
                        M3UVeri.TMDByeIsle(tvInfo);
                    }
                    m3u.Yaz(M3UVeri.db);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
            mainActivity.Cekildi();
        });

        executor.shutdown();
    }

    public static boolean trustInit = false;

    public static void doTrustInit() throws Exception {

        @SuppressLint("CustomX509TrustManager")
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @SuppressLint("TrustAllX509TrustManager")
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        trustInit = true;
    }

    public TVResponse TMDBInfoBul(M3UBilgi m3u) {
        return getTmdbInfo(m3u.TMDBTur(), m3u.SorguYap());
    }

    private TMDBBolum TMDBInfoBulSeri(M3UBilgi m3u, String sezonAd, String bolumNo) {
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            if (!trustInit) doTrustInit();
            String urlStr = String.format("https://api.themoviedb.org/3/tv/%s/season/%s/episode/%s?language=%s", m3u.tmdbId, sezonAd.substring(1), bolumNo.substring(1), OrtakAlan.TMDBDil);

            Log.d("M3UVeri", "TMDB bölüm alınacak:" + urlStr + " " + OrtakAlan.tmdb_erisim_anahtar);
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + OrtakAlan.tmdb_erisim_anahtar);
            connection.setRequestProperty("accept", "application/json");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            } else
                Log.d("M3UVeri", "TMDB veri OK Değil: " + responseCode);
        } catch (Exception ex) {
            Log.d("M3UVeri", "TMDB veri alınamadı" + ex.getMessage());
        }
        if (connection != null)
            connection.disconnect();
        if (response.length() == 0) return null;

        Gson gson = new Gson();

        TMDBBolum gelenObj = gson.fromJson(response.toString(), TMDBBolum.class);
        if (gelenObj == null)
            Log.d("M3UVeri", "TMDB, Gelen nesne çevrilemedi" + response);
        return gelenObj;
    }

    public static TVResponse getTmdbInfo(String tmdbTur, String sorgu) {
        Log.i("M3UVeri", "TMDB veri alınacak");
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            if (!trustInit) doTrustInit();

            String urlStr = String.format("https://api.themoviedb.org/3/search/%s?language=%s&query=%s", tmdbTur, OrtakAlan.TMDBDil, sorgu);
            Log.d("M3UVeri", "TMDB veri alınacak:" + urlStr + " " + OrtakAlan.tmdb_erisim_anahtar);
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + OrtakAlan.tmdb_erisim_anahtar);
            connection.setRequestProperty("accept", "application/json");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            }
        } catch (Exception ex) {
            Log.d("M3UVeri", "TMDB veri alınamadı" + ex.getMessage());
        }
        if (connection != null)
            connection.disconnect();
        if (response.length() == 0) return null;

        Gson gson = new Gson();

        TVResponse gelenObj = gson.fromJson(response.toString(), TVResponse.class);
        if (gelenObj == null)
            Log.d("M3UVeri", "TMDB, Gelen nesne çevrilemedi" + response);
        return gelenObj;
    }

    @SuppressWarnings("all")
    class TMDBBolum {
        public String air_date;
        public int episode_number;
        public String name;
        public String overview;
        public long id;
        public int season_number;
        public double vote_average;

        public String ToListStr() {
            return id + ":" + name + "(" + air_date + ", " + vote_average + ")";
        }
    }

    class BolumBilgi {
        private final TMDBBolum ti;
        private final Bolum blm;

        public BolumBilgi(TMDBBolum ti, Bolum blm) {
            this.ti = ti;
            this.blm = blm;
        }
    }

    public void performNetworkOperationTMDBSeri(MainActivity mainActivity, SQLiteDatabase db, M3UBilgi m3u, YayinListesiAdapter yayinListesiAdapter, int pos) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            boolean globHata = false;
            ArrayList<BolumBilgi> cevaplar = new ArrayList<>();
            try {
                for (Sezon sez : m3u.seriSezonlari) {
                    for (Bolum blm : sez.bolumler) {
                        TMDBBolum tmdbBolum;
                        try {
                            tmdbBolum = TMDBInfoBulSeri(m3u, sez.sezonAd, blm.bolum);
                        } catch (Exception ex) {
                            Log.d("M3UVeri", "Hata:" + ex.getMessage());
                            tmdbBolum = null;
                        }
                        cevaplar.add(new BolumBilgi(tmdbBolum, blm));
                    }
                }
            } catch (Exception e) {
                globHata = true;
                e.printStackTrace();
            }

            if (!globHata) {
                db.beginTransaction();
                for (int i = 0; i < cevaplar.size(); i++) {
                    BolumBilgi bb = cevaplar.get(i);
                    TVInfo tvInfo;
                    long yazId = -1;
                    if (bb.ti == null) {
                        //Log.d("M3UVeri", "-1 olarak yazılacak");
                        tvInfo = null;
                    } else {
                        tvInfo = new TVInfo(9, bb.ti.id, bb.ti.name, bb.ti.air_date, bb.ti.overview, bb.ti.vote_average);
                        yazId = tvInfo.id;
                        //Log.d("M3UVeri", tvInfo.anahtarBul() + " olarak yazılacak");
                    }
                    if (tvInfo != null) {
                        tvInfo.Yaz(M3UVeri.db);
                    }
                    for (String bolumM3UId : bb.blm.ids) {
                        M3UBilgi bolM3u = M3UVeri.tumM3UListesi.getOrDefault(bolumM3UId, null);
                        if (bolM3u != null) {
                            bolM3u.tmdbId = yazId;
                            bolM3u.Yaz(M3UVeri.db);
                        }
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                mainActivity.handler.postDelayed(() -> yayinListesiAdapter.notifyItemChanged(pos), 50);
            }
            mainActivity.Cekildi();
        });

        executor.shutdown();
    }
}

