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

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.roozen.SoundManager.utils.SQLiteDatabaseHelper;

/**
 * Simple hours database access helper class. Defines the basic CRUD operations
 * for the HourActivity, and gives the ability to list all hours as well as
 * retrieve or modify a specific hour record.
 */
public class PreferenceProvider extends ContentProvider {
    
    public static final String AUTHORITY = "com.roozen.SoundManager.provider.PreferenceProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + SQLiteDatabaseHelper.PREFERENCE_TABLE);
    
    // Class Info
    private SQLiteDatabaseHelper mDbHelper = null;
    
    // Content Provider Info
    private static final UriMatcher sUriMatcher;
    private static HashMap<String, String> sGoalProjectionMap;
    
    private static final int PREFERENCE = 1;
    private static final int PREFERENCE_SPECIFIC = 2;

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case PREFERENCE_SPECIFIC:
            String preference = uri.getPathSegments().get(1);
            count = db.delete(SQLiteDatabaseHelper.PREFERENCE_TABLE, 
                              SQLiteDatabaseHelper.PREFERENCES_PREFERENCE + "='" + preference + "'"
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
        long rowId = db.insert(SQLiteDatabaseHelper.PREFERENCE_TABLE, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(PreferenceProvider.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
	    mDbHelper = new SQLiteDatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
        case PREFERENCE:
        	qb.setTables(SQLiteDatabaseHelper.PREFERENCE_TABLE);
        	qb.setProjectionMap(sGoalProjectionMap);
        	selection = null;
        	break;
        	
        case PREFERENCE_SPECIFIC:
            qb.setTables(SQLiteDatabaseHelper.PREFERENCE_TABLE);
            qb.setProjectionMap(sGoalProjectionMap);
            String value = uri.getPathSegments().get(1);
            qb.appendWhere(SQLiteDatabaseHelper.PREFERENCES_PREFERENCE + "='" + value + "'");
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = SQLiteDatabaseHelper.PREFERENCES_SORT_ORDER;
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
            count = db.update(SQLiteDatabaseHelper.PREFERENCE_TABLE, values, where, whereArgs);
            break;

        case PREFERENCE_SPECIFIC:
            String preference = uri.getPathSegments().get(1);
            count = db.update(SQLiteDatabaseHelper.PREFERENCE_TABLE, values, SQLiteDatabaseHelper.PREFERENCES_PREFERENCE + "='" + preference + "'"
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
        sUriMatcher.addURI(PreferenceProvider.AUTHORITY, SQLiteDatabaseHelper.PREFERENCE_TABLE, PREFERENCE);
        sUriMatcher.addURI(PreferenceProvider.AUTHORITY, SQLiteDatabaseHelper.PREFERENCE_TABLE + "/*", PREFERENCE_SPECIFIC);
        
        sGoalProjectionMap = new HashMap<String, String>();
        sGoalProjectionMap.put(SQLiteDatabaseHelper.PREFERENCES_ID, SQLiteDatabaseHelper.PREFERENCES_ID);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.PREFERENCES_PREFERENCE, SQLiteDatabaseHelper.PREFERENCES_PREFERENCE);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.PREFERENCES_STRING_DATA, SQLiteDatabaseHelper.PREFERENCES_STRING_DATA);
        sGoalProjectionMap.put(SQLiteDatabaseHelper.PREFERENCES_INTEGER_DATA, SQLiteDatabaseHelper.PREFERENCES_INTEGER_DATA);
    }
}
