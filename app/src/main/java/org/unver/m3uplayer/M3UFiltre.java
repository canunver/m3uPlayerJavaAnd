package org.unver.m3uplayer;

import java.util.Calendar;
import java.util.Date;

public class M3UFiltre {

        public M3UBilgi.M3UTur tur;
        public String[] grupAd;
        public String filtre;
        public boolean kulAra = true;
        public boolean dosAra = true;
        public String[] filmTurler = null;
        public int numMinPuan = -1;
        public int numMaxPuan = -1;
        public int numMinYil = -1;
        public int numMaxYil = -1;
        public boolean sadeceYeni = false;
        public String tarihStr = "";
        public boolean detayli = false;
        public M3UFiltre(M3UBilgi.M3UTur tur, String grupAd, String filtre, boolean detayli)
        {
            this.detayli = detayli;
            this.tur = tur;
            if (!M3UListeArac.IsNullOrWhiteSpace(grupAd))
                this.grupAd = grupAd.split(";");
            else {
                this.grupAd = new String[1];// { "" };
                this.grupAd[0] = "";
            }
            this.filtre = filtre;
        }

    public M3UFiltre(M3UBilgi.M3UTur tur, String grupAd, String filtre, boolean pkulAra, boolean pdosAra, String pfilmTurler, String pnumMinPuan, String pnumMaxPuan, String pnumMinYil, String pnumMaxYil, boolean sadeceYeni, boolean detayli)
    {
        this(tur, grupAd, filtre, detayli);
        this.kulAra = pkulAra;
        this.dosAra = pdosAra;
        if (!M3UListeArac.IsNullOrWhiteSpace(pfilmTurler))
            this.filmTurler = pfilmTurler.split(";");
        else
            this.filmTurler = null;
        this.numMinPuan = ProgSettings.ConvertToInt32(pnumMinPuan, -1);
        this.numMaxPuan = ProgSettings.ConvertToInt32(pnumMaxPuan, -1);
        this.numMinYil = ProgSettings.ConvertToInt32(pnumMinYil, -1);
        this.numMaxYil = ProgSettings.ConvertToInt32(pnumMaxYil, -1);
        this.sadeceYeni = sadeceYeni;
        Calendar onbesGunOnce = Calendar.getInstance();
        onbesGunOnce.add(Calendar.DAY_OF_MONTH, -15);
        this.tarihStr = ProgSettings.TarihYAGOl(onbesGunOnce.getTime());
    }
}