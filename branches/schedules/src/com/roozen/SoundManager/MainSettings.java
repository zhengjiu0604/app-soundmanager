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

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.roozen.SoundManager.provider.ScheduleProvider;
import com.roozen.SoundManager.schedule.ScheduleList;
import com.roozen.SoundManager.utils.SQLiteDatabaseHelper;

public class MainSettings extends Activity {
	public final static String PREFS_NAME = "EZSoundManagerPrefs";
	
	private Context gui;
    
    public final static int ACTIVITY_LIST = 0;
    
    private HashMap<Integer,Integer> mActiveCount;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gui = this;

    	final SharedPreferences settings = getSharedPreferences(MainSettings.PREFS_NAME, 0);
        boolean hasShownStartup = settings.getBoolean(getString(R.string.ShownStartup), false);
          
        setupSeekbars();    
        setupButtons();
        setStatusText();
        
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
        
    private void setupSeekbars(){
    	final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	final int setVolFlags = AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE |
							AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_VIBRATE;
        
        SeekBar systemSeek = (SeekBar) findViewById(R.id.system_seekbar);
        systemSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        systemSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_SYSTEM));
        systemSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
            
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
              //ignore
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //ignore
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                audio.setStreamVolume(AudioManager.STREAM_SYSTEM, seekBar.getProgress(), setVolFlags);
            }
            
        });
        
        SeekBar ringerSeek = (SeekBar) findViewById(R.id.ringer_seekbar);
        ringerSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_RING));
        ringerSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_RING));
        ringerSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
              //ignore
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //ignore
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                audio.setStreamVolume(AudioManager.STREAM_RING, seekBar.getProgress(), setVolFlags);
            }
            
        });
        
        SeekBar notifSeek = (SeekBar) findViewById(R.id.notif_seekbar);
        notifSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
        notifSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
        notifSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
              //ignore
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //ignore
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, seekBar.getProgress(), setVolFlags);
            }
            
        });
        
        SeekBar mediaSeek = (SeekBar) findViewById(R.id.media_seekbar);
        mediaSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mediaSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_MUSIC));
        mediaSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
			  //ignore
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				//ignore
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				audio.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), setVolFlags);
			}
        	
        });
        
        SeekBar alarmSeek = (SeekBar) findViewById(R.id.alarm_seekbar);
        alarmSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        alarmSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_ALARM));
        alarmSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
			  //ignore
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				//ignore
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				audio.setStreamVolume(AudioManager.STREAM_ALARM, seekBar.getProgress(), setVolFlags);
			}
        	
        });
        
        SeekBar phonecallSeek = (SeekBar) findViewById(R.id.phonecall_seekbar);
        phonecallSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
        phonecallSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        phonecallSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
			  //ignore
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				//ignore
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, seekBar.getProgress(), setVolFlags);
			}
        	
        });        
    }
    
    private void setupButtons() {
	    	
        Button systemTimer = (Button) findViewById(R.id.system_timer_button);
        systemTimer.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent i = new Intent(gui, ScheduleList.class);
        		i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_SYSTEM));
                startActivityForResult(i, ACTIVITY_LIST);
        	}
        });
            
        Button ringerTimer = (Button) findViewById(R.id.ringer_timer_button);
        ringerTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_RING));
                startActivityForResult(i, ACTIVITY_LIST);
            }
        });
            
        Button notifTimer = (Button) findViewById(R.id.notif_timer_button);
        notifTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_NOTIFICATION));
                startActivityForResult(i, ACTIVITY_LIST);
            }
        });
    	
        Button mediaTimer = (Button) findViewById(R.id.media_timer_button);
        mediaTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_MUSIC));
                startActivityForResult(i, ACTIVITY_LIST);
            }
        });
    	
        Button alarmTimer = (Button) findViewById(R.id.alarm_timer_button);
        alarmTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_ALARM));
                startActivityForResult(i, ACTIVITY_LIST);
            }
        });
    	
    	Button incallTimer = (Button) findViewById(R.id.phonecall_timer_button);
    	incallTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_VOICE_CALL));
                startActivityForResult(i, ACTIVITY_LIST);
            }
        });
    	   
    }
    
    private void setStatusText() {
        
        countActiveSchedules();
        
        TextView systemText = (TextView) findViewById(R.id.system_timer_text);
        systemText.setText(getScheduleCountText(AudioManager.STREAM_SYSTEM));
        
        TextView ringerText = (TextView) findViewById(R.id.ringer_timer_text);
        ringerText.setText(getScheduleCountText(AudioManager.STREAM_RING));
        
        TextView notifText = (TextView) findViewById(R.id.notif_timer_text);
        notifText.setText(getScheduleCountText(AudioManager.STREAM_NOTIFICATION));
        
        TextView mediaText = (TextView) findViewById(R.id.media_timer_text);
        mediaText.setText(getScheduleCountText(AudioManager.STREAM_MUSIC));
        
        TextView alarmText = (TextView) findViewById(R.id.alarm_timer_text);
        alarmText.setText(getScheduleCountText(AudioManager.STREAM_ALARM));
        
        TextView incallText = (TextView) findViewById(R.id.phonecall_timer_text);
        incallText.setText(getScheduleCountText(AudioManager.STREAM_VOICE_CALL));
        
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
    
	private void countActiveSchedules() {
	    
	    mActiveCount = new HashMap<Integer,Integer>();
	    
        /*
         * get all active schedules, count them by type
         */
        Uri schedulesUri = Uri.withAppendedPath(ScheduleProvider.CONTENT_URI, "active");
        Cursor scheduleCursor = managedQuery(schedulesUri, null, null, null, null);
	    
        if (scheduleCursor.moveToFirst()) {
            
            int typeIndex = scheduleCursor.getColumnIndex(SQLiteDatabaseHelper.SCHEDULE_TYPE);
            
            do {
                
                if (!mActiveCount.containsKey(scheduleCursor.getInt(typeIndex))) {
                    mActiveCount.put(scheduleCursor.getInt(typeIndex), 0);
                }
                
                mActiveCount.put(scheduleCursor.getInt(typeIndex), mActiveCount.get(scheduleCursor.getInt(typeIndex)).intValue()+1);
                
            } while(scheduleCursor.moveToNext());
            
        }
	}
	
	private String getScheduleCountText(int volumeType) {
	    String result = "";
	    
	    if (mActiveCount.containsKey(volumeType) &&
	            mActiveCount.get(volumeType) > 0) {
	        result = mActiveCount.get(volumeType).toString() + " active schedule";
	        result += mActiveCount.get(volumeType) > 1 ? "s" : "";
	    }
	    
	    return result;
	}
	
	/* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        setStatusText();
    }
}