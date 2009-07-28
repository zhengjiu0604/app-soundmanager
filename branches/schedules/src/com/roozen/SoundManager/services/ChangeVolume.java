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
package com.roozen.SoundManager.services;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import com.roozen.SoundManager.RingmodeToggle;
import com.roozen.SoundManager.provider.ScheduleProvider;
import com.roozen.SoundManager.utils.SQLiteDatabaseHelper;

/**
 * Service that changes volume, ringmode, and vibrate setting
 * 
 * @author droozen
 */
public class ChangeVolume extends Service {

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		Bundle extras = intent.getExtras();
		int scheduleId = extras != null ? extras.getInt(SQLiteDatabaseHelper.SCHEDULE_ID, -1) : -1;
		
		if (scheduleId > 0) {
		    
		    ContentResolver cr = getContentResolver();
            Uri schedulesUri = Uri.withAppendedPath(ScheduleProvider.CONTENT_URI, String.valueOf(scheduleId));
            Cursor scheduleCursor = cr.query(schedulesUri, null, null, null, null);

            if (scheduleCursor.moveToFirst()) {
            
                boolean day0 = (scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY0)) > 0);
                boolean day1 = (scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY1)) > 0);
                boolean day2 = (scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY2)) > 0);
                boolean day3 = (scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY3)) > 0);
                boolean day4 = (scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY4)) > 0);
                boolean day5 = (scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY5)) > 0);
                boolean day6 = (scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY6)) > 0);
                int volume = scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_VOLUME));
                boolean vibrate = (scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_VIBRATE)) > 0);
                boolean active = (scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_ACTIVE)) > 0);
                int volumeType = scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_TYPE));
                
                if (active) {
                    
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                    
                    /*
                     * is the schedule setup for today?
                     */
                    if (dayOfWeek == Calendar.SUNDAY && day0 ||
                            dayOfWeek == Calendar.MONDAY && day1 ||
                            dayOfWeek == Calendar.TUESDAY && day2 ||
                            dayOfWeek == Calendar.WEDNESDAY && day3 ||
                            dayOfWeek == Calendar.THURSDAY && day4 ||
                            dayOfWeek == Calendar.FRIDAY && day5 ||
                            dayOfWeek == Calendar.SATURDAY && day6                            
                    ) {
                        
                        //TODO: based on volumeType, these may or may not all be called,
                        // and the vibrate type may differ
                        
                        checkVolume(volumeType, volume, AudioManager.VIBRATE_TYPE_RINGER);
                        checkRingmode(volume);
                        checkVibrate(AudioManager.VIBRATE_TYPE_RINGER, volume);
                                                
                    }
                    
                } //end active check
                
            } //end schedule cursor check
            
            scheduleCursor.close();		    
		}
		
	}
	
    private void checkVolume(int stream, int vol, int vibrateType) {
        if (vol != -1) {
            final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            int volMax = audio.getStreamMaxVolume(stream);
            if (vol > volMax) {
                vol = volMax;
            }
            else if (vol < 0) {
                vol = 0;
            }

            if (vibrateType == AudioManager.VIBRATE_TYPE_RINGER) {
                RingmodeToggle.fixRingMode(audio, vol);
            }

            int flags = AudioManager.FLAG_PLAY_SOUND;
            flags = flags | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE;
            flags = flags | AudioManager.FLAG_SHOW_UI;
            flags = flags | AudioManager.FLAG_VIBRATE;

            audio.setStreamVolume(stream, vol, flags);
        }
    }

    private void checkRingmode(int type) {
        if (type != -1) {
            final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            switch (type) {
                case RingmodeToggle.SILENT:
                    audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    break;
                case RingmodeToggle.VIBRATE_ONLY:
                    audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;
                case RingmodeToggle.RINGER_ONLY:
                    audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    int vibrateSetting = audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
                    if (vibrateSetting == AudioManager.VIBRATE_SETTING_ON) {
                        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
                                                AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                    }
                    else {
                        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
                    }
                    break;
                case RingmodeToggle.RINGER_VIBRATE:
                    audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
                    break;
            }
        }
    }

    private void checkVibrate(int type, int vibrateSetting) {
        if (type != -1 && vibrateSetting != -1) {
            final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audio.setVibrateSetting(type, vibrateSetting);
            RingmodeToggle.fixRingMode(audio);
        }
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		onStart(intent, 0);
		return null;
	}

}
