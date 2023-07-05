package org.unver.m3uplayer;

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

    private void Ekle(MainActivity mainActivity, SQLiteDatabase db, String kod, String ilkSatir, String ikinciSatir, String suAn) {
        M3UBilgi m3u = new M3UBilgi(kod,
                M3UListeArac.DegerBul(ilkSatir, "tvg-id"),
                M3UListeArac.DegerBul(ilkSatir, "tvg-name"),
                M3UListeArac.DegerBul(ilkSatir, "tvg-logo"),
                M3UListeArac.DegerBul(ilkSatir, "group-title"),
                ikinciSatir,
                suAn);
        M3UBilgi m3uEski = M3UVeri.tumM3Ular.getOrDefault(m3u.ID, null);

        if (m3uEski != null) {
            m3u.eklemeTarih = m3uEski.eklemeTarih;
            m3u.adult = m3uEski.adult;
            m3u.gizli = m3uEski.gizli;
            m3u.seyredilenSure = m3uEski.seyredilenSure;
            m3u.tmdbId = m3uEski.tmdbId;
        }
        m3u.Yaz(db);
        M3UVeri.GruplaraIsle(m3u, true, false);
    }

    public void performNetworkOperation(MainActivity mainActivity, SQLiteDatabase db, String kod) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Log.i("M3UVeri", "Internetten veri alınacak");
                URL url = new URL(ProgSettings.m3u_internet_adresi_1);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.i("M3UVeri", "Internetten veri alındı");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    boolean hataVar;
                    String line;
                    db.beginTransaction();
                    try {
                        int say = 0;
                        String ilkSatir = null;
                        String suAn = ProgSettings.TarihYAGOl(new Date());

                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("#EXTINF:")) {
                                ilkSatir = line;
                            } else if (ilkSatir != null) {
                                Ekle(mainActivity, db, kod, ilkSatir, line, suAn);
                                say++;
                                if (say % 1000 == 1)
                                    Log.i("M3UVeri", "Internet verisi işleniyor:" + say);
                            }
                        }
                        hataVar = false;
                    } catch (Exception ex) {
                        hataVar = true;
                        Log.i("M3UVeri", "Internet verisi işlenemedi:" + ex.getMessage());
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    reader.close();
                    if (!hataVar) {
                        Log.i("M3UVeri", "Internet verisi işlendi");
                        ProgSettings.sonCekilmeZamaniYaz();
                    }
                }
                connection.disconnect();
                mainActivity.Cekildi();
            } catch (IOException e) {
                e.printStackTrace();
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

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

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

    public TVResponse TMDBInfoBul(M3UBilgi m3u) throws Exception {
        return getTmdbInfo(m3u.TMDBTur(), m3u.SorguYap());
    }

    public static TVResponse getTmdbInfo(String tmdbTur, String sorgu) {
        Log.i("M3UVeri", "TMDB veri alınacak");
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            if (!trustInit) doTrustInit();

            String urlStr = String.format("https://api.themoviedb.org/3/search/%s?language=tr-TR&query=%s",tmdbTur, sorgu);
            Log.d("M3UVeri", "TMDB veri alınacak:" + urlStr + " " + ProgSettings.tmdb_erisim_anahtar);
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + ProgSettings.tmdb_erisim_anahtar);
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
        if (response == null) return null;
        if (response.length() == 0) return null;

        Gson gson = new Gson();

        TVResponse gelenObj = gson.fromJson(response.toString(), TVResponse.class);
        if (gelenObj != null) {
            Log.d("M3UVeri", "TMDB, Gelen nesne:" + gelenObj.ToListStr());
        } else
            Log.d("M3UVeri", "TMDB, Gelen nesne çevrilemedi" + response.toString());
        return gelenObj;
    }
}

