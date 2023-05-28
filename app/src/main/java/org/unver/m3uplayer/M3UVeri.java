package org.unver.m3uplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class M3UVeri extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "M3UVeri";
    private static final int DATABASE_VERSION = 1;

    // Define the table names
    public static final String TABLE_M3U = "M3U";
//    private static final String TABLE2_NAME = "table2";
//    private static final String TABLE3_NAME = "table3";
//    private static final String TABLE4_NAME = "table4";

    public M3UVeri(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_M3U + " ("
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

    }
}
