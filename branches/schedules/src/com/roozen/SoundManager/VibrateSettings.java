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

import com.roozen.SoundManager.receivers.SoundTimer;
import com.roozen.SoundManager.utils.DbUtil;
import com.roozen.SoundManager.utils.Util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class VibrateSettings extends Activity {
	

	private PendingIntent pendingVibrateRingerStart;
	private PendingIntent pendingVibrateRingerEnd;
	private PendingIntent pendingVibrateNotifStart;
	private PendingIntent pendingVibrateNotifEnd;

	private Context gui;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vibrate_settings);
        gui = this;
        
        setupPendingIntents();

        final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        setupButtons(audio);
        
        int vibrateRinger = audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
        int vibrateNotif = audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION);
        
        final ContentResolver resolver = getContentResolver();
		
		TextView displayText = (TextView) findViewById(R.id.vibrateringer_timer_text);
		displayText.setText(DbUtil.queryString(resolver, getString(R.string.VibrateRingerDisplay), ""));
		
		Button vibrateRingerTimer = (Button) findViewById(R.id.vibrateringer_timer_button);
		vibrateRingerTimer.setOnClickListener(this.getNewOnClickListener(audio, getString(R.string.VibrateRingerTimeStart), 
				getString(R.string.VibrateRingerTimeEnd), getString(R.string.VibrateRingerStartVolume), getString(R.string.VibrateRingerEndVolume), 
				pendingVibrateRingerStart, pendingVibrateRingerEnd, "Ringer Vibrate Timer Set", getString(R.string.EnableVibrateRinger), displayText, 
				getString(R.string.VibrateRingerDisplay), vibrateRinger));
		

		TextView notifText = (TextView) findViewById(R.id.vibratenotif_timer_text);
		notifText.setText(DbUtil.queryString(resolver, getString(R.string.VibrateNotifDisplay), ""));
		
		Button vibrateNotifTimer = (Button) findViewById(R.id.vibratenotif_timer_button);
		vibrateNotifTimer.setOnClickListener(this.getNewOnClickListener(audio, getString(R.string.VibrateNotifTimeStart), 
				getString(R.string.VibrateNotifTimeEnd), getString(R.string.VibrateNotifStartVolume), getString(R.string.VibrateNotifEndVolume), 
				pendingVibrateNotifStart, pendingVibrateNotifEnd, "Notification Vibrate Timer Set", getString(R.string.EnableVibrateNotif), notifText, 
				getString(R.string.VibrateNotifDisplay), vibrateNotif));
	}
    
    private void setupPendingIntents(){
    	Intent soundTimer = new Intent(this, SoundTimer.class);

        pendingVibrateRingerStart = PendingIntent.getBroadcast(this, R.string.VibrateRingerTimeStart, 
        		new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.VIBRATE_RINGER_START), 0);

        pendingVibrateRingerEnd = PendingIntent.getBroadcast(gui, R.string.VibrateRingerTimeEnd, 
        		new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.VIBRATE_RINGER_END), 0);
        
        pendingVibrateNotifStart = PendingIntent.getBroadcast(gui, R.string.VibrateNotifTimeStart, 
        		new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.VIBRATE_NOTIF_START), 0);

        pendingVibrateNotifEnd = PendingIntent.getBroadcast(gui, R.string.VibrateNotifTimeEnd, 
        		new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.VIBRATE_NOTIF_END), 0);
        
    }
    
    private void setupButtons(final AudioManager audio){
        int vibrateRinger = audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
        int vibrateNotif = audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION);
        
    	final RadioButton ringerAlways = (RadioButton) findViewById(R.id.ringer_vibrate_when_possible);
        final RadioButton ringerSilent = (RadioButton) findViewById(R.id.ringer_vibrate_when_silent);
        final RadioButton ringerNever = (RadioButton) findViewById(R.id.ringer_vibrate_never);
        
        final RadioButton notifAlways = (RadioButton) findViewById(R.id.notif_vibrate_when_possible);
        final RadioButton notifSilent = (RadioButton) findViewById(R.id.notif_vibrate_when_silent);
        final RadioButton notifNever = (RadioButton) findViewById(R.id.notif_vibrate_never);
        
        switch(vibrateRinger){
        case AudioManager.VIBRATE_SETTING_ON:
        	ringerAlways.setChecked(true);
        	break;
        case AudioManager.VIBRATE_SETTING_ONLY_SILENT:
        	ringerSilent.setChecked(true);
        	break;
        case AudioManager.VIBRATE_SETTING_OFF:
        	ringerNever.setChecked(true);
        	break;
        }
        
        switch(vibrateNotif){
        case AudioManager.VIBRATE_SETTING_ON:
        	notifAlways.setChecked(true);
        	break;
        case AudioManager.VIBRATE_SETTING_ONLY_SILENT:
        	notifSilent.setChecked(true);
        	break;
        case AudioManager.VIBRATE_SETTING_OFF:
        	notifNever.setChecked(true);
        	break;
        }
        
        ringerAlways.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				if(isChecked){
					audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
					RingmodeToggle.fixRingMode(audio);
				}
			}
        	
        });
        
        ringerSilent.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ONLY_SILENT);
					RingmodeToggle.fixRingMode(audio);
				}
			}
        	
        });
        
        ringerNever.setOnCheckedChangeListener(new OnCheckedChangeListener(){
        	
        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
					RingmodeToggle.fixRingMode(audio);
				}
			}
        	
        });
        
        notifAlways.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				if(isChecked){
					audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
				}
			}
        	
        });
        
        notifSilent.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ONLY_SILENT);
				} 
			}
        	
        });
        
        notifNever.setOnCheckedChangeListener(new OnCheckedChangeListener(){
        	
        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
				} 
			}
        	
        });
    }

	private OnClickListener getNewOnClickListener(final AudioManager audio,
			final String timeStartPref, final String timeEndPref,
			final String volumeStartPref, final String volumeEndPref, 
			final PendingIntent pendingIntentStart,
			final PendingIntent pendingIntentEnd, final String toastMessage,
			final String enablePref, final TextView display,
			final String displayPref, final int vibrateType) {
		
		return new OnClickListener() {

			public void onClick(View view) {
				final ContentResolver resolver = getContentResolver();
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
				String timeDefault = DbUtil.queryString(resolver, timeStartPref, cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));

				TimePickerDialog timeStart = new TimePickerDialog(gui,
						new TimePickerDialog.OnTimeSetListener() {

							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								final String time = hourOfDay + ":" + minute;

								DbUtil.update(resolver, timeStartPref, time);
								
								final Dialog diag = new Dialog(gui);
								diag.setContentView(R.layout.vibrate_timer_choice);
								
								int vibrate = DbUtil.queryInt(resolver, volumeStartPref, audio.getVibrateSetting(vibrateType));

								final RadioButton vibrateOn = (RadioButton) diag.findViewById(R.id.ringer_vibrate_when_possible);
								final RadioButton vibrateWhenSilent = (RadioButton) diag.findViewById(R.id.ringer_vibrate_when_silent);
								final RadioButton vibrateOff = (RadioButton) diag.findViewById(R.id.ringer_vibrate_never);
															
								switch (vibrate) {
								case AudioManager.VIBRATE_SETTING_ON:
									vibrateOn.setChecked(true);
									break;
								case AudioManager.VIBRATE_SETTING_ONLY_SILENT:
									vibrateWhenSilent.setChecked(true);
									break;
								case AudioManager.VIBRATE_SETTING_OFF:
									vibrateOff.setChecked(true);
									break;
								}
																
								Button ok = (Button) diag.findViewById(R.id.ok_button);
								ok.setOnClickListener(new OnClickListener() {

									public void onClick(View v) {
										int vol = -1;
										if(vibrateOn.isChecked()){
											vol = AudioManager.VIBRATE_SETTING_ON;
										} else if(vibrateWhenSilent.isChecked()){
											vol = AudioManager.VIBRATE_SETTING_ONLY_SILENT;
										} else if(vibrateOff.isChecked()){
											vol = AudioManager.VIBRATE_SETTING_OFF;
										}

										DbUtil.update(resolver, volumeStartPref, vol);
										
										diag.dismiss();

										String endDefault = DbUtil.queryString(resolver, timeEndPref, time);

										TimePickerDialog timeEnd = new TimePickerDialog(gui,
												new TimePickerDialog.OnTimeSetListener() {

													public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
														String nexttime = hourOfDay	+ ":" + minute;
														
														DbUtil.update(resolver, timeEndPref, nexttime);

														final Dialog endVolume = new Dialog(gui);
														endVolume.setContentView(R.layout.vibrate_timer_choice);
														
														int endvibrate = DbUtil.queryInt(resolver, volumeEndPref, audio.getVibrateSetting(vibrateType));
														
														final RadioButton endvibrateOn = (RadioButton) endVolume.findViewById(R.id.ringer_vibrate_when_possible);
														final RadioButton endvibrateWhenSilent = (RadioButton) endVolume.findViewById(R.id.ringer_vibrate_when_silent);
														final RadioButton endvibrateOff = (RadioButton) endVolume.findViewById(R.id.ringer_vibrate_never);
																
														switch (endvibrate) {
														case AudioManager.VIBRATE_SETTING_ON:
															endvibrateOn.setChecked(true);
															break;
														case AudioManager.VIBRATE_SETTING_ONLY_SILENT:
															endvibrateWhenSilent.setChecked(true);
															break;
														case AudioManager.VIBRATE_SETTING_OFF:
															endvibrateOff.setChecked(true);
															break;
														}
														
														Button endOk = (Button) endVolume.findViewById(R.id.ok_button);
														endOk.setOnClickListener(new OnClickListener() {

																	public void onClick(View v) {
																		int vol = -1;
																		if(endvibrateOn.isChecked()){
																			vol = AudioManager.VIBRATE_SETTING_ON;
																		} else if(endvibrateWhenSilent.isChecked()){
																			vol = AudioManager.VIBRATE_SETTING_ONLY_SILENT;
																		} else if(endvibrateOff.isChecked()){
																			vol = AudioManager.VIBRATE_SETTING_OFF;
																		}
																		
																		DbUtil.update(resolver, volumeEndPref, vol);

																		endVolume.dismiss();

																		final Dialog OKDialog = new Dialog(gui);
																		OKDialog.setContentView(R.layout.okdialog);
																		TextView oktext = (TextView) OKDialog.findViewById(R.id.verify);
																		OKDialog.setTitle("Confirm");

																		oktext.setText("Are you sure you want to set this timer?\n\n"
																						+ "This may change your volume immediately.\n");

																		Button okbutton = (Button) OKDialog.findViewById(R.id.ok_button);
																		okbutton.setOnClickListener(new OnClickListener() {

																					public void onClick(View view) {
																						String startTime = DbUtil.queryString(resolver, timeStartPref, "");
																						if (startTime != null && startTime.length() > 0	&& startTime.contains(":")) {
																							Calendar cal = Util.getMyCalendar(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))),
																									Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)),
																									0, 200);

																							AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
																							alarmManager.cancel(pendingIntentStart);
																							alarmManager.cancel(pendingIntentEnd);
																							alarmManager.setRepeating(
																											AlarmManager.RTC_WAKEUP,
																											cal.getTimeInMillis(),
																											1000 * 60 * 60 * 24,
																											pendingIntentStart); // Repeating alarm every day
																						}

																						String endTime = DbUtil.queryString(resolver, timeEndPref, "");
																						if (endTime != null	&& endTime.length() > 0 && endTime.contains(":")) {
																							Calendar cal = Util.getMyCalendar(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))),
																									Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)),
																									0, 205);

																							AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
																							alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
																											cal.getTimeInMillis(),
																											1000 * 60 * 60 * 24,
																											pendingIntentEnd); // Repeating alarm every day
																						}

																						String startHour = Util.padZero(startTime.substring(0,startTime.indexOf(":")));
																						String startMin = Util.padZero(startTime.substring(startTime.indexOf(":") + 1));
																						
																						String endHour = Util.padZero(endTime.substring(0, endTime.indexOf(":")));
																						String endMin = Util.padZero(endTime.substring(endTime.indexOf(":") + 1));
																						
																						int startInt = DbUtil.queryInt(resolver, volumeStartPref, -1);
																						String startMode = null;
																						if(startInt == AudioManager.VIBRATE_SETTING_ON){
																							startMode = "Vibrate On";
																						} else if(startInt == AudioManager.VIBRATE_SETTING_ONLY_SILENT){
																							startMode = "Vibrate Silent";
																						} else if(startInt == AudioManager.VIBRATE_SETTING_OFF){
																							startMode = "Vibrate Off";
																						}
																						int endInt = DbUtil.queryInt(resolver, volumeEndPref, -1);
																						String endMode = null;
																						if(endInt == AudioManager.VIBRATE_SETTING_ON){
																							endMode = "Vibrate On";
																						} else if(endInt == AudioManager.VIBRATE_SETTING_ONLY_SILENT){
																							endMode = "Vibrate Silent";
																						} else if(endInt == AudioManager.VIBRATE_SETTING_OFF){
																							endMode = "Vibrate Off";
																						}

																						String displayStr = "Start: " + startHour + ":"	+ startMin + " Mode: " + startMode
																								+ "\nEnd: "	+ endHour + ":"	+ endMin + " Vol: "	+ endMode;
																						display.setText(displayStr);

																						DbUtil.update(resolver, enablePref, 1);
																						DbUtil.update(resolver, displayPref, displayStr);
																						Toast.makeText(gui,toastMessage,Toast.LENGTH_SHORT).show();

																						OKDialog.dismiss();
																					}

																				});

																		Button cancelButton = (Button) OKDialog.findViewById(R.id.cancel_button);
																		cancelButton.setOnClickListener(new OnClickListener() {

																					public void onClick(View v) {
																						Toast.makeText(gui,	getString(R.string.timerNotSet), Toast.LENGTH_SHORT).show();
																						OKDialog.dismiss();
																					}

																				});

																		OKDialog.show();
																	}

																});

														endVolume.show();
													}
												},
												Integer.parseInt(endDefault.substring(0, endDefault.indexOf(":"))),
												Integer.parseInt(endDefault.substring(endDefault.indexOf(":") + 1)),
												false);
										timeEnd.show();
									}

								});

								diag.show();

							}
						}, Integer.parseInt(timeDefault.substring(0, timeDefault.indexOf(":"))), 
						   Integer.parseInt(timeDefault.substring(timeDefault.indexOf(":") + 1)), false);
				timeStart.show();
			}

		};
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.shortcut, menu);
	    return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		ContentResolver resolver = getContentResolver();
		switch(item.getItemId()){
		case R.id.create_shortcut:
			Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
	        shortcutIntent.setClassName(this, this.getClass().getName());

	        Intent intent = new Intent();
	        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
	        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Vibration Settings");
	        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this,  R.drawable.sound_icon);
	        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
	        
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            sendBroadcast(intent);

            // Inform the user that the shortcut has been created
            Toast.makeText(this, "Shortcut Created", Toast.LENGTH_SHORT).show();
			return true;

		case R.id.disable_ringer_timer:
			alarmManager.cancel(pendingVibrateRingerStart);
			alarmManager.cancel(pendingVibrateRingerEnd);
			

			DbUtil.update(resolver, getString(R.string.EnableVibrateRinger), 0);
			DbUtil.update(resolver, getString(R.string.VibrateRingerDisplay), "");
			
	        TextView ringerText = (TextView) findViewById(R.id.vibrateringer_timer_text);
	        ringerText.setText("");
			
			Toast.makeText(this, getString(R.string.VibrateRingerDisabled), Toast.LENGTH_SHORT);
			return true;
		case R.id.disable_notification_timer:
			alarmManager.cancel(pendingVibrateNotifStart);
			alarmManager.cancel(pendingVibrateNotifEnd);
			

			DbUtil.update(resolver, getString(R.string.EnableVibrateNotif), 0);
			DbUtil.update(resolver, getString(R.string.VibrateNotifDisplay), "");
			
	        TextView notifText = (TextView) findViewById(R.id.vibratenotif_timer_text);
	        notifText.setText("");
			
			Toast.makeText(this, getString(R.string.VibrateNotifDisabled), Toast.LENGTH_SHORT);
		}
		return super.onOptionsItemSelected(item);
	}
}
