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
import android.os.IBinder;

import com.roozen.SoundManager.provider.ScheduleProvider;
import com.roozen.SoundManager.utils.SQLiteDatabaseHelper;

/**
 * Service that changes volume, ringmode, and vibrate setting given a schedule id
 * 
 * @author droozen
 */
public class ChangeVolume extends Service {

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		int scheduleId = Integer.parseInt(intent.getData().getPathSegments().get(1));
		
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
                     * if the schedule is setup for today, apply the settings
                     */
                    if (dayOfWeek == Calendar.SUNDAY && day0 ||
                            dayOfWeek == Calendar.MONDAY && day1 ||
                            dayOfWeek == Calendar.TUESDAY && day2 ||
                            dayOfWeek == Calendar.WEDNESDAY && day3 ||
                            dayOfWeek == Calendar.THURSDAY && day4 ||
                            dayOfWeek == Calendar.FRIDAY && day5 ||
                            dayOfWeek == Calendar.SATURDAY && day6                            
                    ) {
                        
                        switch (volumeType) {
                            case AudioManager.STREAM_SYSTEM:
                                setVolume(volumeType, volume);
                                break;
                            case AudioManager.STREAM_RING:
                                setRingmode(volume, vibrate);
                                setVolume(volumeType, volume);
                                setVibration(AudioManager.VIBRATE_TYPE_RINGER, vibrate);                                
                                break;
                            case AudioManager.STREAM_NOTIFICATION:
                                setVolume(volumeType, volume);
                                setVibration(AudioManager.VIBRATE_TYPE_NOTIFICATION, vibrate);
                                break;
                            case AudioManager.STREAM_MUSIC:
                                setVolume(volumeType, volume);
                                break;
                            case AudioManager.STREAM_ALARM:
                                setVolume(volumeType, volume);
                                break;
                            case AudioManager.STREAM_VOICE_CALL:
                                setVolume(volumeType, volume);
                                break;
                        }
                                                
                    } //schedule applies today
                    
                } //end active check
                
            } //end schedule cursor check
            
            scheduleCursor.close();		    
		} //schedule id was found
		
	}
	
    private void setVolume(int stream, int volume) {
        final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = audio.getStreamMaxVolume(stream);
        if (volume > maxVolume) {
            volume = maxVolume;
        }
        else if (volume < 0) {
            volume = 0;
        }

        int flags = AudioManager.FLAG_PLAY_SOUND;
        flags = flags | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE;
        flags = flags | AudioManager.FLAG_SHOW_UI;
        flags = flags | AudioManager.FLAG_VIBRATE;

        /*
         * apply volume to the system
         */
        audio.setStreamVolume(stream, volume, flags);
    }

    private void setRingmode(int volume, boolean vibrate) {
        final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        /*
         * apply ringmode to the system
         */
        if (volume < 1 && !vibrate) {
            audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
        else if (volume < 1 && vibrate) {
            audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
        else if (volume > 0) {
            audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

    }

    private void setVibration(int type, boolean vibrate) {
        final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        /*
         * apply vibrate settings to the system
         */
        if (vibrate) {
            audio.setVibrateSetting(type, AudioManager.VIBRATE_SETTING_ON);
        }
        else {
            audio.setVibrateSetting(type, AudioManager.VIBRATE_SETTING_OFF);
        }
        
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		onStart(intent, 0);
		return null;
	}

}
