package org.unver.m3uplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class M3U_DB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "M3UVeri.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_M3U = "M3U";
    public static final String TABLE_AYARLAR = "AYARLAR";
    public static final String TABLE_TVINFO = "TVINFO";

    public M3U_DB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_M3U + " ("
                + "ID TEXT PRIMARY KEY,"
                + "tvgId TEXT,"
                + "tvgName TEXT,"
                + "tvgLogo TEXT,"
                + "groupTitle TEXT,"
                + "urlAdres TEXT,"
                + "eklemeTarih TEXT,"
                + "gizli INTEGER,"
                + "adult INTEGER,"
                + "tmdbId INTEGER)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS  " + TABLE_TVINFO + " ("
            + "type_id TEXT PRIMARY KEY,"
            + "name TEXT,"
            + "title TEXT,"
            + "original_name TEXT,"
            + "original_title TEXT,"
            + "poster_path TEXT,"
            + "adult INTEGER,"
            + "popularity REAL,"
            + "backdrop_path TEXT,"
            + "vote_average REAL,"
            + "overview TEXT,"

            + "first_air_date TEXT,"
            + "release_date TEXT,"
            + "original_language TEXT,"
            + "vote_count TEXT,"
            + "origin_country TEXT,"
            + "genre_ids TEXT)");
        }

        if(oldVersion<3) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN guncellemeTarih TEXT  ", TABLE_M3U));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN seyredilenSure INTEGER", TABLE_M3U));
        }

        if(oldVersion<2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS  " + TABLE_AYARLAR + " ("
                    + "KOD TEXT PRIMARY KEY,"
                    + "DEGER TEXT)");
        }
    }
}