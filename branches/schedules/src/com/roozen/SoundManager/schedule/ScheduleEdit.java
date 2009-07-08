/**
 * Copyright 2009 Mike Partridge
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
package com.roozen.SoundManager.schedule;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.roozen.SoundManager.R;
import com.roozen.SoundManager.provider.ScheduleProvider;
import com.roozen.SoundManager.utils.SQLiteDatabaseHelper;
import com.roozen.SoundManager.utils.Util;

/**
 * Schedule Edit screen
 * 
 * @author Mike Partridge
 */
public class ScheduleEdit extends Activity {

    private Long mScheduleId;
    private ToggleButton mDay0;
    private ToggleButton mDay1;
    private ToggleButton mDay2;
    private ToggleButton mDay3;
    private ToggleButton mDay4;
    private ToggleButton mDay5;
    private ToggleButton mDay6;
    private TimePicker mStartTime;
    private TimePicker mEndTime;
    private SeekBar mVolume;
    private CheckBox mVibrate;
    private TextView mVolumeDsc;
    
    private Integer mVolumeType;
    private boolean mClock24hour;
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*
         * check the saved state for schedule id, then check the bundle passed through the Intent
         */
        mScheduleId = savedInstanceState != null ? savedInstanceState.getLong(SQLiteDatabaseHelper.SCHEDULE_ID)
                                                 : null;
        if (mScheduleId == null) {
            Bundle extras = getIntent().getExtras();
            mScheduleId = extras != null ? extras.getLong(SQLiteDatabaseHelper.SCHEDULE_ID)
                                         : null;
            if (mScheduleId == 0) mScheduleId = null;
        }
        
        /*
         * figure out the volume type for the volume bar
         */
        mVolumeType = savedInstanceState != null ? savedInstanceState.getInt(ScheduleList.VOLUME_TYPE) 
                                                 : null;
        if (mVolumeType == null) {
            Bundle extras = getIntent().getExtras();
            mVolumeType = extras != null ? extras.getInt(ScheduleList.VOLUME_TYPE)
                                         : null;
        }
        
        setContentView(R.layout.schedule_edit);
        
        /*
         * get handles to the gui
         */

        mDay0 = (ToggleButton) findViewById(R.id.day0toggle);
        mDay1 = (ToggleButton) findViewById(R.id.day1toggle);
        mDay2 = (ToggleButton) findViewById(R.id.day2toggle);
        mDay3 = (ToggleButton) findViewById(R.id.day3toggle);
        mDay4 = (ToggleButton) findViewById(R.id.day4toggle);
        mDay5 = (ToggleButton) findViewById(R.id.day5toggle);
        mDay6 = (ToggleButton) findViewById(R.id.day6toggle);
        mStartTime = (TimePicker) findViewById(R.id.startTime);
        mEndTime = (TimePicker) findViewById(R.id.endTime);
        
        mVolume = (SeekBar) findViewById(R.id.volume);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mVolume.setMax(audio.getStreamMaxVolume(mVolumeType));
        
        mVibrate = (CheckBox) findViewById(R.id.vibrateCheckbox);
        
        mClock24hour = Util.is24HourClock(this.getContentResolver());
        mStartTime.setIs24HourView(mClock24hour);
        mEndTime.setIs24HourView(mClock24hour);

        mVolumeDsc = (TextView) findViewById(R.id.ScheduleType);
        
