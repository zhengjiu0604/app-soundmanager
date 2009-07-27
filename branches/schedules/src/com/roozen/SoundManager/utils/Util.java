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

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class Util {
    
    public static final String PREFS_NAME = "SoundManagerPrefs";

	public static String padZero(int num){
		String str = Integer.toString(num);
		if(num < 10){
			str = "0" + str;
		}
		return str;
	}
	
	public static String padZero(String str){
		if(str.length() < 2){
			str = "0" + str;
		}
		return str;
	}
	
	public static Calendar getMyCalendar(int hour, int minute, int second, int milli){
		Calendar cal = Calendar.getInstance(TimeZone.getDefault(),	Locale.getDefault());
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, milli);
		return cal;
	}

    /**
     * Queries system settings for the system clock format
     * 
     * @return boolean
     */
	public static boolean is24HourClock(ContentResolver contentResolver) {
	    boolean clock24hour;
    
        try {
            clock24hour = (Settings.System.getInt(contentResolver, Settings.System.TIME_12_24) == 24);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
            clock24hour = false;
        }
        
        return clock24hour;
    }
    
	/**
	 * get a Boolean out of SharedPreferences
	 * 
	 * @param context
	 * @param name
	 * @param def
	 * @return
	 */
	public static boolean getBooleanPref(Context context, String name, boolean def) {
	    return context.getSharedPreferences(PREFS_NAME, 
	                                        Context.MODE_PRIVATE).getBoolean(name, def);
	}
	
	/**
	 * put a Boolean into SharedPreferences
	 * 
	 * @param context
	 * @param name
	 * @param value
	 */
	public static void putBooleanPref(Context context, String name, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(name, value);
        editor.commit();
	}
	
	   /**
     * get a Boolean out of SharedPreferences
     * 
     * @param context
     * @param name
     * @param def
     * @return
     */
    public static int getIntPref(Context context, String name, int def) {
        return context.getSharedPreferences(PREFS_NAME, 
                                            Context.MODE_PRIVATE).getInt(name, def);
    }
    
    /**
     * put a Boolean into SharedPreferences
     * 
     * @param context
     * @param name
     * @param value
     */
    public static void putIntPref(Context context, String name, int value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(name, value);
        editor.commit();
    }
	
}
