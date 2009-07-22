package com.roozen.SoundManager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.AudioManager;
import android.util.Log;

import com.roozen.SoundManager.R;

/**
 * @author Mike Partridge
 *
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 3;
    private Context mContext;

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
    public static final String SCHEDULE_START_HOUR = "_start_hour";
    public static final String SCHEDULE_START_MINUTE = "_start_min";
    public static final String SCHEDULE_VOLUME = "_volume";
    public static final String SCHEDULE_VIBRATE = "_vibrate";
    public static final String SCHEDULE_ACTIVE = "_active_fg";
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
            + SCHEDULE_START_HOUR + " integer not null default 0, "
            + SCHEDULE_START_MINUTE + " integer not null default 0, "
            + SCHEDULE_VOLUME + " integer not null default 0, "
            + SCHEDULE_VIBRATE + " integer not null default 0, "
            + SCHEDULE_ACTIVE + " integer not null default 1, "
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
        + SCHEDULE_START_HOUR + ","
        + SCHEDULE_START_MINUTE + ","
        + SCHEDULE_ID;
    
    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
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
        
        if (oldVersion < 3) {
            upgradeTo3(db);
        }

        Log.w(SQLiteDatabaseHelper.class.toString(), "Upgrade completed.");
    }

    private void upgradeTo3(SQLiteDatabase db) {
        
        db.execSQL(SCHEDULE_TABLE_CREATE);
        
        //copy the system timer
        if (getBooleanPref(db, mContext.getString(R.string.EnableSystem))) {
            String time = getStringPref(db, mContext.getString(R.string.SystemTimeStart));
            int volume = getIntPref(db, mContext.getString(R.string.SystemStartVolume));
            insertSchedule(db, time, volume, AudioManager.STREAM_SYSTEM);
            
            time = getStringPref(db, mContext.getString(R.string.SystemTimeEnd));
            volume = getIntPref(db, mContext.getString(R.string.SystemEndVolume));
            insertSchedule(db, time, volume, AudioManager.STREAM_SYSTEM);
        }
        
        //copy the ringer timer
        if (getBooleanPref(db, mContext.getString(R.string.EnableRinger))) {
            String time = getStringPref(db, mContext.getString(R.string.RingerTimeStart));
            int volume = getIntPref(db, mContext.getString(R.string.RingerStartVolume));
            insertSchedule(db, time, volume, AudioManager.STREAM_RING);
            
            time = getStringPref(db, mContext.getString(R.string.RingerTimeEnd));
            volume = getIntPref(db, mContext.getString(R.string.RingerEndVolume));
            insertSchedule(db, time, volume, AudioManager.STREAM_RING);
        }
        
        //copy the media timer
        if (getBooleanPref(db, mContext.getString(R.string.EnableMedia))) {
            String time = getStringPref(db, mContext.getString(R.string.MediaTimeStart));
            int volume = getIntPref(db, mContext.getString(R.string.MediaStartVolume));
            insertSchedule(db, time, volume, AudioManager.STREAM_MUSIC);
            
            time = getStringPref(db, mContext.getString(R.string.MediaTimeEnd));
            volume = getIntPref(db, mContext.getString(R.string.MediaEndVolume));
            insertSchedule(db, time, volume, AudioManager.STREAM_MUSIC);
        }
        
        //copy the alarm timer
        if (getBooleanPref(db, mContext.getString(R.string.EnableAlarm))) {
            String time = getStringPref(db, mContext.getString(R.string.AlarmTimeStart));
            int volume = getIntPref(db, mContext.getString(R.string.AlarmStartVolume));
            insertSchedule(db, time, volume, AudioManager.STREAM_ALARM);
            
            time = getStringPref(db, mContext.getString(R.string.AlarmTimeEnd));
            volume = getIntPref(db, mContext.getString(R.string.AlarmEndVolume));
            insertSchedule(db, time, volume, AudioManager.STREAM_ALARM);
        }
        
        //copy the in-call timer
        if (getBooleanPref(db, mContext.getString(R.string.EnableIncall))) {
            String time = getStringPref(db, mContext.getString(R.string.IncallTimeStart));
            int volume = getIntPref(db, mContext.getString(R.string.IncallStartVolume));
            insertSchedule(db, time, volume, AudioManager.STREAM_VOICE_CALL);
            
            time = getStringPref(db, mContext.getString(R.string.IncallTimeEnd));
            volume = getIntPref(db, mContext.getString(R.string.IncallEndVolume));
            insertSchedule(db, time, volume, AudioManager.STREAM_VOICE_CALL);
        }
        
        //copy the mute pref to shared prefs
        boolean muted = getBooleanPref(db, mContext.getString(R.string.muted));
        if (muted) {
            SharedPreferences prefs = mContext.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(mContext.getString(R.string.muted), muted);
        }
        
        //TODO: cancel existing alarms
        
        db.execSQL("drop table "+PREFERENCE_TABLE);
    }
    
    private String getStringPref(SQLiteDatabase db, String pref) {
        String value = "";
        
        Cursor c = db.query(PREFERENCE_TABLE, new String[]{PREFERENCES_STRING_DATA}, 
                            PREFERENCES_PREFERENCE+"=\""+pref+"\"", null, 
                            null, null, null);
        
        if (c != null && c.moveToFirst()) {
            value = c.getString(c.getColumnIndex(PREFERENCES_STRING_DATA));
        }
        
        return value;
    }
    
    private int getIntPref(SQLiteDatabase db, String pref) {
        int value = 0;
        
        Cursor c = db.query(PREFERENCE_TABLE, new String[]{PREFERENCES_INTEGER_DATA}, 
                            PREFERENCES_PREFERENCE+"=\""+pref+"\"", null, 
                            null, null, null);
        
        if (c != null && c.moveToFirst()) {
            value = c.getInt(c.getColumnIndex(PREFERENCES_INTEGER_DATA));
        }
        
        return value;
    }
    
    private boolean getBooleanPref(SQLiteDatabase db, String pref) {
        boolean value = false;
        
        Cursor c = db.query(PREFERENCE_TABLE, new String[]{PREFERENCES_INTEGER_DATA}, 
                            PREFERENCES_PREFERENCE+"=\""+pref+"\"", null, 
                            null, null, null);
        
        if (c != null && c.moveToFirst()) {
            value = (c.getInt(c.getColumnIndex(PREFERENCES_INTEGER_DATA)) > 0);
        }
        
        return value;
    }
    
    private void insertSchedule(SQLiteDatabase db, String timeStart, int volume, int stream) {
        
        ContentValues values = new ContentValues();

        values.put(SCHEDULE_DAY0, "1");
        values.put(SCHEDULE_DAY1, "1");
        values.put(SCHEDULE_DAY2, "1");
        values.put(SCHEDULE_DAY3, "1");
        values.put(SCHEDULE_DAY4, "1");
        values.put(SCHEDULE_DAY5, "1");
        values.put(SCHEDULE_DAY6, "1");
        values.put(SCHEDULE_START_HOUR, Integer.parseInt(timeStart.substring(0, timeStart.indexOf(":"))));
        values.put(SCHEDULE_START_MINUTE, Integer.parseInt(timeStart.substring(timeStart.indexOf(":") + 1)));
        values.put(SCHEDULE_VOLUME, volume);
        values.put(SCHEDULE_VIBRATE, "0");
        values.put(SCHEDULE_ACTIVE, "1");
        values.put(SCHEDULE_TYPE, stream);
        
        db.insert(SCHEDULE_TABLE, null, values);
    }
    
}
