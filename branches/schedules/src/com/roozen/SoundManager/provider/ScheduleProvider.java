/**
 * Copyright 2009 Mike Partridge
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed 
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */
package com.roozen.SoundManager.provider;

import java.util.HashMap;

import com.roozen.SoundManager.utils.SQLiteDatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Abstracts access to schedule data in SQLite db
 * 
 * @author Mike Partridge
 */
public class ScheduleProvider extends ContentProvider {

	//expose a URI for our data
	public static final String AUTHORITY = "com.roozen.soundmanager.provider.scheduleprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + SQLiteDatabaseHelper.SCHEDULE_TABLE);

    //Content Provider requisites
    private static final UriMatcher sUriMatcher;
    private static HashMap<String, String> sGoalProjectionMap;
    
    private SQLiteDatabaseHelper mDbHelper = null;
    
    private static final int NOFILTER = 0;
    private static final int SCHEDULE_ID = 1;
    private static final int SCHEDULE_TYPE = 2;
    private static final int ALL_ACTIVE = 3;

    public static final String MIME_SYSTEM = "system";
    public static final String MIME_RINGER = "ringer";
    public static final String MIME_NOTIF = "notif";
    public static final String MIME_MEDIA = "media";
    public static final String MIME_ALARM = "alarm";
    public static final String MIME_INCALL = "incall";
    
	/*
	 * initialize sUriMatcher and sGoalProjectionMap
	 */
    static {
        /*
         * defines how to identify what is being requested
         */
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ScheduleProvider.AUTHORITY, SQLiteDatabaseHelper.SCHEDULE_TABLE, NOFILTER);
        sUriMatcher.addURI(ScheduleProvider.AUTHORITY, SQLiteDatabaseHelper.SCHEDULE_TABLE + "/#", SCHEDULE_ID);
        sUriMatcher.addURI(ScheduleProvider.AUTHORITY, SQLiteDatabaseHelper.SCHEDULE_TABLE + "/type/*", SCHEDULE_TYPE);
        sUriMatcher.addURI(ScheduleProvider.AUTHORITY, SQLiteDatabaseHelper.SCHEDULE_TABLE + "/active", ALL_ACTIVE);
        
