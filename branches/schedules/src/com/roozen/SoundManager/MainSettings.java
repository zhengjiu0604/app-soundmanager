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
package com.roozen.SoundManager;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.roozen.SoundManager.receivers.SoundTimer;
import com.roozen.SoundManager.schedule.ScheduleList;
import com.roozen.SoundManager.utils.DbUtil;
import com.roozen.SoundManager.utils.Util;

public class MainSettings extends Activity {
	public final static String PREFS_NAME = "EZSoundManagerPrefs";
	
	private Context gui;
	
	private PendingIntent pendingRingerStart;
	private PendingIntent pendingRingerEnd;
	private PendingIntent pendingAlarmStart;
	private PendingIntent pendingAlarmEnd;
	private PendingIntent pendingMediaStart;
	private PendingIntent pendingMediaEnd;
	private PendingIntent pendingSystemStart;
	private PendingIntent pendingSystemEnd;
	private PendingIntent pendingIncallStart;
	private PendingIntent pendingIncallEnd;
	
	public final static String EXTRA_WHICH = "WhichVolume";
	
	public final static int RINGER_VOLUME_START = 0;
	public final static int RINGER_VOLUME_END = 1;
	public final static int ALARM_VOLUME_START = 2;
	public final static int ALARM_VOLUME_END = 3;
	public final static int MEDIA_VOLUME_START = 4;
	public final static int MEDIA_VOLUME_END = 5;
	public final static int SYSTEM_VOLUME_START = 6;
	public final static int SYSTEM_VOLUME_END = 7;
	public final static int INCALL_VOLUME_START = 8;
	public final static int INCALL_VOLUME_END = 9;
	public final static int VIBRATE_NOTIF_START = 10;
	public final static int VIBRATE_NOTIF_END = 11;
	public final static int VIBRATE_RINGER_START = 12;
	public final static int VIBRATE_RINGER_END = 13;
	public final static int RINGER_MODE_START = 14;
	public final static int RINGER_MODE_END = 15;
	
	private static final int ACTIVITY_SCHEDULE = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gui = this;

    	final SharedPreferences settings = getSharedPreferences(MainSettings.PREFS_NAME, 0);
        boolean hasShownStartup = settings.getBoolean(getString(R.string.ShownStartup), false);
        
        setupPendingIntents();        
        setupSeekbars();        
        setupButtons();
        
