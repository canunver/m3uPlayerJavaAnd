package org.unver.m3uplayer;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class TVInfo {
    public int type;
    public long id;
    public String name;
    public String title;
    public String original_name;
    public String original_title;
    public String poster_path;
    public boolean adult = false;
    public double popularity;
    public String backdrop_path;
    public double vote_average;
    public String overview;
    public String first_air_date;
    public String release_date;
    public String original_language;
    public int vote_count;
    public String[] origin_country;
    public int[] genre_ids;

    public String YayinTarihiBul() {
        if (ProgSettings.StringIsNUllOrEmpty(first_air_date))
            return release_date;
        else
            return first_air_date;
    }

    public int FilmYil() {
        String yt = YayinTarihiBul();
        if (!ProgSettings.StringIsNUllOrEmpty(yt)) {
            ProgSettings.ConvertToInt32(yt.substring(yt.length() - 4), 0);
        }
        return 0;
    }

    public TVInfo() {

    }

    public TVInfo(String typeId, String name,
                  String title, String original_name, String original_title,
                  String poster_path, int adult, double popularity, String backdrop_path,
                  double vote_average, String overview, String first_air_date, String release_date, String original_language,
                  int vote_count, String origin_country, String genre_ids) {

        String[] typeIds = typeId.split("_");
        this.type = ProgSettings.ConvertToInt32(typeIds[0], 0);
        this.id = ProgSettings.ConvertToLong(typeIds[1], 0);
        this.name = name;
        this.title = title;
        this.original_name = original_name;
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.adult = adult == 1;
        this.popularity = popularity;
        this.backdrop_path = backdrop_path;
        this.vote_average = vote_average;
        this.overview = overview;
        this.first_air_date = first_air_date;
        this.release_date = release_date;
        this.original_language = original_language;
        this.vote_count = vote_count;
        this.origin_country = ProgSettings.ConvertToArrayStr(origin_country);
        this.genre_ids = ProgSettings.ConvertToArrayInt(genre_ids);
    }

    public String Name() {
        if (ProgSettings.StringIsNUllOrEmpty(name))
            return title;
        else
            return name;
    }

    public String Original_name() {
        if (ProgSettings.StringIsNUllOrEmpty(original_name))
            return original_title;
        else
            return original_name;
    }

    public String ToListStr() {
        return String.format("%s (%s) - %s, %s - %s", Name(), YayinTarihiBul(), Original_name(), popularity, vote_average);
    }

    public long Yaz(SQLiteDatabase db) {
        long rowId;

        try {
            ContentValues values = new ContentValues();

            values.put("type_id", AnahtarBul());                                       //public int
            values.put("name", name);                                                  //public String
            values.put("title", title);                                                //public String
            values.put("original_name", original_name);                                //public String
            values.put("original_title", original_title);                              //public String

            values.put("poster_path", poster_path);                                    //public String
            values.put("adult", false);                                                //public boolean
            values.put("popularity", popularity);                                      //public double
            values.put("backdrop_path", backdrop_path);                                //public String
            values.put("vote_average", vote_average);                                  //public double
            values.put("overview", overview);                                          //public String
            values.put("first_air_date", first_air_date);                              //public String
            values.put("release_date", release_date);                                  //public String
            values.put("original_language", original_language);                        //public String
            values.put("vote_count", vote_count);                                      //public int
            values.put("origin_country", ProgSettings.ConvertToStr(origin_country));   //public String[]
            values.put("genre_ids", ProgSettings.ConvertToStr(genre_ids));             //public int[]

            rowId = db.insertWithOnConflict(M3U_DB.TABLE_TVINFO, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception ex) {
            rowId = -1;
        }
        return rowId;
    }

    public String AnahtarBul() {
        return AnahtarBul(this.type, this.id);
    }

    public static String AnahtarBul(int type, long id) {
        return type + "_" + id;
    }

}
