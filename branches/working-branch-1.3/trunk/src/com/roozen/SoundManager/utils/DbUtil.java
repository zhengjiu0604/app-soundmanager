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
package com.roozen.SoundManager.utils;

import com.roozen.SoundManager.provider.PreferenceProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class DbUtil {

	public static void insert(ContentResolver resolver, String pref, String data){
		ContentValues initialValues = new ContentValues();
        initialValues.put(PreferenceProvider._PREFERENCE, pref);
        initialValues.put(PreferenceProvider._STRING_DATA, data);
        
        resolver.insert(PreferenceProvider.CONTENT_URI, initialValues);
	}
	
	public static void insert(ContentResolver resolver, String pref, int data){
		ContentValues initialValues = new ContentValues();
        initialValues.put(PreferenceProvider._PREFERENCE, pref);
        initialValues.put(PreferenceProvider._INTEGER_DATA, data);
        
        resolver.insert(PreferenceProvider.CONTENT_URI, initialValues);
	}
	
	public static void update(ContentResolver resolver, String pref, String data){
		ContentValues values = new ContentValues();
        values.put(PreferenceProvider._STRING_DATA, data);
        
		if(resolver.update(Uri.withAppendedPath(PreferenceProvider.CONTENT_URI, pref), values, null, null) == 0){
			insert(resolver, pref, data);
		}
		
	}
	
	public static void update(ContentResolver resolver, String pref, int data){
		ContentValues values = new ContentValues();
        values.put(PreferenceProvider._INTEGER_DATA, data);
        
		if(resolver.update(Uri.withAppendedPath(PreferenceProvider.CONTENT_URI, pref), values, null, null) == 0){
			insert(resolver, pref, data);
		}
		
	}
	
	public static String queryString(ContentResolver resolver, String pref, String def){
        Cursor preference = resolver.query(Uri.withAppendedPath(PreferenceProvider.CONTENT_URI, pref), null, null, null, null);
        if(preference != null && preference.moveToFirst()){
        	int column = preference.getColumnIndex(PreferenceProvider._STRING_DATA);
        	String prefit = preference.getString(column);
        	if(prefit == null || prefit.equals("")){
        		return def;
        	}
        	return prefit;
        }
        return def;
	}
	
	public static int queryInt(ContentResolver resolver, String pref, int def){
        Cursor preference = resolver.query(Uri.withAppendedPath(PreferenceProvider.CONTENT_URI, pref), 
        		                          null, null, null, null);
        if(preference != null && preference.moveToFirst()){
        	return preference.getInt(preference.getColumnIndex(PreferenceProvider._INTEGER_DATA));
        }
        return def;
	}
	
	public static boolean queryBoolean(ContentResolver resolver, String pref, boolean def){
        Cursor preference = resolver.query(Uri.withAppendedPath(PreferenceProvider.CONTENT_URI, pref), 
        		                          null, null, null, null);
        if(preference != null && preference.moveToFirst()){
        	int get = preference.getInt(preference.getColumnIndex(PreferenceProvider._INTEGER_DATA));
        	return get == 1;
        }
        return def;
	}
	
}
