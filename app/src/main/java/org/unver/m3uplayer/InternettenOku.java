package org.unver.m3uplayer;

import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        if(m3uEski!=null)
        {
            m3u.eklemeTarih = m3uEski.eklemeTarih;
            m3u.adult = m3uEski.adult;
            m3u.gizli = m3uEski.gizli;
            m3u.seyredilenSure = m3uEski.seyredilenSure;
            m3u.tmdbId = m3uEski.tmdbId;
        }
        m3u.Yaz(db);
        M3UVeri.GruplaraIsle(m3u, true);
    }

    public void performNetworkOperation(MainActivity mainActivity, SQLiteDatabase db, String kod) {
        String urlStr = "http://panel.atlaspremium11.com:8080/get.php?username=futboloyuncu19341&password=2yegQAhncz&type=m3u_plus&output=mpegts";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    //StringBuilder content = new StringBuilder();
                    String line;

                    //int say = 0;
                    String ilkSatir = null;
                    String suAn = ProgSettings.TarihYAGOl(new Date());

                    while ((line = reader.readLine()) != null) {

                        if (line.startsWith("#EXTINF:"))
                        {
                            ilkSatir = line;
                        }
                        else if (ilkSatir != null)
                        {
                            Ekle(mainActivity, db, kod, ilkSatir, line, suAn);
                        }
                    }

                    reader.close();
                    mainActivity.Cekildi();
                    //Log.d("query", content.toString());

                    // Process the retrieved content or update UI
                    //processResult(content.toString());
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        executor.shutdown();
    }

//    private void processResult(String result) {
//        // Process the result or update UI with the retrieved data
//        if (result != null) {
//            // Update UI or perform further operations
//            // based on the retrieved data
//        } else {
//            // Handle the case when the request failed
//        }
//    }
}

