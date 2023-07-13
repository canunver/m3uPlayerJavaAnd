package org.unver.m3uplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import java.text.SimpleDateFormat;
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
    public static String TMDBDil = "tr-TR";
    public static boolean gizlilerVar = false;
    public static boolean yetiskinlerVar = false;
    public static boolean parolaVar = false;
    private static String parola = "1111";

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

    public static void AyarlariOku() {
        m3u_internet_adresi_1 = M3UVeri.AyarOku("m3u_internet_adresi_1");
        m3u_internet_adresi_2 = M3UVeri.AyarOku("m3u_internet_adresi_2");
        m3u_internet_adresi_3 = M3UVeri.AyarOku("m3u_internet_adresi_3");
        tmdb_erisim_anahtar = M3UVeri.AyarOku("tmdb_erisim_anahtar");
        son_tv_kanalini_oynatarak_basla = ConvertToInt32(M3UVeri.AyarOku("son_tv_kanalini_oynatarak_basla"), 0) == 1;
        tamEkranBaslat = ConvertToInt32(M3UVeri.AyarOku("tamEkranBaslat"), 0) == 1;

        sonTVGrup = M3UVeri.AyarOku("sonTVGrup");
        sonTVProgramID = M3UVeri.AyarOku("sonTVProgramID");
        sonM3UTur = M3UVeri.TurBul(ConvertToInt32(M3UVeri.AyarOku("sonM3UTur"), 0));
        sonGrup = M3UVeri.AyarOku("sonGrup");
        sonProgramID = M3UVeri.AyarOku("sonProgramID");
        sonCekilmeZamani = ConvertToLong(M3UVeri.AyarOku("sonCekilmeZamani"), 0);
    }

    public static void AyarlariYaz() {
        M3UVeri.AyarYaz("m3u_internet_adresi_1", m3u_internet_adresi_1);
        M3UVeri.AyarYaz("m3u_internet_adresi_2", m3u_internet_adresi_2);
        M3UVeri.AyarYaz("m3u_internet_adresi_3", m3u_internet_adresi_3);
        M3UVeri.AyarYaz("tmdb_erisim_anahtar", tmdb_erisim_anahtar);
        M3UVeri.AyarYaz("son_tv_kanalini_oynatarak_basla", son_tv_kanalini_oynatarak_basla ? "1" : "0");
        M3UVeri.AyarYaz("tamEkranBaslat", tamEkranBaslat ? "1" : "0");
    }

    public static void TarihceyeEkle(M3UBilgi.M3UTur aktifTur, String aktifGrupAd, String id) {
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
        }
    }

    public static boolean StringIsNUllOrEmpty(String str) {
        if (str == null) return true;
        if (str.length() == 0) return true;
        return false;
    }

    public static void sonCekilmeZamaniYaz() {
        sonCekilmeZamani = Calendar.getInstance().getTimeInMillis();
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
        if(kodMu) return kod;
        return "_";
    }

    public static void parolaGirildi(String parola) {
        if(parola.equals(OrtakAlan.parola))
        {
            parolaVar = true;
            M3UVeri.mainActivity.switchAdultRL.setVisibility(View.VISIBLE);
        }
    }
}
