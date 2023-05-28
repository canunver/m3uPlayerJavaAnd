package org.unver.m3uplayer;

public class M3UListeArac {
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
}
