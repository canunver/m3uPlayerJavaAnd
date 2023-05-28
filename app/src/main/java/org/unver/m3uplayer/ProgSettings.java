package org.unver.m3uplayer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProgSettings {
    public static int ConvertToInt32(String strDeger, int varsayilanDeger) {
        if(strDeger!=null && !strDeger.equals("")) {
            try {
                return Integer.getInteger(strDeger);
            } catch (Exception _) {
            }
        }
        return varsayilanDeger;
    }

    public static String TarihYAGOl(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}
