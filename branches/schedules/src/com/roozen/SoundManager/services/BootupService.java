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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BootupService extends Service {
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		/*
		if(systemEnabled){
			/*String time = DbUtil.queryString(resolver, getString(R.string.SystemTimeStart), null);
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
	    */
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		onStart(intent, 0);
		return null;
	}
}
