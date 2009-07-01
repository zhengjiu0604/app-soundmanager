package com.roozen.SoundManager.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Mike Partridge
 *
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 2;

    /*
     * Preferences table
     */
    public static final String PREFERENCE_TABLE = "preferences";

    public static final String PREFERENCES_ID = "_id";
    public static final String PREFERENCES_PREFERENCE = "_preference";
    public static final String PREFERENCES_STRING_DATA = "_string_data";
    public static final String PREFERENCES_INTEGER_DATA = "_integer_data";
    
    private static final String PREFERENCES_CREATE = 
        "create table " + PREFERENCE_TABLE + " (" + PREFERENCES_ID + " integer primary key autoincrement, " +
        PREFERENCES_PREFERENCE + " text not null, " + PREFERENCES_STRING_DATA + " text, " +
        PREFERENCES_INTEGER_DATA + " integer); ";

    public static final String PREFERENCES_SORT_ORDER = PREFERENCES_PREFERENCE + ", " + PREFERENCES_ID;
    
    /*
     * Schedule table
     */
    public static final String SCHEDULE_TABLE = "schedules";
    
    public static final String SCHEDULE_ID = "_id";
    public static final String SCHEDULE_TYPE = "_type";
    public static final String SCHEDULE_START_TIME = "_start_time";
    public static final String SCHEDULE_END_TIME = "_end_time";
    public static final String SCHEDULE_VOLUME = "_volume";
    public static final String SCHEDULE_VIBRATE = "_vibrate";
    public static final String SCHEDULE_DAY0 = "_day0";
    public static final String SCHEDULE_DAY1 = "_day1";
    public static final String SCHEDULE_DAY2 = "_day2";
    public static final String SCHEDULE_DAY3 = "_day3";
    public static final String SCHEDULE_DAY4 = "_day4";
    public static final String SCHEDULE_DAY5 = "_day5";
    public static final String SCHEDULE_DAY6 = "_day6";
    
    private static final String SCHEDULE_TABLE_CREATE = 
        "create table "
            + SCHEDULE_TABLE + " (" 
            + SCHEDULE_ID + " integer primary key autoincrement, " 
            + SCHEDULE_TYPE + " integer not null default 0, "
            + SCHEDULE_START_TIME + " text not null default \"00:00\", "
            + SCHEDULE_END_TIME + " text not null default \"24:00\", "
            + SCHEDULE_VOLUME + " integer not null default 0, "
            + SCHEDULE_VIBRATE + " integer not null default 0, "
            + SCHEDULE_DAY0 + " integer not null default 0, "
            + SCHEDULE_DAY1 + " integer not null default 0, "
            + SCHEDULE_DAY2 + " integer not null default 0, "
            + SCHEDULE_DAY3 + " integer not null default 0, "
            + SCHEDULE_DAY4 + " integer not null default 0, "
            + SCHEDULE_DAY5 + " integer not null default 0, "
            + SCHEDULE_DAY6 + " integer not null default 0 "
            +");";
    
    public static final String SCHEDULE_DEFAULT_ORDER = 
        SCHEDULE_DAY0 + " desc, " 
        + SCHEDULE_DAY1 + " desc, "
        + SCHEDULE_DAY2 + " desc, "
        + SCHEDULE_DAY3 + " desc, "
        + SCHEDULE_DAY4 + " desc, "
        + SCHEDULE_DAY5 + " desc, "
        + SCHEDULE_DAY6 + " desc, "
        + SCHEDULE_ID;
    
    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PREFERENCES_CREATE);
        db.execSQL(SCHEDULE_TABLE_CREATE);
    }

    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteDatabaseHelper.class.toString(), 
                "Upgrading database from version " + oldVersion + " to "
                + newVersion + ".");
    }

}