        if(!hasShownStartup){
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage("Please see my F.A.Q. page (Menu > F.A.Q.) for a full " +
	        		           "explanation of how Sound Manager works if you need help or " +
	        		           "have questions. Feel free to contact the developer by email " +
	        		           "if you've found problems, have a feature request, or need help.\n\n" +
	        		           "Thanks for downloading!");
	        builder.show();
	        
	        Editor edit = settings.edit();
	        edit.putBoolean(getString(R.string.ShownStartup), true);
	        edit.commit();
        }
    }
    
    private void setupPendingIntents(){
    	Intent soundTimer = new Intent(this, SoundTimer.class);

        pendingSystemStart = PendingIntent.getBroadcast(this, R.string.SystemTimeStart, 
        		new Intent(soundTimer).putExtra(EXTRA_WHICH, SYSTEM_VOLUME_START), 0);
        
        pendingSystemEnd = PendingIntent.getBroadcast(this, R.string.SystemTimeEnd, 
        		new Intent(soundTimer).putExtra(EXTRA_WHICH, SYSTEM_VOLUME_END), 0);
        
        pendingRingerStart = PendingIntent.getBroadcast(this, R.string.RingerTimeStart, 
        		new Intent(soundTimer).putExtra(EXTRA_WHICH, RINGER_VOLUME_START), 0);
        
        pendingRingerEnd = PendingIntent.getBroadcast(this, R.string.RingerTimeEnd, 
        		new Intent(soundTimer).putExtra(EXTRA_WHICH, RINGER_VOLUME_END), 0);
        
        pendingMediaStart = PendingIntent.getBroadcast(this, R.string.MediaTimeStart, 
        		new Intent(soundTimer).putExtra(EXTRA_WHICH, MEDIA_VOLUME_START), 0);
        
        pendingMediaEnd = PendingIntent.getBroadcast(this, R.string.MediaTimeEnd, 
        		new Intent(soundTimer).putExtra(EXTRA_WHICH, MEDIA_VOLUME_END), 0);
        
        pendingAlarmStart = PendingIntent.getBroadcast(this, R.string.AlarmTimeStart, 
        		new Intent(soundTimer).putExtra(EXTRA_WHICH, ALARM_VOLUME_START), 0);
        
        pendingAlarmEnd = PendingIntent.getBroadcast(this, R.string.AlarmTimeEnd, 
        		new Intent(soundTimer).putExtra(EXTRA_WHICH, ALARM_VOLUME_END), 0);
        
        
        pendingIncallStart = PendingIntent.getBroadcast(this, R.string.IncallTimeStart, 
        		new Intent(soundTimer).putExtra(EXTRA_WHICH, INCALL_VOLUME_START), 0);
        
        pendingIncallEnd = PendingIntent.getBroadcast(this, R.string.IncallTimeEnd, 
        		new Intent(soundTimer).putExtra(EXTRA_WHICH, INCALL_VOLUME_END), 0);
    }
    
    private void setupSeekbars(){
    	final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	final int flagsNoUI = AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE |
    						  AudioManager.FLAG_VIBRATE;
    	final int flagsUI = AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE |
							AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_VIBRATE;
    	
        final int systemStream = AudioManager.STREAM_SYSTEM;
        final int ringStream = AudioManager.STREAM_RING;
        final int mediaStream = AudioManager.STREAM_MUSIC;
        final int alarmStream = AudioManager.STREAM_ALARM;
        final int incallStream = AudioManager.STREAM_VOICE_CALL;
        
        SeekBar systemSeek = (SeekBar) findViewById(R.id.system_seekbar);
        systemSeek.setMax(audio.getStreamMaxVolume(systemStream));
        systemSeek.setProgress(audio.getStreamVolume(systemStream));
        systemSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
        	
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
				audio.setStreamVolume(systemStream, progress, flagsNoUI);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				audio.setStreamVolume(systemStream, seekBar.getProgress(), flagsUI);
			}
        	
        });
        
        SeekBar ringerSeek = (SeekBar) findViewById(R.id.ringer_seekbar);
        ringerSeek.setMax(audio.getStreamMaxVolume(ringStream));
        ringerSeek.setProgress(audio.getStreamVolume(ringStream));
        
        ringerSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
				audio.setStreamVolume(ringStream, progress, flagsNoUI);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				RingmodeToggle.fixRingMode(audio, seekBar.getProgress());
				audio.setStreamVolume(ringStream, seekBar.getProgress(), flagsUI);
			}
        	
        });
        
        SeekBar mediaSeek = (SeekBar) findViewById(R.id.media_seekbar);
        mediaSeek.setMax(audio.getStreamMaxVolume(mediaStream));
        mediaSeek.setProgress(audio.getStreamVolume(mediaStream));
        mediaSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
				audio.setStreamVolume(mediaStream, progress, flagsNoUI);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				audio.setStreamVolume(mediaStream, seekBar.getProgress(), flagsUI);
			}
        	
        });
        
        SeekBar alarmSeek = (SeekBar) findViewById(R.id.alarm_seekbar);
        alarmSeek.setMax(audio.getStreamMaxVolume(alarmStream));
        alarmSeek.setProgress(audio.getStreamVolume(alarmStream));
        alarmSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
				audio.setStreamVolume(alarmStream, progress, flagsNoUI);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				audio.setStreamVolume(alarmStream, seekBar.getProgress(), flagsUI);
			}
        	
        });
        
        SeekBar phonecallSeek = (SeekBar) findViewById(R.id.phonecall_seekbar);
        phonecallSeek.setMax(audio.getStreamMaxVolume(incallStream));
        phonecallSeek.setProgress(audio.getStreamVolume(incallStream));
        phonecallSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
				audio.setStreamVolume(incallStream, progress, flagsNoUI);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				audio.setStreamVolume(incallStream, seekBar.getProgress(), flagsUI);
			}
        	
        });        
    }
    
    private void setupButtons() {
		final ContentResolver resolver = getContentResolver();
		
		TextView systemText = (TextView) findViewById(R.id.system_timer_text);
        systemText.setText(DbUtil.queryString(resolver, getString(R.string.SystemDisplay), ""));
	    	
        Button systemTimer = (Button) findViewById(R.id.system_timer_button);
        systemTimer.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent i = new Intent(gui, ScheduleList.class);
        		i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_SYSTEM));
        		startActivityForResult(i, ACTIVITY_SCHEDULE);
        	}
        });
		
	    TextView ringerText = (TextView) findViewById(R.id.ringer_timer_text);
	    ringerText.setText(DbUtil.queryString(resolver, getString(R.string.RingerDisplay), ""));
	    	
	    Button ringerTimer = (Button) findViewById(R.id.ringer_timer_button);
	    ringerTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_RING));
                startActivityForResult(i, ACTIVITY_SCHEDULE);
            }
        });
	    
	    TextView mediaText = (TextView) findViewById(R.id.media_timer_text);
    	mediaText.setText(DbUtil.queryString(resolver, getString(R.string.MediaDisplay), ""));
    	
        Button mediaTimer = (Button) findViewById(R.id.media_timer_button);
        mediaTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_MUSIC));
                startActivityForResult(i, ACTIVITY_SCHEDULE);
            }
        });
        
        TextView alarmText = (TextView) findViewById(R.id.alarm_timer_text);
    	alarmText.setText(DbUtil.queryString(resolver, getString(R.string.AlarmDisplay), ""));
    	
        Button alarmTimer = (Button) findViewById(R.id.alarm_timer_button);
        alarmTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_ALARM));
                startActivityForResult(i, ACTIVITY_SCHEDULE);
            }
        });
        
    	TextView incallText = (TextView) findViewById(R.id.phonecall_timer_text);
    	incallText.setText(DbUtil.queryString(resolver, getString(R.string.IncallDisplay), ""));
    	
    	Button incallTimer = (Button) findViewById(R.id.phonecall_timer_button);
    	incallTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_VOICE_CALL));
                startActivityForResult(i, ACTIVITY_SCHEDULE);
            }
        });
    	   
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		ContentResolver resolver = getContentResolver();
        
		switch(item.getItemId()){
		case R.id.just_mute:
			Intent mute = new Intent(this, MuteActivity.class);
			startActivity(mute);
			return true;
		case R.id.create_mute_shortcut:
			Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
			shortcutIntent.setClassName(this, MuteActivity.class.getName());

			Intent intent = new Intent();
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Mute/Unmute");
			Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.mute);
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			sendBroadcast(intent);

			// Inform the user that the shortcut has been created
			Toast.makeText(this, "Shortcut Created", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.disable_system:
			DbUtil.update(resolver, getString(R.string.EnableSystem), 0);
			DbUtil.update(resolver, getString(R.string.SystemDisplay), "");
			
			alarmManager.cancel(pendingSystemStart);
	        alarmManager.cancel(pendingSystemEnd);
	        
	        ((TextView) findViewById(R.id.system_timer_text)).setText("");

	        Toast.makeText(this, getString(R.string.SystemDisabled), Toast.LENGTH_SHORT).show();
	        return true;
		case R.id.disable_ringer:
			DbUtil.update(resolver, getString(R.string.EnableRinger), 0);
			DbUtil.update(resolver, getString(R.string.RingerDisplay), "");
			
			alarmManager.cancel(pendingRingerStart);
	        alarmManager.cancel(pendingRingerEnd);
	        
	        ((TextView) findViewById(R.id.ringer_timer_text)).setText("");

	        Toast.makeText(this, getString(R.string.RingerDisabled), Toast.LENGTH_SHORT).show();
			return true;
		case R.id.disable_media:
			DbUtil.update(resolver, getString(R.string.EnableMedia), 0);
			DbUtil.update(resolver, getString(R.string.MediaDisplay), "");
			
			alarmManager.cancel(pendingMediaStart);
	        alarmManager.cancel(pendingMediaEnd);

	        ((TextView) findViewById(R.id.media_timer_text)).setText("");

	        Toast.makeText(this, getString(R.string.MediaDisabled), Toast.LENGTH_SHORT).show();
			return true;
		case R.id.disable_alarm:
			DbUtil.update(resolver, getString(R.string.EnableAlarm), 0);
			DbUtil.update(resolver, getString(R.string.AlarmDisplay), "");
			
			alarmManager.cancel(pendingAlarmStart);
	        alarmManager.cancel(pendingAlarmEnd);
	        
	        ((TextView) findViewById(R.id.alarm_timer_text)).setText("");

	        Toast.makeText(this, getString(R.string.AlarmDisabled), Toast.LENGTH_SHORT).show();
			return true;
		case R.id.disable_incall:
			DbUtil.update(resolver, getString(R.string.EnableIncall), 0);
			DbUtil.update(resolver, getString(R.string.IncallDisplay), "");
			
			alarmManager.cancel(pendingIncallStart);
	        alarmManager.cancel(pendingIncallEnd);

	        ((TextView) findViewById(R.id.phonecall_timer_text)).setText("");
	        
	        Toast.makeText(this, getString(R.string.IncallDisabled), Toast.LENGTH_SHORT).show();
			return true;
		case R.id.disable_all:
			DbUtil.update(resolver, getString(R.string.EnableSystem), 0);
			DbUtil.update(resolver, getString(R.string.SystemDisplay), "");
			DbUtil.update(resolver, getString(R.string.EnableRinger), 0);
			DbUtil.update(resolver, getString(R.string.RingerDisplay), "");
			DbUtil.update(resolver, getString(R.string.EnableMedia), 0);
			DbUtil.update(resolver, getString(R.string.MediaDisplay), "");
			DbUtil.update(resolver, getString(R.string.EnableAlarm), 0);
			DbUtil.update(resolver, getString(R.string.AlarmDisplay), "");
			DbUtil.update(resolver, getString(R.string.EnableIncall), 0);
			DbUtil.update(resolver, getString(R.string.IncallDisplay), "");

			alarmManager.cancel(pendingSystemStart);
	        alarmManager.cancel(pendingSystemEnd);
			alarmManager.cancel(pendingRingerStart);
	        alarmManager.cancel(pendingRingerEnd);
			alarmManager.cancel(pendingMediaStart);
	        alarmManager.cancel(pendingMediaEnd);
			alarmManager.cancel(pendingIncallStart);
	        alarmManager.cancel(pendingIncallEnd);
			alarmManager.cancel(pendingAlarmStart);
	        alarmManager.cancel(pendingAlarmEnd);

	        ((TextView) findViewById(R.id.system_timer_text)).setText("");
	        ((TextView) findViewById(R.id.ringer_timer_text)).setText("");
	        ((TextView) findViewById(R.id.media_timer_text)).setText("");
	        ((TextView) findViewById(R.id.alarm_timer_text)).setText("");
	        ((TextView) findViewById(R.id.phonecall_timer_text)).setText("");
			
			Toast.makeText(this, getString(R.string.TimersDisabled), Toast.LENGTH_SHORT).show();
			return true;
		case R.id.vibrate_settings:
			Intent vibrate = new Intent(this, VibrateSettings.class);
			startActivity(vibrate);
			return true;
		case R.id.toggle_ringmode:
			Intent toggle = new Intent(this, RingmodeToggle.class);
			startActivity(toggle);
			return true;
		case R.id.faq:
			Uri uri = Uri.parse("http://code.google.com/p/app-soundmanager/wiki/FAQ");
			Intent i = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
	private void popToastShort(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}