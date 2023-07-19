package org.unver.m3uplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class M3UListeArac {

    public static void ImageYukle(ImageView imageView, String url)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            try {
                InputStream iStream = new URL(url).openStream();
                Bitmap image = BitmapFactory.decodeStream(iStream);
                handler.post(()-> imageView.setImageBitmap(image));
            } catch (Exception e) {
                Log.d("URL", e.getMessage());
            }
        });
    }

    @SuppressWarnings("all")
    public static String DegerBul(String line, String anahtar) {
        String deger = "";
        int yer = line.indexOf(anahtar + "=");
        if (yer > 0)
        {
            yer = yer + anahtar.length() + 1;
            boolean basladi = false;
            while (yer < line.length())
            {
                char c = line.charAt(yer);
                if (c == '"')
                {
                    if (!basladi) basladi = true;
                    else return deger;
                }
                else if (basladi)
                    deger += c;
                yer++;
            }
        }
        return deger;
    }

    public static boolean IsNullOrWhiteSpace(String str) {
        if (str == null) return true;
        if (str.length() == 0) return true;

        if( str.trim().length() == 0) return true;
        return false;
    }
}