        populateFields();
    }
    
    /**
     * Populate GUI with data from the db if the schedule exists,
     * or with defaults if not
     */
    private void populateFields() {

        /*
         * set the header text based on volume type
         */
        switch (mVolumeType) {
            case AudioManager.STREAM_SYSTEM:
                mVolumeDsc.setText(R.string.SystemVolumeSchedule);
                break;
            case AudioManager.STREAM_RING:
                mVolumeDsc.setText(R.string.RingerVolumeSchedule);
                break;
            case AudioManager.STREAM_MUSIC:
                mVolumeDsc.setText(R.string.MediaVolumeSchedule);
                break;
            case AudioManager.STREAM_ALARM:
                mVolumeDsc.setText(R.string.AlarmVolumeSchedule);
                break;
            case AudioManager.STREAM_VOICE_CALL:
                mVolumeDsc.setText(R.string.InCallVolumeSchedule);
                break;
        }
        
        /*
         * load data
         */
        if (mScheduleId != null) {
            
            Uri schedulesUri = Uri.withAppendedPath(ScheduleProvider.CONTENT_URI, mScheduleId.toString());
            Cursor scheduleCursor = managedQuery(schedulesUri, null, null, null, null);

            if (scheduleCursor.moveToFirst()) {
            
                mDay0.setChecked(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY0)) > 0);
                mDay1.setChecked(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY1)) > 0);
                mDay2.setChecked(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY2)) > 0);
                mDay3.setChecked(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY3)) > 0);
                mDay4.setChecked(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY4)) > 0);
                mDay5.setChecked(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY5)) > 0);
                mDay6.setChecked(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_DAY6)) > 0);
                
                mStartTime.setCurrentHour(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_START_HOUR)));
                mStartTime.setCurrentMinute(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_START_MINUTE)));

                mEndTime.setCurrentHour(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_END_HOUR)));
                mEndTime.setCurrentMinute(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_END_MINUTE)));
                
                mVolume.setProgress(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_VOLUME)));
                mVibrate.setChecked(scheduleCursor.getInt(scheduleCursor.getColumnIndexOrThrow(SQLiteDatabaseHelper.SCHEDULE_VIBRATE)) > 0);
            
            }
        }
        
        /*
         * new schedule - populate defaults
         */
        else {
            
            mDay1.setChecked(true);
            mDay2.setChecked(true);
            mDay3.setChecked(true);
            mDay4.setChecked(true);
            mDay5.setChecked(true);

            mStartTime.setCurrentHour(8);
            mStartTime.setCurrentMinute(0);
            mEndTime.setCurrentHour(17);
            mEndTime.setCurrentMinute(0);
            
            mVolume.setProgress((int)(mVolume.getMax() / 2));
            mVibrate.setChecked(false);
        }
        
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        
        saveState();
        
        Toast.makeText(this, R.string.scheduleSaved, Toast.LENGTH_SHORT).show();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        populateFields();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        //store the schedule id for display on resume
        outState.putLong(SQLiteDatabaseHelper.SCHEDULE_ID, mScheduleId);
    }
    
    /**
     * writes schedule to db
     */
    private void saveState() {
        
        ContentValues values = new ContentValues();

        values.put(SQLiteDatabaseHelper.SCHEDULE_DAY0, mDay0.isChecked() ? "1" : "0");
        values.put(SQLiteDatabaseHelper.SCHEDULE_DAY1, mDay1.isChecked() ? "1" : "0");
        values.put(SQLiteDatabaseHelper.SCHEDULE_DAY2, mDay2.isChecked() ? "1" : "0");
        values.put(SQLiteDatabaseHelper.SCHEDULE_DAY3, mDay3.isChecked() ? "1" : "0");
        values.put(SQLiteDatabaseHelper.SCHEDULE_DAY4, mDay4.isChecked() ? "1" : "0");
        values.put(SQLiteDatabaseHelper.SCHEDULE_DAY5, mDay5.isChecked() ? "1" : "0");
        values.put(SQLiteDatabaseHelper.SCHEDULE_DAY6, mDay6.isChecked() ? "1" : "0");
        
        values.put(SQLiteDatabaseHelper.SCHEDULE_START_HOUR, mStartTime.getCurrentHour());
        values.put(SQLiteDatabaseHelper.SCHEDULE_START_MINUTE, mStartTime.getCurrentMinute());
        
        values.put(SQLiteDatabaseHelper.SCHEDULE_END_HOUR, mEndTime.getCurrentHour());
        values.put(SQLiteDatabaseHelper.SCHEDULE_END_MINUTE, mEndTime.getCurrentMinute());
        
        values.put(SQLiteDatabaseHelper.SCHEDULE_VOLUME, mVolume.getProgress());
        values.put(SQLiteDatabaseHelper.SCHEDULE_VIBRATE, mVibrate.isChecked() ? "1" : "0");
        
        if (mScheduleId == null) {
            values.put(SQLiteDatabaseHelper.SCHEDULE_TYPE, mVolumeType);
            
            Uri newSchedule = getContentResolver().insert(ScheduleProvider.CONTENT_URI, values);
            mScheduleId = Long.parseLong(newSchedule.getPathSegments().get(1));
        }
        else {
            Uri updateUri = Uri.withAppendedPath(ScheduleProvider.CONTENT_URI, mScheduleId.toString());
            getContentResolver().update(updateUri, values, null, null);
        }
        
    }
    
}
