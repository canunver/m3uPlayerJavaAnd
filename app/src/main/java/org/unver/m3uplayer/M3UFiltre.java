package org.unver.m3uplayer;

import java.util.Calendar;
import java.util.List;

public class M3UFiltre {
    public M3UBilgi.M3UTur tur;
    public String[] grupAd;
    public String isimFiltreStr;
    public boolean sadeceYeni;
    public String yeniTarihBaslaStr = "";

    public int[] filmTurler = null;
    public int minPuan = -1;
    public int maxPuan = -1;
    public int minYil = -1;
    public int maxYil = -1;

    public M3UFiltre() {
        this.tur = M3UBilgi.M3UTur.tv;
        this.grupAd = new String[1];// { "" };
        this.grupAd[0] = "";
        this.isimFiltreStr = "";
        sadeceYeni = false;
    }

    public void SadeceYeniAyarla(boolean sadeceYeni, List<Float> values) {
        this.sadeceYeni = sadeceYeni;
        Calendar yeniTarihBasla = Calendar.getInstance();
        int gs = values.get(0).intValue();
        yeniTarihBasla.add(Calendar.DAY_OF_MONTH, -1 * gs);
        this.yeniTarihBaslaStr = ProgSettings.TarihYAGOl(yeniTarihBasla.getTime());
    }

    public void PuanAyarla(List<Float> values) {
        minPuan = values.get(0).intValue();
        maxPuan = values.get(1).intValue();
        if (minPuan == 0 && maxPuan == 100) {
            minPuan = -1;
            maxPuan = -1;
        }
    }

    public void YilAyarla(List<Float> values, int minYil, int maxYil) {
        this.minYil = values.get(0).intValue();
        this.maxYil = values.get(1).intValue();
        if (this.minYil == minYil && this.maxYil == maxYil) {
            this.minYil = -1;
            this.maxYil = -1;
        }
    }

    public void TurAyarla(String turlerS) {
        if (ProgSettings.StringIsNUllOrEmpty(turlerS)) {
            filmTurler = null;
        } else {
            String[] turler = turlerS.split(",");
            filmTurler = new int[turler.length];
            for (int i = 0; i < turler.length; i++)
                filmTurler[i] = FilmTurYonetim.TurKodBul(turler[i]);
        }
    }

//    public M3UFiltre(M3UBilgi.M3UTur tur, String grupAd, String filtre, boolean pkulAra, boolean pdosAra, String pfilmTurler, String pnumMinPuan, String pnumMaxPuan, String pnumMinYil, String pnumMaxYil, boolean sadeceYeni, boolean detayli, int yeniGun) {
//        this(tur, grupAd, filtre, detayli);
//        this.kulAra = pkulAra;
//        this.dosAra = pdosAra;
//        if (!M3UListeArac.IsNullOrWhiteSpace(pfilmTurler))
//            this.filmTurler = pfilmTurler.split(";");
//        else
//            this.filmTurler = null;
//        this.numMinPuan = ProgSettings.ConvertToInt32(pnumMinPuan, -1);
//        this.numMaxPuan = ProgSettings.ConvertToInt32(pnumMaxPuan, -1);
//        this.numMinYil = ProgSettings.ConvertToInt32(pnumMinYil, -1);
//        this.numMaxYil = ProgSettings.ConvertToInt32(pnumMaxYil, -1);
//        this.sadeceYeni = sadeceYeni;
//    }
}