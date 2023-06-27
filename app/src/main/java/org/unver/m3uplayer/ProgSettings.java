package org.unver.m3uplayer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProgSettings {
    public static boolean son_tv_kanalini_oynatarak_basla;
    public static String tmdb_erisim_anahtar;
    public static String m3u_internet_adresi_3;
    public static String m3u_internet_adresi_2;
    public static String m3u_internet_adresi_1;
    private static M3UBilgi.M3UTur sonM3UTur;
    private static String sonGrup;
    private static String sonProgID;
    private static String sonTVGrup;
    private static String sonTVProgID;

    public static int ConvertToInt32(String strDeger, int varsayilanDeger) {
        if (strDeger != null && !strDeger.equals("")) {
            try {
                return Integer.parseInt(strDeger);
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

}
