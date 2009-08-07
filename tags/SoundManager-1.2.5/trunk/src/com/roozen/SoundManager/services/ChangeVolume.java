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

import com.roozen.SoundManager.MainSettings;
import com.roozen.SoundManager.R;
import com.roozen.SoundManager.RingmodeToggle;
import com.roozen.SoundManager.utils.DbUtil;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;

public class ChangeVolume extends Service {

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	
		Bundle extras = intent.getExtras();            
		int type = extras != null ? extras.getInt(MainSettings.EXTRA_WHICH) : -1;
		if(type == -1){
			return;
		}
		
        int vol;
        boolean enabled;
        ContentResolver resolver = getContentResolver();
        
		switch(type){
		case MainSettings.RINGER_VOLUME_START:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableRinger), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.RingerStartVolume), -1);
				checkVolume(AudioManager.STREAM_RING, vol, AudioManager.VIBRATE_TYPE_RINGER);
			}
			break;
		case MainSettings.RINGER_VOLUME_END:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableRinger), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.RingerEndVolume), -1);
			    checkVolume(AudioManager.STREAM_RING, vol, AudioManager.VIBRATE_TYPE_RINGER);
			}
			break;
		case MainSettings.ALARM_VOLUME_START:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableAlarm), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.AlarmStartVolume), -1);
				checkVolume(AudioManager.STREAM_ALARM, vol, -1);
			}
			break;
		case MainSettings.ALARM_VOLUME_END:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableAlarm), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.AlarmEndVolume), -1);
				checkVolume(AudioManager.STREAM_ALARM, vol, -1);
			}
			break;
		case MainSettings.MEDIA_VOLUME_START:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableMedia), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.MediaStartVolume), -1);
				checkVolume(AudioManager.STREAM_MUSIC, vol, -1);
			}
			break;
		case MainSettings.MEDIA_VOLUME_END:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableMedia), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.MediaEndVolume), -1);
				checkVolume(AudioManager.STREAM_MUSIC, vol, -1);
			}
			break;
		case MainSettings.INCALL_VOLUME_START:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableIncall), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.IncallStartVolume), -1);
				checkVolume(AudioManager.STREAM_VOICE_CALL, vol, -1);
			}
			break;
		case MainSettings.INCALL_VOLUME_END:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableIncall), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.IncallEndVolume), -1);
				checkVolume(AudioManager.STREAM_VOICE_CALL, vol, -1);
			}
			break;
		case MainSettings.SYSTEM_VOLUME_START:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableSystem), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.SystemStartVolume), -1);
				checkVolume(AudioManager.STREAM_SYSTEM, vol, -1);
			}
			break;
		case MainSettings.SYSTEM_VOLUME_END:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableSystem), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.SystemEndVolume), -1);
				checkVolume(AudioManager.STREAM_SYSTEM, vol, -1);
			}
			break;
		case MainSettings.RINGER_MODE_START:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableRingmode), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.RingmodeStartVolume), -1);
				checkRingmode(vol);
			}
			break;
		case MainSettings.RINGER_MODE_END:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableRingmode), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.RingmodeEndVolume), -1);
				checkRingmode(vol);
			}
			break;
		case MainSettings.VIBRATE_RINGER_START:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableVibrateRinger), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.VibrateRingerStartVolume), -1);
				checkVibrate(AudioManager.VIBRATE_TYPE_RINGER, vol);
			}
			break;
		case MainSettings.VIBRATE_RINGER_END:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableVibrateRinger), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.VibrateRingerEndVolume), -1);
				checkVibrate(AudioManager.VIBRATE_TYPE_RINGER, vol);
			}
			break;
		case MainSettings.VIBRATE_NOTIF_START:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableVibrateNotif), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.VibrateNotifStartVolume), -1);
				checkVibrate(AudioManager.VIBRATE_TYPE_NOTIFICATION, vol);
			}
			break;
		case MainSettings.VIBRATE_NOTIF_END:
			enabled = DbUtil.queryBoolean(resolver, getString(R.string.EnableVibrateNotif), false);
			if(enabled){
				vol = DbUtil.queryInt(resolver, getString(R.string.VibrateNotifEndVolume), -1);
				checkVibrate(AudioManager.VIBRATE_TYPE_NOTIFICATION, vol);
			}
			break;
		}
	}
	
	private void checkVolume(int stream, int vol, int vibrateType){
        if(vol != -1){
        	final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        	
        	int volMax = audio.getStreamMaxVolume(stream);
	        if(vol > volMax){
	        	vol = volMax;
	        } else if(vol < 0){
	        	vol = 0;
	        }
	        
	        if(vibrateType == AudioManager.VIBRATE_TYPE_RINGER){
				RingmodeToggle.fixRingMode(audio, vol);
			}
	        
        	int flags = AudioManager.FLAG_PLAY_SOUND;
			flags = flags | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE;
			flags = flags | AudioManager.FLAG_SHOW_UI;
			flags = flags | AudioManager.FLAG_VIBRATE;
			
			audio.setStreamVolume(stream, vol, flags);
		}
	}
	
	private void checkRingmode(int type){
		if(type != -1){
			final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			
			switch(type){
			case RingmodeToggle.SILENT:
				audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				break;
			case RingmodeToggle.VIBRATE_ONLY:
				audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				break;
			case RingmodeToggle.RINGER_ONLY:
				audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				int vibrateSetting = audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
				if(vibrateSetting == AudioManager.VIBRATE_SETTING_ON){
					audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ONLY_SILENT);
				} else {
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
	
	private void checkVibrate(int type, int vibrateSetting){
		if(type != -1 && vibrateSetting != -1){
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
