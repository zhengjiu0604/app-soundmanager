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

public class RingmodeToggle extends Activity {

	private Context gui;
	
	private PendingIntent pendingRingmodeStart;
	private PendingIntent pendingRingmodeEnd;
	
	public static final int RINGER_VIBRATE = 0;
	public static final int RINGER_ONLY = 1;
	public static final int VIBRATE_ONLY = 2;
	public static final int SILENT = 3;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ringmode_toggle);
		gui = this;
		
    	setupPendingIntents();
    	
		final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		setupButtons(audio);

    	final ContentResolver resolver = getContentResolver();
		
		TextView displayText = (TextView) findViewById(R.id.ringmode_timer_text);
		displayText.setText(DbUtil.queryString(resolver, getString(R.string.RingmodeDisplay), ""));
		
		Button timer = (Button) findViewById(R.id.ringmode_timer_button);
		timer.setOnClickListener(this.getNewOnClickListener(audio, getString(R.string.RingmodeTimeStart), 
				getString(R.string.RingmodeTimeEnd), getString(R.string.RingmodeStartVolume), getString(R.string.RingmodeEndVolume), 
				pendingRingmodeStart, pendingRingmodeEnd, "Ringmode Timer Set", getString(R.string.EnableRingmode), displayText, 
				getString(R.string.RingmodeDisplay)));
	}
	
	private void setupPendingIntents(){
		Intent soundTimer = new Intent(this, SoundTimer.class);
        pendingRingmodeStart = PendingIntent.getBroadcast(this, R.string.RingmodeTimeStart, 
        		new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.RINGER_MODE_START), 0);
        
        pendingRingmodeEnd = PendingIntent.getBroadcast(this, R.string.RingmodeTimeEnd, 
        		new Intent(soundTimer).putExtra(MainSettings.EXTRA_WHICH, MainSettings.RINGER_MODE_END), 0);
	}
	
	private void setupButtons(final AudioManager audio){
		int ringmode = audio.getRingerMode();

		final RadioButton ringVibrate = (RadioButton) findViewById(R.id.ring_vibrate);
		final RadioButton ringOnly = (RadioButton) findViewById(R.id.ring_only);
		final RadioButton vibrateOnly = (RadioButton) findViewById(R.id.vibrate_only);
		final RadioButton silent = (RadioButton) findViewById(R.id.silent);

		switch (ringmode) {
		case AudioManager.RINGER_MODE_SILENT:
			silent.setChecked(true);
			break;
		case AudioManager.RINGER_MODE_VIBRATE:
			vibrateOnly.setChecked(true);
			break;
		case AudioManager.RINGER_MODE_NORMAL:
			int vibrateSetting = audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
			if (vibrateSetting == AudioManager.VIBRATE_SETTING_ON) {
				ringVibrate.setChecked(true);
			} else {
				ringOnly.setChecked(true);
			}
			break;
		}

		ringVibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton button,
					boolean isChecked) {
				if (isChecked) {
					audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
							AudioManager.VIBRATE_SETTING_ON);
				}
			}

		});

		ringOnly.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
							AudioManager.VIBRATE_SETTING_ONLY_SILENT);
				}
			}

		});

		vibrateOnly.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				}
			}

		});

		silent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
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
			final String displayPref) {
		
		return new OnClickListener() {

			@Override
			public void onClick(View view) {
				final ContentResolver resolver = getContentResolver();
				Calendar cal = Calendar.getInstance(TimeZone.getDefault(),Locale.getDefault());
				String timeDefault = DbUtil.queryString(resolver, timeStartPref, cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));

				TimePickerDialog timeStart = new TimePickerDialog(gui,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								final String time = hourOfDay + ":" + minute;

								DbUtil.update(resolver, timeStartPref, time);
								
								final Dialog diag = new Dialog(gui);
								diag.setContentView(R.layout.ringmode_timer_choice);
								
								int ringmode = DbUtil.queryInt(resolver, volumeStartPref, -1);
								if(ringmode == -1){
									int mode = audio.getRingerMode();
									switch (mode) {
									case AudioManager.RINGER_MODE_SILENT:
										ringmode = RingmodeToggle.SILENT;
										break;
									case AudioManager.RINGER_MODE_VIBRATE:
										ringmode = RingmodeToggle.VIBRATE_ONLY;
										break;
									case AudioManager.RINGER_MODE_NORMAL:
										int vibrateSetting = audio
												.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
										if (vibrateSetting == AudioManager.VIBRATE_SETTING_ON) {
											ringmode = RingmodeToggle.RINGER_VIBRATE;
										} else {
											ringmode = RingmodeToggle.RINGER_ONLY;
										}
										break;
									}
								}

								final RadioButton ringVibrate = (RadioButton) diag.findViewById(R.id.ring_vibrate);
								final RadioButton ringOnly = (RadioButton) diag.findViewById(R.id.ring_only);
								final RadioButton vibrateOnly = (RadioButton) diag.findViewById(R.id.vibrate_only);
								final RadioButton silent = (RadioButton) diag.findViewById(R.id.silent);

								switch (ringmode) {
								case RingmodeToggle.SILENT:
									silent.setChecked(true);
									break;
								case RingmodeToggle.VIBRATE_ONLY:
									vibrateOnly.setChecked(true);
									break;
								case RingmodeToggle.RINGER_ONLY:
									ringOnly.setChecked(true);
									break;
								case RingmodeToggle.RINGER_VIBRATE:
									ringVibrate.setChecked(true);
									break;
								}
								
								Button ok = (Button) diag.findViewById(R.id.ok_button);
								ok.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										int vol = -1;
										if(ringVibrate.isChecked()){
											vol = RINGER_VIBRATE;
										} else if(ringOnly.isChecked()){
											vol = RINGER_ONLY;
										} else if(vibrateOnly.isChecked()){
											vol = VIBRATE_ONLY;
										} else if(silent.isChecked()){
											vol = SILENT;
										}
										DbUtil.update(resolver, volumeStartPref, vol);

										diag.dismiss();

										String endDefault = DbUtil.queryString(resolver, timeEndPref, time);

										TimePickerDialog timeEnd = new TimePickerDialog(gui,
												new TimePickerDialog.OnTimeSetListener() {

													@Override
													public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
														String nexttime = hourOfDay	+ ":" + minute;
														DbUtil.update(resolver, timeEndPref, nexttime);

														final Dialog endVolume = new Dialog(gui);
														endVolume.setContentView(R.layout.ringmode_timer_choice);
														
														int endringmode = DbUtil.queryInt(resolver, volumeEndPref, -1);
														if(endringmode == -1){
															int mode = audio.getRingerMode();
															switch (mode) {
															case AudioManager.RINGER_MODE_SILENT:
																endringmode = RingmodeToggle.SILENT;
																break;
															case AudioManager.RINGER_MODE_VIBRATE:
																endringmode = RingmodeToggle.VIBRATE_ONLY;
																break;
															case AudioManager.RINGER_MODE_NORMAL:
																int vibrateSetting = audio
																		.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
																if (vibrateSetting == AudioManager.VIBRATE_SETTING_ON) {
																	endringmode = RingmodeToggle.RINGER_VIBRATE;
																} else {
																	endringmode = RingmodeToggle.RINGER_ONLY;
																}
																break;
															}
														}

														final RadioButton endringVibrate = (RadioButton) endVolume.findViewById(R.id.ring_vibrate);
														final RadioButton endringOnly = (RadioButton) endVolume.findViewById(R.id.ring_only);
														final RadioButton endvibrateOnly = (RadioButton) endVolume.findViewById(R.id.vibrate_only);
														final RadioButton endsilent = (RadioButton) endVolume.findViewById(R.id.silent);

														switch (endringmode) {
														case RingmodeToggle.SILENT:
															endsilent.setChecked(true);
															break;
														case RingmodeToggle.VIBRATE_ONLY:
															endvibrateOnly.setChecked(true);
															break;
														case RingmodeToggle.RINGER_ONLY:
															endringOnly.setChecked(true);
															break;
														case RingmodeToggle.RINGER_VIBRATE:
															endringVibrate.setChecked(true);
															break;
														}
														
														Button endOk = (Button) endVolume.findViewById(R.id.ok_button);
														endOk.setOnClickListener(new OnClickListener() {

																	@Override
																	public void onClick(View v) {
																		int vol = -1;
																		if(endringVibrate.isChecked()){
																			vol = RINGER_VIBRATE;
																		} else if(endringOnly.isChecked()){
																			vol = RINGER_ONLY;
																		} else if(endvibrateOnly.isChecked()){
																			vol = VIBRATE_ONLY;
																		} else if(endsilent.isChecked()){
																			vol = SILENT;
																		}
																		DbUtil.update(resolver, volumeEndPref, vol);

																		endVolume.dismiss();

																		final Dialog OKDialog = new Dialog(gui);
																		OKDialog.setContentView(R.layout.okdialog);
																		OKDialog.setTitle("Confirm");
																		TextView oktext = (TextView) OKDialog.findViewById(R.id.verify);

																		oktext.setText("Are you sure you want to set this timer?\n\n"
																						+ "This may change your volume immediately.\n");

																		Button okbutton = (Button) OKDialog.findViewById(R.id.ok_button);
																		okbutton.setOnClickListener(new OnClickListener() {

																					@Override
																					public void onClick(View view) {
																						String startTime = DbUtil.queryString(resolver,	timeStartPref, "");
																						if (startTime != null && startTime.length() > 0
																								&& startTime.contains(":")) {
																							Calendar cal = Util.getMyCalendar(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))), 
																														 Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)),
																														 0, 200);

																							AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
																							alarmManager.cancel(pendingIntentStart);
																							alarmManager.cancel(pendingIntentEnd);
																							alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
																											cal.getTimeInMillis(),
																											1000 * 60 * 60 * 24,
																											pendingIntentStart); // Repeating alarm every day
																						}

																						String endTime = DbUtil.queryString(resolver, timeEndPref, "");
																						if (endTime != null	&& endTime.length() > 0	&& endTime.contains(":")) {
																							Calendar cal = Util.getMyCalendar(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))),
																															  Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)),
																															  0, 205);

																							AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
																							alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
																											cal.getTimeInMillis(),
																											1000 * 60 * 60 * 24,
																											pendingIntentEnd); // Repeating alarm every day
																						}

																						String startHour = Util.padZero(startTime.substring(0, startTime.indexOf(":")));
																						String startMin = Util.padZero(startTime.substring(startTime.indexOf(":") + 1));

																						String endHour = Util.padZero(endTime.substring(0, endTime.indexOf(":")));
																						String endMin = Util.padZero(endTime.substring(endTime.indexOf(":") + 1));
																						
																						int startInt = DbUtil.queryInt(resolver, volumeStartPref, -1);
																						String startMode = null;
																						if(startInt == RingmodeToggle.RINGER_VIBRATE){
																							startMode = "Ring & Vibrate";
																						} else if(startInt == RingmodeToggle.RINGER_ONLY){
																							startMode = "Ring Only";
																						} else if(startInt == RingmodeToggle.VIBRATE_ONLY){
																							startMode = "Vibrate Only";
																						} else if(startInt == RingmodeToggle.SILENT){
																							startMode = "Silent";
																						}
																						int endInt = DbUtil.queryInt(resolver, volumeEndPref, -1);
																						String endMode = null;
																						if(endInt == RingmodeToggle.RINGER_VIBRATE){
																							endMode = "Ring & Vibrate";
																						} else if(endInt == RingmodeToggle.RINGER_ONLY){
																							endMode = "Ring Only";
																						} else if(endInt == RingmodeToggle.VIBRATE_ONLY){
																							endMode = "Vibrate Only";
																						} else if(endInt == RingmodeToggle.SILENT){
																							endMode = "Silent";
																						}

																						String displayStr = "Start: "+ startHour + ":" + startMin + " Mode: " + startMode
																								+ "\nEnd: "	+ endHour + ":" + endMin + " Vol: " + endMode;
																						display.setText(displayStr);

																						DbUtil.update(resolver, enablePref, 1);
																						DbUtil.update(resolver, displayPref, displayStr);
																						Toast.makeText(gui,	toastMessage, Toast.LENGTH_SHORT).show();

																						OKDialog.dismiss();
																					}

																				});

																		Button cancelButton = (Button) OKDialog.findViewById(R.id.cancel_button);
																		cancelButton.setOnClickListener(new OnClickListener() {

																					@Override
																					public void onClick(View v) {
																						Toast.makeText(gui, getString(R.string.timerNotSet),Toast.LENGTH_SHORT).show();
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

	public static void fixRingMode(AudioManager audio) {
		int vol = audio.getStreamVolume(AudioManager.STREAM_RING);
		RingmodeToggle.fixRingMode(audio, vol);
	}

	public static void fixRingMode(AudioManager audio, int vol) {
		int ringerVibrate = audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);

		if (vol == 0 && ringerVibrate != AudioManager.VIBRATE_SETTING_OFF) {
			audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		} else if (vol == 0	&& ringerVibrate == AudioManager.VIBRATE_SETTING_OFF) {
			audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		} else if (vol > 0) {
			audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ringer_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.create_shortcut:
			Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
			shortcutIntent.setClassName(this, this.getClass().getName());

			Intent intent = new Intent();
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "RingMode Toggle");
			Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.bell);
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			sendBroadcast(intent);

			// Inform the user that the shortcut has been created
			Toast.makeText(this, "Shortcut Created", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.disable_timer:
			ContentResolver resolver = getContentResolver();
			DbUtil.update(resolver, getString(R.string.EnableRingmode), 0);
			DbUtil.update(resolver, getString(R.string.RingmodeDisplay), "");
			
	        ((TextView) findViewById(R.id.ringmode_timer_text)).setText("");
			
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.cancel(pendingRingmodeStart);
			alarmManager.cancel(pendingRingmodeEnd);
			
			Toast.makeText(this, getString(R.string.RingmodeDisabled), Toast.LENGTH_SHORT);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
