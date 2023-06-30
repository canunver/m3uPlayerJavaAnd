package org.unver.m3uplayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProgSettings {
    public static boolean son_tv_kanalini_oynatarak_basla;
    public static String tmdb_erisim_anahtar;
    public static String m3u_internet_adresi_3;
    public static String m3u_internet_adresi_2;
    public static String m3u_internet_adresi_1;
    public static M3UBilgi.M3UTur sonM3UTur;
    public static String sonGrup;
    public static String sonProgID;
    public static String sonTVGrup;
    public static String sonTVProgID;
    public static long sonCekilmeZamani;

    public static int ConvertToInt32(String strDeger, int varsayilanDeger) {
        if (!StringIsNUllOrEmpty(strDeger)) {
            try {
                return Integer.parseInt(strDeger);
            } catch (Exception _) {
            }
        }
        return varsayilanDeger;
    }

    public static long ConvertToLong(String strDeger, long varsayilanDeger) {
        if (!StringIsNUllOrEmpty(strDeger)) {
            try {
                return Long.parseLong(strDeger);
            } catch (Exception _) {
            }
        }
        return varsayilanDeger;
    }

    public static String TarihYAGOl(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static void AyarlariOku() {
        m3u_internet_adresi_1 = M3UVeri.AyarOku("m3u_internet_adresi_1");
        m3u_internet_adresi_2 = M3UVeri.AyarOku("m3u_internet_adresi_2");
        m3u_internet_adresi_3 = M3UVeri.AyarOku("m3u_internet_adresi_3");
        tmdb_erisim_anahtar = M3UVeri.AyarOku("tmdb_erisim_anahtar");
        son_tv_kanalini_oynatarak_basla = ConvertToInt32(M3UVeri.AyarOku("son_tv_kanalini_oynatarak_basla"), 0) == 1;

        sonTVGrup = M3UVeri.AyarOku("sonTVGrup");
        sonTVProgID = M3UVeri.AyarOku("sonTVProgID");
        sonM3UTur = M3UVeri.TurBul(ConvertToInt32(M3UVeri.AyarOku("sonM3UTur"), 0));
        sonGrup = M3UVeri.AyarOku("sonGrup");
        sonProgID = M3UVeri.AyarOku("sonProgID");
        sonCekilmeZamani = ConvertToLong(M3UVeri.AyarOku("sonCekilmeZamani"), 0);
    }

    public static void AyarlariYaz() {
        M3UVeri.AyarYaz("m3u_internet_adresi_1", m3u_internet_adresi_1);
        M3UVeri.AyarYaz("m3u_internet_adresi_2", m3u_internet_adresi_2);
        M3UVeri.AyarYaz("m3u_internet_adresi_3", m3u_internet_adresi_3);
        M3UVeri.AyarYaz("tmdb_erisim_anahtar", tmdb_erisim_anahtar);
        M3UVeri.AyarYaz("son_tv_kanalini_oynatarak_basla", son_tv_kanalini_oynatarak_basla ? "1" : "0");
    }

    public static void TarihceyeEkle(M3UBilgi.M3UTur aktifTur, String aktifGrupAd, String id) {
        if (!StringIsNUllOrEmpty(aktifGrupAd) && !StringIsNUllOrEmpty(id)) {
            if (aktifTur != sonM3UTur || !aktifGrupAd.equals(sonGrup) || !id.equals(sonProgID)) {
                sonM3UTur = aktifTur;
                sonGrup = aktifGrupAd;
                sonProgID = id;
                if (sonM3UTur == M3UBilgi.M3UTur.tv) {
                    sonTVGrup = aktifGrupAd;
                    sonTVProgID = id;
                    M3UVeri.AyarYaz("sonTVGrup", sonTVGrup);
                    M3UVeri.AyarYaz("sonTVProgID", sonTVProgID);
                }
                M3UVeri.AyarYaz("sonM3UTur", Integer.toString(M3UVeri.SiraBul(sonM3UTur)));
                M3UVeri.AyarYaz("sonGrup", sonGrup);
                M3UVeri.AyarYaz("sonProgID", sonProgID);
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
            if (retVal != "") retVal += ";";
            retVal += s;
        }
        return retVal;
    }

    public static String ConvertToStr(int[] intDizi) {
        if (intDizi == null || intDizi.length == 0)
            return null;
        String retVal = "";
        for (int s : intDizi) {
            if (retVal != "") retVal += ";";
            retVal += s;
        }
        return retVal;
    }

    public static String[] ConvertToArrayStr(String str) {
        return str.split(";");
    }

    public static int[] ConvertToArrayInt(String str) {
        String[] strs = str.split(";");
        int[] ints = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            ints[i] = ProgSettings.ConvertToInt32(strs[i], 0);
        }
        return ints;
    }
}
