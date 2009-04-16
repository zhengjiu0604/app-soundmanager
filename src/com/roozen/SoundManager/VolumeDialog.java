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

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VolumeDialog extends Activity {

	public static final String TYPE = "TYPE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();            
		final int volumeType = extras != null ? extras.getInt(TYPE) : -1;
		
		final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		String title = "";
		if(volumeType != -1){
			switch(volumeType){
			case AudioManager.STREAM_SYSTEM:
				title = "System Volume";
				break;
			case AudioManager.STREAM_RING:
				title = "Ringer Volume";
				break;
			case AudioManager.STREAM_MUSIC:
				title = "Music/Video Volume";
				break;
			case AudioManager.STREAM_VOICE_CALL:
				title = "In-Call Volume";
				break;
			case AudioManager.STREAM_ALARM:
				title = "Alarm Volume";
				break;
			}

			setContentView(R.layout.volume_edit);
			setTitle(title);
			
			final SeekBar systemSeek = (SeekBar) findViewById(R.id.volume_bar);
	        systemSeek.setMax(audio.getStreamMaxVolume(volumeType));
	        systemSeek.setProgress(audio.getStreamVolume(volumeType));
	        systemSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromTouch) {
					int flags = AudioManager.FLAG_PLAY_SOUND;
					flags = flags | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE;
					flags = flags | AudioManager.FLAG_VIBRATE;
					audio.setStreamVolume(volumeType, progress, flags);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					int flags = AudioManager.FLAG_PLAY_SOUND;
					flags = flags | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE;
					flags = flags | AudioManager.FLAG_SHOW_UI;
					flags = flags | AudioManager.FLAG_VIBRATE;
					audio.setStreamVolume(volumeType, seekBar.getProgress(), flags);
				}
	        	
	        });
			
			Button ok = (Button) findViewById(R.id.ok_button);
				ok.setOnClickListener(new OnClickListener(){
		
					@Override
					public void onClick(View v) {											
						if(volumeType == AudioManager.STREAM_RING){
							RingmodeToggle.fixRingMode(audio);
						}
						
						finish();
					}
					
				});
		} else {
			finish();
		}
	}

}
