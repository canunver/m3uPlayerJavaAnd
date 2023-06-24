package org.unver.m3uplayer;

import java.util.ArrayList;
import java.util.List;

public class FilmTurYonetim {
    public static FilmDiziTur[] filmDiziTurler = {
            new FilmDiziTur(12,"Adventure","Macera"),
            new FilmDiziTur(14,"Fantasy","Fantastik"),
            new FilmDiziTur(16,"Animation","Animasyon"),
            new FilmDiziTur(18,"Drama","Dram"),
            new FilmDiziTur(27,"Horror","Korku"),
            new FilmDiziTur(28,"Action","Aksiyon"),
            new FilmDiziTur(35,"Comedy","Komedi"),
            new FilmDiziTur(36,"History","Tarih"),
            new FilmDiziTur(37,"Western","Vahşi Batı"),
            new FilmDiziTur(53,"Thriller","Gerilim"),
            new FilmDiziTur(80,"Crime","Suç"),
            new FilmDiziTur(99,"Documentary","Belgesel"),
            new FilmDiziTur(878,"Science Fiction","Bilim-Kurgu"),
            new FilmDiziTur(9648,"Mystery","Gizem"),
            new FilmDiziTur(10402,"Music","Müzik"),
            new FilmDiziTur(10749,"Romance","Romantik"),
            new FilmDiziTur(10751,"Family","Aile"),
            new FilmDiziTur(10752,"War","Savaş"),
            new FilmDiziTur(10759,"Action & Adventure","Aksiyon & Macera"),
            new FilmDiziTur(10762,"Kids","Çocuklar"),
            new FilmDiziTur(10763,"News","Haber"),
            new FilmDiziTur(10764,"Reality","Gerçeklik"),
            new FilmDiziTur(10765,"Sci-Fi & Fantasy","Bilim Kurgu & Fantazi"),
            new FilmDiziTur(10766,"Soap","Pembe Dizi"),
            new FilmDiziTur(10767,"Talk","Konuşma"),
            new FilmDiziTur(10768,"War & Politics","Savaş & Politik"),
            new FilmDiziTur(10770,"TV Movie","TV film"),
    };

    public static int TurKodBul(String ad)
    {
        for (FilmDiziTur item : filmDiziTurler)
        {
            if (item.adTr == ad || item.adEn == ad)
                return item.kod;
        }
        return -1;
    }

    public static ArrayList<String> TurIsimler(int dilKod)
    {
        ArrayList<String> l = new ArrayList<>();
        for(FilmDiziTur item: filmDiziTurler)
        {
            if (dilKod == 2)
                l.add(item.adTr);
            else
                l.add(item.adEn);
        }
        return l;
    }

    public static String FilmDiziTurAd(int kod, int dilKod) //1 tr, diğer en;
    {
        for (FilmDiziTur item:filmDiziTurler)
        {
            if (item.kod == kod)
            {
                if (dilKod == 1) return item.adTr;
                else return item.adEn;
            }
        }
        return String.valueOf(kod);
    }

    public static String FilmDiziTurAd(int[] kodlar, int dilKod) //1 tr, diğer en;
    {
        String adlar = "";
        for(int item:kodlar)
        {
            if (adlar != "") adlar += ", ";
            adlar += FilmDiziTurAd(item, dilKod);
        }

        return adlar;
    }

    private static class FilmDiziTur {
        private final int kod;
        private final String adEn;
        private final String adTr;

        public FilmDiziTur(int kod, String adEn, String adTr) {
            this.kod = kod;
            this.adEn = adEn;
            this.adTr = adTr;
        }
    }
}
