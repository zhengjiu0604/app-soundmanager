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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

import com.roozen.SoundManager.provider.ScheduleProvider;
import com.roozen.SoundManager.schedule.Schedule;
import com.roozen.SoundManager.utils.SQLiteDatabaseHelper;

public class BootupService extends Service {
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		/*
		 * get all active schedules and register them with the AlarmManager
		 */
		ContentResolver cr = getContentResolver();
		Uri schedulesUri = Uri.withAppendedPath(ScheduleProvider.CONTENT_URI, "active");
		Cursor scheduleCursor = cr.query(schedulesUri, null, null, null, null);
		
		if (scheduleCursor.moveToFirst()) {
		    
		    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		    int idIndex = scheduleCursor.getColumnIndex(SQLiteDatabaseHelper.SCHEDULE_ID);
            int startHourIndex = scheduleCursor.getColumnIndex(SQLiteDatabaseHelper.SCHEDULE_START_HOUR);
            int startMinuteIndex = scheduleCursor.getColumnIndex(SQLiteDatabaseHelper.SCHEDULE_START_MINUTE);
		    
		    do {
		        
		        int scheduleId = scheduleCursor.getInt(idIndex);
		        int startHour = scheduleCursor.getInt(startHourIndex);
		        int startMinute = scheduleCursor.getInt(startMinuteIndex);
		        
                Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                cal.set(Calendar.HOUR_OF_DAY, startHour);
                cal.set(Calendar.MINUTE, startMinute);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 200);
		        
		        Intent scheduleIntent = new Intent (this, Schedule.class);
		        scheduleIntent.setAction(String.valueOf(scheduleId));
		        scheduleIntent.putExtra(SQLiteDatabaseHelper.SCHEDULE_ID, scheduleId);
		        PendingIntent pi = PendingIntent.getBroadcast(this, 0, scheduleIntent, 0);
		        
		        //repeat the alarm every day; the receiver will check day of week
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
		        
		    } while(scheduleCursor.moveToNext());
		    
	    }
		
		scheduleCursor.close();
		
        stopSelf();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		onStart(intent, 0);
		return null;
	}
}
