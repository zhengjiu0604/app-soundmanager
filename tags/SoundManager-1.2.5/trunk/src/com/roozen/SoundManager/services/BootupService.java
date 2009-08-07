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

import com.roozen.SoundManager.MainSettings;
import com.roozen.SoundManager.R;
import com.roozen.SoundManager.receivers.SoundTimer;
import com.roozen.SoundManager.utils.DbUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.IBinder;

public class BootupService extends Service {
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Intent soundTimer = new Intent(this, SoundTimer.class);
		
		ContentResolver resolver = getContentResolver();

		boolean systemEnabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableSystem), false);
		boolean ringerEnabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableRinger), false);
		boolean mediaEnabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableMedia), false);
		boolean alarmEnabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableAlarm), false);
		boolean incallEnabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableIncall), false);
		
		boolean ringmodeEnabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableRingmode), false);
		boolean vibrateRingerEnabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableVibrateRinger), false);
		boolean vibrateNotifEnabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableVibrateNotif), false);
		
		if(systemEnabled){
			String time = DbUtil.queryString(resolver, getString(R.string.SystemTimeStart), null);
			int vol = DbUtil.queryInt(resolver, getString(R.string.SystemStartVolume), -1);
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 200);
				
				PendingIntent pendingSystemStart = PendingIntent.getBroadcast(this, R.string.SystemTimeStart, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.SYSTEM_VOLUME_START), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingSystemStart); // Repeating alarm every day
			}
			
			time = DbUtil.queryString(resolver, getString(R.string.SystemTimeEnd), null);
			vol = DbUtil.queryInt(resolver, getString(R.string.SystemEndVolume), -1);
			
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 205);
				
				PendingIntent pendingSystemEnd = PendingIntent.getBroadcast(this, R.string.SystemTimeEnd, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.SYSTEM_VOLUME_END), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingSystemEnd); // Repeating alarm every day
			}
		}
		
		if(ringerEnabled){
			String time = DbUtil.queryString(resolver, getString(R.string.RingerTimeStart), null);
			int vol = DbUtil.queryInt(resolver, getString(R.string.RingerStartVolume), -1);
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 200);
				
				PendingIntent pendingRingerStart = PendingIntent.getBroadcast(this, R.string.RingerTimeStart, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.RINGER_VOLUME_START), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingRingerStart); // Repeating alarm every day
			}
			
			time = DbUtil.queryString(resolver, getString(R.string.RingerTimeEnd), null);
			vol = DbUtil.queryInt(resolver, getString(R.string.RingerEndVolume), -1);
			
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 205);
				
				PendingIntent pendingRingerEnd = PendingIntent.getBroadcast(this, R.string.RingerTimeEnd, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.RINGER_VOLUME_END), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingRingerEnd); // Repeating alarm every day
			}
		}
		
		if(mediaEnabled){
			String time = DbUtil.queryString(resolver, getString(R.string.MediaTimeStart), null);
			int vol = DbUtil.queryInt(resolver, getString(R.string.MediaStartVolume), -1);
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 200);
				
				PendingIntent pendingMediaStart = PendingIntent.getBroadcast(this, R.string.MediaTimeStart, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.MEDIA_VOLUME_START), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingMediaStart); // Repeating alarm every day
			}
			
			time = DbUtil.queryString(resolver, getString(R.string.MediaTimeEnd), null);
			vol = DbUtil.queryInt(resolver, getString(R.string.MediaEndVolume), -1);
			
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 205);
				
				PendingIntent pendingMediaEnd = PendingIntent.getBroadcast(this, R.string.MediaTimeEnd, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.MEDIA_VOLUME_END), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingMediaEnd); // Repeating alarm every day
			}
		}
		
		if(alarmEnabled){
			String time = DbUtil.queryString(resolver, getString(R.string.AlarmTimeStart), null);
			int vol = DbUtil.queryInt(resolver, getString(R.string.AlarmStartVolume), -1);
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 200);
				
				PendingIntent pendingAlarmStart = PendingIntent.getBroadcast(this, R.string.AlarmTimeStart, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.ALARM_VOLUME_START), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingAlarmStart); // Repeating alarm every day
			}
			
			time = DbUtil.queryString(resolver, getString(R.string.AlarmTimeEnd), null);
			vol = DbUtil.queryInt(resolver, getString(R.string.AlarmEndVolume), -1);
			
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 205);
				
				PendingIntent pendingAlarmEnd = PendingIntent.getBroadcast(this, R.string.AlarmTimeEnd, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.ALARM_VOLUME_END), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingAlarmEnd); // Repeating alarm every day
			}
		}
		
		
		
		if(incallEnabled){
			String time = DbUtil.queryString(resolver, getString(R.string.IncallTimeStart), null);
			int vol = DbUtil.queryInt(resolver, getString(R.string.IncallStartVolume), -1);
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 200);
				
				PendingIntent pendingIncallStart = PendingIntent.getBroadcast(this, R.string.IncallTimeStart, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.INCALL_VOLUME_START), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIncallStart); // Repeating alarm every day
			}
			
			time = DbUtil.queryString(resolver, getString(R.string.IncallTimeEnd), null);
			vol = DbUtil.queryInt(resolver, getString(R.string.IncallEndVolume), -1);
			
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 205);
				
				PendingIntent pendingIncallEnd = PendingIntent.getBroadcast(this, R.string.IncallTimeEnd, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.INCALL_VOLUME_END), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIncallEnd); // Repeating alarm every day
			}
		}
		
		if(ringmodeEnabled){
			String time = DbUtil.queryString(resolver, getString(R.string.RingmodeTimeStart), null);
			int vol = DbUtil.queryInt(resolver, getString(R.string.RingmodeStartVolume), -1);
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 200);
				
				PendingIntent pendingRingmodeStart = PendingIntent.getBroadcast(this, R.string.RingmodeTimeStart, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.RINGER_MODE_START), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingRingmodeStart); // Repeating alarm every day
			}
			
			time = DbUtil.queryString(resolver, getString(R.string.RingmodeTimeEnd), null);
			vol = DbUtil.queryInt(resolver, getString(R.string.RingmodeEndVolume), -1);
			
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 205);
				
				PendingIntent pendingRingmodeEnd = PendingIntent.getBroadcast(this, R.string.RingmodeTimeEnd, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.RINGER_MODE_END), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingRingmodeEnd); // Repeating alarm every day
			}
		}
		
		if(vibrateRingerEnabled){
			String time = DbUtil.queryString(resolver, getString(R.string.VibrateRingerTimeStart), null);
			int vol = DbUtil.queryInt(resolver, getString(R.string.VibrateRingerStartVolume), -1);
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 200);
				
				PendingIntent pendingVibrateRingerStart = PendingIntent.getBroadcast(this, R.string.VibrateRingerTimeStart, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.VIBRATE_RINGER_START), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingVibrateRingerStart); // Repeating alarm every day
			}
			
			time = DbUtil.queryString(resolver, getString(R.string.VibrateRingerTimeEnd), null);
			vol = DbUtil.queryInt(resolver, getString(R.string.VibrateRingerEndVolume), -1);
			
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 205);
				
				PendingIntent pendingVibrateRingerEnd = PendingIntent.getBroadcast(this, R.string.VibrateRingerTimeEnd, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.VIBRATE_RINGER_END), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingVibrateRingerEnd); // Repeating alarm every day
			}
		}

		if(vibrateNotifEnabled){
			String time = DbUtil.queryString(resolver, getString(R.string.VibrateNotifTimeStart), null);
			int vol = DbUtil.queryInt(resolver, getString(R.string.VibrateNotifStartVolume), -1);
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 200);
				
				PendingIntent pendingVibrateNotifStart = PendingIntent.getBroadcast(this, R.string.VibrateNotifTimeStart, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.VIBRATE_NOTIF_START), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingVibrateNotifStart); // Repeating alarm every day
			}
			
			time = DbUtil.queryString(resolver, getString(R.string.VibrateNotifTimeEnd), null);
			vol = DbUtil.queryInt(resolver, getString(R.string.VibrateNotifEndVolume), -1);
			
			if(time != null && vol >= 0 && time.contains(":")){
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, time.indexOf(":"))));
				cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 205);
				
				PendingIntent pendingVibrateNotifEnd = PendingIntent.getBroadcast(this, R.string.VibrateNotifTimeEnd, 
						new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.VIBRATE_NOTIF_END), 0);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingVibrateNotifEnd); // Repeating alarm every day
			}
		}
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		onStart(intent, 0);
		return null;
	}
}
