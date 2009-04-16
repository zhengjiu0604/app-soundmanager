/**
 * Copyright 2009 Daniel Roozen
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

/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * This file was copied from the original NotesDbAdapter class that came with
 * a Notes Android tutorial. This file has been modified to suit the purposes
 * of PiggyBank Droid.
 */

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Simple hours database access helper class. Defines the basic CRUD operations
 * for the HourActivity, and gives the ability to list all hours as well as
 * retrieve or modify a specific hour record.
 */
public class PreferenceProvider extends ContentProvider {
    private static final String TAG = "PreferencesDbAdapter";
    
    public static final String AUTHORITY = "com.roozen.SoundManager.provider.PreferenceProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PreferenceProvider.PREFERENCE_TABLE);
    
	// Database Info
	private static final String DATABASE_NAME = "data";
    public static final String PREFERENCE_TABLE = "preferences";
    
    private static final int DATABASE_VERSION = 2;
    
    // Class Info
    private DatabaseHelper mDbHelper = null;
    
    // Content Provider Info
    private static final UriMatcher sUriMatcher;
    private static HashMap<String, String> sGoalProjectionMap;
    
    private static final int PREFERENCE = 1;
    private static final int PREFERENCE_SPECIFIC = 2;
    
    public static final String _ID = "_id";
    public static final String _PREFERENCE = "_preference";
    public static final String _STRING_DATA = "_string_data";
    public static final String _INTEGER_DATA = "_integer_data";

    // Database sql statements
    private static final String PREFERENCES_CREATE = 
    	             "create table " + PREFERENCE_TABLE + " (" + _ID + " integer primary key autoincrement, " +
				     _PREFERENCE + " text not null, " + _STRING_DATA + " text, " +
				     _INTEGER_DATA + " integer); ";

    
    
    public static final String DEFAULT_SORT_ORDER = _PREFERENCE + ", " + _ID;

    public static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(PREFERENCES_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ".");
        }
    }

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case PREFERENCE_SPECIFIC:
            String preference = uri.getPathSegments().get(1);
            count = db.delete(PREFERENCE_TABLE, _PREFERENCE + "='" + preference + "'"
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {            
        case PREFERENCE:
            return "vnd.android.cursor.dir/com.roozen.SoundManager.preference";
        case PREFERENCE_SPECIFIC:
        	return "vnd.android.cursor.item/com.roozen.SoundManager.preference";

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != PREFERENCE) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(PREFERENCE_TABLE, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(PreferenceProvider.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
	    mDbHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
        case PREFERENCE:
        	qb.setTables(PREFERENCE_TABLE);
        	qb.setProjectionMap(sGoalProjectionMap);
        	selection = null;
        	break;
        	
        case PREFERENCE_SPECIFIC:
            qb.setTables(PREFERENCE_TABLE);
            qb.setProjectionMap(sGoalProjectionMap);
            String value = uri.getPathSegments().get(1);
            qb.appendWhere(_PREFERENCE + "='" + value + "'");
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case PREFERENCE:
            count = db.update(PREFERENCE_TABLE, values, where, whereArgs);
            break;

        case PREFERENCE_SPECIFIC:
            String preference = uri.getPathSegments().get(1);
            count = db.update(PREFERENCE_TABLE, values, _PREFERENCE + "='" + preference + "'"
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
	
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(PreferenceProvider.AUTHORITY, PREFERENCE_TABLE, PREFERENCE);
        sUriMatcher.addURI(PreferenceProvider.AUTHORITY, PREFERENCE_TABLE + "/*", PREFERENCE_SPECIFIC);
        
        sGoalProjectionMap = new HashMap<String, String>();
        sGoalProjectionMap.put(_ID, _ID);
        sGoalProjectionMap.put(_PREFERENCE, _PREFERENCE);
        sGoalProjectionMap.put(_STRING_DATA, _STRING_DATA);
        sGoalProjectionMap.put(_INTEGER_DATA, _INTEGER_DATA);
    }
}