        /*
         * defines the columns returned for any query
         */
        sGoalProjectionMap = new HashMap<String,String>();
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_ID, SQLiteDatabaseHelper.SCHEDULE_ID);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_TYPE, SQLiteDatabaseHelper.SCHEDULE_TYPE);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_START_HOUR, SQLiteDatabaseHelper.SCHEDULE_START_HOUR);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_START_MINUTE, SQLiteDatabaseHelper.SCHEDULE_START_MINUTE);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_VOLUME, SQLiteDatabaseHelper.SCHEDULE_VOLUME);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_VIBRATE, SQLiteDatabaseHelper.SCHEDULE_VIBRATE);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_ACTIVE, SQLiteDatabaseHelper.SCHEDULE_ACTIVE);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_DAY0, SQLiteDatabaseHelper.SCHEDULE_DAY0);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_DAY1, SQLiteDatabaseHelper.SCHEDULE_DAY1);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_DAY2, SQLiteDatabaseHelper.SCHEDULE_DAY2);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_DAY3, SQLiteDatabaseHelper.SCHEDULE_DAY3);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_DAY4, SQLiteDatabaseHelper.SCHEDULE_DAY4);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_DAY5, SQLiteDatabaseHelper.SCHEDULE_DAY5);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.SCHEDULE_DAY6, SQLiteDatabaseHelper.SCHEDULE_DAY6);
        
    }
    
    /**
     * Maps mime type strings in URIs to standard volume type ints
     * 
     * @param mimeType
     * @return int volume type
     */
    private static int getVolumeType(String mimeType) {
        int volumeType = AudioManager.STREAM_SYSTEM;
        
        if (mimeType.equals(MIME_SYSTEM)) {
            volumeType = AudioManager.STREAM_SYSTEM;
        }
        else if (mimeType.equals(MIME_RINGER)) {
            volumeType = AudioManager.STREAM_RING;
        }
        else if (mimeType.equals(MIME_NOTIF)) {
            volumeType = AudioManager.STREAM_NOTIFICATION;
        }
        else if (mimeType.equals(MIME_MEDIA)) {
            volumeType = AudioManager.STREAM_MUSIC;
        }
        else if (mimeType.equals(MIME_ALARM)) {
            volumeType = AudioManager.STREAM_ALARM;
        }
        else if (mimeType.equals(MIME_INCALL)) {
            volumeType = AudioManager.STREAM_VOICE_CALL;
        }
        
        return volumeType;
    }

    /**
     * Maps standard volume type ints to mime type string for URIs
     * 
     * @param volumeType
     * @return String mime type
     */
    public static String getMimeType(int volumeType) {
        String mimeType = "system";
        
        switch (volumeType) {
            case AudioManager.STREAM_SYSTEM:
                mimeType = MIME_SYSTEM;
                break;
                
            case AudioManager.STREAM_RING:
                mimeType = MIME_RINGER;
                break;
                
            case AudioManager.STREAM_NOTIFICATION:
                mimeType = MIME_NOTIF;
                break;
                
            case AudioManager.STREAM_MUSIC:
                mimeType = MIME_MEDIA;
                break;
                
            case AudioManager.STREAM_ALARM:
                mimeType = MIME_ALARM;
                break;

            case AudioManager.STREAM_VOICE_CALL:
                mimeType = MIME_INCALL;
                break;

            default:
                break;
        }
        
        return mimeType;
    }
    
    /* (non-Javadoc)
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new SQLiteDatabaseHelper(getContext());
        return true;
    }

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SCHEDULE_ID:
                return "vnd.android.cursor.item/" + AUTHORITY;
            case SCHEDULE_TYPE:
                return "vnd.android.cursor.dir/" + AUTHORITY + ".type";
            case ALL_ACTIVE:
                return "vnd.android.cursor.dir/" + AUTHORITY + ".active";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
        //Only the base NOFILTER URI is allowed for inserts
        if (sUriMatcher.match(uri) != NOFILTER) {
            throw new IllegalArgumentException("Invalid URI " + uri);
        }

        /*
         * _TYPE is required on insert
         */
        ContentValues values;
        if (initialValues != null) {
            if (!initialValues.containsKey(SQLiteDatabaseHelper.SCHEDULE_TYPE)) {
                throw new IllegalArgumentException("Type value is required on insert.");
            }
            
            values = new ContentValues(initialValues);
        } 
        else {
            throw new IllegalArgumentException("Type value is required on insert.");
        }

        /*
         * do the insert
         */
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(SQLiteDatabaseHelper.SCHEDULE_TABLE, null, values);
        if (rowId > 0) {            
            Uri noteUri = ContentUris.withAppendedId(ScheduleProvider.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
	    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        
	    String orderBy;
        // If no sort order is specified use the default
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = SQLiteDatabaseHelper.SCHEDULE_DEFAULT_ORDER;
        } else {
            orderBy = sortOrder;
        }

	    /*
	     * act on supported query URIs 
	     */
	    switch (sUriMatcher.match(uri)) {

	        case SCHEDULE_TYPE:
	            qb.setTables(SQLiteDatabaseHelper.SCHEDULE_TABLE);
	            qb.setProjectionMap(sGoalProjectionMap);
	            {
	                String mimeType = uri.getPathSegments().get(2);
	                qb.appendWhere(SQLiteDatabaseHelper.SCHEDULE_TYPE + "=" + getVolumeType(mimeType));
	            }
	            break;

	        case SCHEDULE_ID:
	            qb.setTables(SQLiteDatabaseHelper.SCHEDULE_TABLE);
	            qb.setProjectionMap(sGoalProjectionMap);
	            {
	                String id = uri.getPathSegments().get(1);
	                qb.appendWhere(SQLiteDatabaseHelper.SCHEDULE_ID + "=" + id);
	            }
	            break;
	            
	        case ALL_ACTIVE:
                qb.setTables(SQLiteDatabaseHelper.SCHEDULE_TABLE);
                qb.setProjectionMap(sGoalProjectionMap);
                qb.appendWhere(SQLiteDatabaseHelper.SCHEDULE_ACTIVE + "=1");
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = SQLiteDatabaseHelper.SCHEDULE_TYPE;
                }
                break;

	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
	    }

	    // Get the database and run the query
	    SQLiteDatabase db = mDbHelper.getReadableDatabase();
	    Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

	    // Tell the cursor what uri to watch, so it knows when its source data changes
	    c.setNotificationUri(getContext().getContentResolver(), uri);
	    return c;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        
        /*
         * allow update by ID only
         */
        switch (sUriMatcher.match(uri)) {

        case SCHEDULE_ID:
            String id = uri.getPathSegments().get(1);
            String whereClause = SQLiteDatabaseHelper.SCHEDULE_ID + "=?";
            String[] whereArgs = {id};
            
            count = db.update(SQLiteDatabaseHelper.SCHEDULE_TABLE, values, whereClause, whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Invalid URI " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        
        return count;
	}
    
    /* (non-Javadoc)
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        
        /*
         * delete by ID only
         */
        switch (sUriMatcher.match(uri)) {
            
        case SCHEDULE_ID:
            String id = uri.getPathSegments().get(1);
            String whereClause = SQLiteDatabaseHelper.SCHEDULE_ID + "=" + id;
            
            count = db.delete(SQLiteDatabaseHelper.SCHEDULE_TABLE, whereClause, null);
            break;
            
        default:
            throw new IllegalArgumentException("Invalid URI " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        
        return count;
    }
}
