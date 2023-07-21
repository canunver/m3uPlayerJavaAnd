package org.unver.m3uplayer;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

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

    @NonNull
    @Override
    public String toString() {
        return nameTitle() + "(" + yayinTarihiBul() + ")";
    }

    public String yayinTarihiBul() {
        if (OrtakAlan.StringIsNUllOrEmpty(first_air_date))
            return release_date;
        else
            return first_air_date;
    }

    public int FilmYil() {
        String yt = yayinTarihiBul();
        if (!OrtakAlan.StringIsNUllOrEmpty(yt)) {
            return OrtakAlan.ConvertToInt32(yt.substring(0, 4), 0);
        }
        return 0;
    }

    @SuppressWarnings("all")
    public TVInfo() {

    }

    public TVInfo(String typeId, String name,
                  String title, String original_name, String original_title,
                  String poster_path, int adult, double popularity, String backdrop_path,
                  double vote_average, String overview, String first_air_date, String release_date, String original_language,
                  int vote_count, String origin_country, String genre_ids) {

        String[] typeIds = typeId.split("_");
        this.type = OrtakAlan.ConvertToInt32(typeIds[0], 0);
        this.id = OrtakAlan.ConvertToLong(typeIds[1], 0);
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
        this.origin_country = OrtakAlan.ConvertToArrayStr(origin_country);
        this.genre_ids = OrtakAlan.ConvertToArrayInt(genre_ids);
    }

    public TVInfo(int type, long id, String name, String air_date, String overview, double vote_average) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.original_name = name;
        this.adult = false;
        this.popularity = 0;
        this.backdrop_path = null;
        this.vote_average = vote_average;
        this.overview = overview;
        this.first_air_date = air_date;
        this.release_date = air_date;
        this.origin_country = null;
        this.genre_ids = null;
    }


    public String nameTitle() {
        if (OrtakAlan.StringIsNUllOrEmpty(name))
            return title;
        else
            return name;
    }

    public String Original_name() {
        if (OrtakAlan.StringIsNUllOrEmpty(original_name))
            return original_title;
        else
            return original_name;
    }

    public String ToListStr() {
        return String.format("%s (%s) - %s, %s - %s", nameTitle(), yayinTarihiBul(), Original_name(), popularity, vote_average);
    }

    @SuppressWarnings("all")
    public long Yaz(SQLiteDatabase db) {
        long rowId;

        try {
            ContentValues values = new ContentValues();

            values.put("type_id", anahtarBul());                                       //public int
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
            values.put("origin_country", OrtakAlan.ConvertToStr(origin_country));   //public String[]
            values.put("genre_ids", OrtakAlan.ConvertToStr(genre_ids));             //public int[]

            rowId = db.insertWithOnConflict(M3U_DB.TABLE_TVINFO, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception ex) {
            rowId = -1;
        }
        return rowId;
    }

    public String anahtarBul() {
        return anahtarBul(this.type, this.id);
    }

    public static String anahtarBul(int type, long id) {
        return type + "_" + id;
    }

    public int voteAverage() {
        return (int) vote_average * 10;
    }
}
