package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrtakAlan {
    public static boolean son_tv_kanalini_oynatarak_basla;
    public static boolean tamEkranBaslat;
    public static String tmdb_erisim_anahtar;
    public static String m3u_internet_adresi_3;
    public static String m3u_internet_adresi_2;
    public static String m3u_internet_adresi_1;
    public static M3UBilgi.M3UTur sonM3UTur;
    public static String sonGrup;
    public static String sonProgramID;
    public static String sonTVGrup;
    public static String sonTVProgramID;
    public static long sonCekilmeZamani;
    public static String TMDBDil = "";
    public static boolean gizlilerVar = false;
    public static boolean yetiskinlerVar = false;
    public static boolean parolaVar = false;
    public static int TMDBTurDil = 1;
    public static int tmdb_erisim_dil = 0;
    public static String parola = "";
    public static String sonSezon = "";
    public static String sonBolum = "";

    public static int ConvertToInt32(String strDeger, int varsayilanDeger) {
        if (!StringIsNUllOrEmpty(strDeger)) {
            try {
                return Integer.parseInt(strDeger);
            } catch (Exception ignored) {
            }
        }
        return varsayilanDeger;
    }

    public static long ConvertToLong(String strDeger, long varsayilanDeger) {
        if (!StringIsNUllOrEmpty(strDeger)) {
            try {
                return Long.parseLong(strDeger);
            } catch (Exception ignored) {
            }
        }
        return varsayilanDeger;
    }

    public static String TarihYAGOl(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static void AyarlariOku(String languageCode) {
        m3u_internet_adresi_1 = M3UVeri.AyarOku("m3u_internet_adresi_1");
        m3u_internet_adresi_2 = M3UVeri.AyarOku("m3u_internet_adresi_2");
        m3u_internet_adresi_3 = M3UVeri.AyarOku("m3u_internet_adresi_3");
        tmdb_erisim_anahtar = M3UVeri.AyarOku("tmdb_erisim_anahtar");
        son_tv_kanalini_oynatarak_basla = ConvertToInt32(M3UVeri.AyarOku("son_tv_kanalini_oynatarak_basla"), 0) == 1;
        tamEkranBaslat = ConvertToInt32(M3UVeri.AyarOku("tamEkranBaslat"), 0) == 1;
        tmdb_erisim_dil = ConvertToInt32(M3UVeri.AyarOku("tmdb_erisim_dil"), 0);
        if (tmdb_erisim_dil == 1) {
            TMDBDil = "tr-TR";
        }
        sonTVGrup = M3UVeri.AyarOku("sonTVGrup");
        sonTVProgramID = M3UVeri.AyarOku("sonTVProgramID");
        sonM3UTur = M3UVeri.TurBul(ConvertToInt32(M3UVeri.AyarOku("sonM3UTur"), 0));
        sonGrup = M3UVeri.AyarOku("sonGrup");
        sonProgramID = M3UVeri.AyarOku("sonProgramID");
        sonCekilmeZamani = ConvertToLong(M3UVeri.AyarOku("sonCekilmeZamani"), 0);
        parola = M3UVeri.AyarOku("parola");
        sonSezon = M3UVeri.AyarOku("sonSezon");
        sonBolum = M3UVeri.AyarOku("sonBolum");

        if (languageCode.startsWith("tr"))
            TMDBTurDil = 2;
        else
            TMDBTurDil = 1;
    }

    public static void AyarlariYaz() {
        M3UVeri.AyarYaz("m3u_internet_adresi_1", m3u_internet_adresi_1);
        M3UVeri.AyarYaz("m3u_internet_adresi_2", m3u_internet_adresi_2);
        M3UVeri.AyarYaz("m3u_internet_adresi_3", m3u_internet_adresi_3);
        M3UVeri.AyarYaz("tmdb_erisim_anahtar", tmdb_erisim_anahtar);
        M3UVeri.AyarYaz("son_tv_kanalini_oynatarak_basla", son_tv_kanalini_oynatarak_basla ? "1" : "0");
        M3UVeri.AyarYaz("tamEkranBaslat", tamEkranBaslat ? "1" : "0");
        M3UVeri.AyarYaz("tmdb_erisim_dil", String.valueOf(tmdb_erisim_dil));
    }

    public static void TarihceyeEkle(M3UBilgi.M3UTur aktifTur, String aktifGrupAd, String id, String sezon, String bolum) {
        if (!StringIsNUllOrEmpty(aktifGrupAd) && !StringIsNUllOrEmpty(id)) {
            if (aktifTur != sonM3UTur || !aktifGrupAd.equals(sonGrup) || !id.equals(sonProgramID)) {
                sonM3UTur = aktifTur;
                sonGrup = aktifGrupAd;
                sonProgramID = id;
                if (sonM3UTur == M3UBilgi.M3UTur.tv) {
                    sonTVGrup = aktifGrupAd;
                    sonTVProgramID = id;
                    M3UVeri.AyarYaz("sonTVGrup", sonTVGrup);
                    M3UVeri.AyarYaz("sonTVProgramID", sonTVProgramID);
                }
                M3UVeri.AyarYaz("sonM3UTur", Integer.toString(M3UVeri.SiraBul(sonM3UTur)));
                M3UVeri.AyarYaz("sonGrup", sonGrup);
                M3UVeri.AyarYaz("sonProgramID", sonProgramID);
            }
            if (!OrtakAlan.StringIsNUllOrEmpty(sezon) && !sezon.equals(sonSezon)) {
                sonSezon = sezon;
                M3UVeri.AyarYaz("sonSezon", sezon);
            }
            if (!OrtakAlan.StringIsNUllOrEmpty(bolum) && !bolum.equals(sonBolum)) {
                sonBolum = bolum;
                M3UVeri.AyarYaz("sonBolum", bolum);
            }
        }
    }

    public static boolean StringIsNUllOrEmpty(String str) {
        if (str == null) return true;
        if (str.length() == 0) return true;
        return false;
    }

    public static void sonCekilmeZamaniniSimdiYap() {
        sonCekilmeZamaniYaz(Calendar.getInstance().getTimeInMillis());
    }

    public static void sonCekilmeZamaniYaz(long zaman) {
        sonCekilmeZamani = zaman;
        M3UVeri.AyarYaz("sonCekilmeZamani", Long.toString(sonCekilmeZamani));
    }

    public static String ConvertToStr(String[] strDizi) {
        if (strDizi == null || strDizi.length == 0)
            return null;
        String retVal = "";
        for (String s : strDizi) {
            if (!OrtakAlan.StringIsNUllOrEmpty(retVal)) retVal += ";";
            retVal += s;
        }
        return retVal;
    }

    public static String ConvertToStr(int[] intDizi) {
        if (intDizi == null || intDizi.length == 0)
            return null;
        String retVal = "";
        for (int s : intDizi) {
            if (!OrtakAlan.StringIsNUllOrEmpty(retVal)) retVal += ";";
            retVal += s;
        }
        return retVal;
    }

    public static String[] ConvertToArrayStr(String str) {
        if (OrtakAlan.StringIsNUllOrEmpty(str)) return null;
        return str.split(";");
    }

    public static int[] ConvertToArrayInt(String str) {
        if (OrtakAlan.StringIsNUllOrEmpty(str)) return null;
        String[] dizgiler = str.split(";");
        int[] tamsayilar = new int[dizgiler.length];
        for (int i = 0; i < dizgiler.length; i++) {
            tamsayilar[i] = OrtakAlan.ConvertToInt32(dizgiler[i], 0);
        }
        return tamsayilar;
    }

    public static String GizliBul(Context c) {
        return c.getString(R.string.GizliIlkHarf);
    }

    public static String YetiskinBul(Context c) {
        return c.getString(R.string.YetiskinIlkHarf);
    }

    public static String DogruysaDondur(boolean kodMu, String kod) {
        if (kodMu) return kod;
        return "_";
    }

    public static boolean parolaDogruMu(String parola) {
        return parola.equals(OrtakAlan.parolaBul());
    }

    public static void parolaGirildi(String parola) {
        if (parola.equals(OrtakAlan.parolaBul())) {
            parolaVar = true;
            M3UVeri.mainActivity.switchAdultRL.setVisibility(View.VISIBLE);
        }
    }

    public static void ParolaAta(String parolaYeni) {
        parola = parolaYeni;
        M3UVeri.AyarYaz("parola", parola);
    }

    private static String parolaBul() {
        if (OrtakAlan.StringIsNUllOrEmpty(parola))
            return "1111";
        else
            return parola;
    }

    static String[] formatDizgileri = {"<b>"};
    static String[] formatBitisDizgileri = {"</b>"};

    public static SpannableString SpanYap(String s) {
        SpannableString spannableString;

        try {
            ArrayList<Integer[]> yerDizisi = new ArrayList<>();
            while (true) {
                int[] yerler = ilkUyaniBul(formatDizgileri, s);
                if (yerler[0] == -1 || yerler[1] == -1) break;
                String openTag = formatDizgileri[yerler[0]];
                String closeTag = formatBitisDizgileri[yerler[0]];

                int baslaYer = yerler[1];
                int bitisYer = s.indexOf(closeTag);
                if (bitisYer > baslaYer) {
                    s = s.substring(0, baslaYer) + s.substring(baslaYer + openTag.length(), bitisYer) + s.substring(bitisYer + closeTag.length());
                    yerDizisi.add(new Integer[]{baslaYer, bitisYer - baslaYer - openTag.length()});
                } else
                    s = s.substring(0, baslaYer) + s.substring(baslaYer + openTag.length());
            }

            spannableString = new SpannableString(s);
            for (Integer[] yerler : yerDizisi
            ) {
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                spannableString.setSpan(boldSpan, yerler[0], yerler[0] + yerler[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception ex) {
            spannableString = new SpannableString(s);
        }
        return spannableString;
    }

    public static int[] ilkUyaniBul(String[] formatDizisi, String formatlanacakDizgi) {
        int bulunanIndeks = -1;
        int bulunanYer = -1;

        for (int i = 0; i < formatDizisi.length; i++) {
            int yer = formatlanacakDizgi.indexOf(formatDizisi[i]);
            if (yer != -1 && (bulunanYer == -1 || yer < bulunanYer)) {
                bulunanIndeks = i;
                bulunanYer = yer;
            }
        }

        int[] result = new int[2];
        result[0] = bulunanIndeks;
        result[1] = bulunanYer;

        return result;
    }

    public static void adresDegerAta(int adresNo, String yeniAdres) {
        String eskiAdres = m3uAdresAl(adresNo);
        if (StringIsNUllOrEmpty(eskiAdres)) {
            if (!StringIsNUllOrEmpty(yeniAdres)) {
                adresDegerAtaDogrudan(adresNo, yeniAdres);
            }
        } else {
            if (StringIsNUllOrEmpty(yeniAdres)) {
                adresDegerAtaDogrudan(adresNo, yeniAdres);
            } else if (!yeniAdres.equals(eskiAdres)) {
                adresDegerAtaDogrudan(adresNo, yeniAdres);
            }
        }
    }

    private static void adresDegerAtaDogrudan(int adresNo, String yeniAdres) {
        if (adresNo == 1) m3u_internet_adresi_1 = yeniAdres;
        else if (adresNo == 2) m3u_internet_adresi_2 = yeniAdres;
        else m3u_internet_adresi_3 = yeniAdres;
        sonCekilmeZamaniYaz(0);
        ArkaPlanIslemleri.performBackgroundTask(false);
    }

    public static String m3uAdresAl(int adresNo) {
        if (adresNo == 1) return m3u_internet_adresi_1;
        else if (adresNo == 2) return m3u_internet_adresi_2;
        else return m3u_internet_adresi_3;
    }
}
