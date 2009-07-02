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

import android.content.Context;
import android.media.AudioManager;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.roozen.SoundManager.R;

/**
 * @author Mike Partridge
 */
public class ScheduleView extends LinearLayout {

    private TextView mDay0;
    private TextView mDay1;
    private TextView mDay2;
    private TextView mDay3;
    private TextView mDay4;
    private TextView mDay5;
    private TextView mDay6;
    private TextView mStartTime;
    private TextView mEndTime;
    private SeekBar mVolume;
    private int mVolumeType;
    private TextView mVibrate;
    
    /**
     * @param context
     * @param schedule
     */
    public ScheduleView(Context context, Schedule schedule) {
        super(context);
        
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        this.setOrientation(VERTICAL);
        this.setClickable(false);

        //convenience for addView calls later
        LinearLayout.LayoutParams paramsWrapBoth = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsFillWrap = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                
        /*
         * days
         */
        LinearLayout daysLayout = new LinearLayout(context);
        daysLayout.setOrientation(HORIZONTAL);
        daysLayout.setGravity(Gravity.CENTER);

        mDay0 = new TextView(context);
        mDay0.setPadding(5, 5, 5, 5);
        mDay0.setText(schedule.isDay0() ? "Sun" : "   ");
        daysLayout.addView(mDay0, paramsWrapBoth);
        mDay1 = new TextView(context);
        mDay1.setPadding(5, 5, 5, 5);
        mDay1.setText(schedule.isDay1() ? "Mon" : "   ");
        daysLayout.addView(mDay1, paramsWrapBoth);
        mDay2 = new TextView(context);
        mDay2.setPadding(5, 5, 5, 5);
        mDay2.setText(schedule.isDay2() ? "Tue" : "   ");
        daysLayout.addView(mDay2, paramsWrapBoth);
        mDay3 = new TextView(context);
        mDay3.setPadding(5, 5, 5, 5);
        mDay3.setText(schedule.isDay3() ? "Wed" : "   ");
        daysLayout.addView(mDay3, paramsWrapBoth);
        mDay4 = new TextView(context);
        mDay4.setPadding(5, 5, 5, 5);
        mDay4.setText(schedule.isDay4() ? "Thu" : "   ");
        daysLayout.addView(mDay4, paramsWrapBoth);
        mDay5 = new TextView(context);
        mDay5.setPadding(5, 5, 5, 5);
        mDay5.setText(schedule.isDay5() ? "Fri" : "   ");
        daysLayout.addView(mDay5, paramsWrapBoth);
        mDay6 = new TextView(context);
        mDay6.setPadding(5, 5, 5, 5);
        mDay6.setText(schedule.isDay6() ? "Sat" : "   ");
        daysLayout.addView(mDay6, paramsWrapBoth);
        
        addView(daysLayout, paramsFillWrap);
        
        /*
         * times
         */
        TableLayout timesLayout = new TableLayout(context);
        timesLayout.setStretchAllColumns(true);
        TableRow tr = new TableRow(context);
        
        TextView startTimeLabel = new TextView(context);
        startTimeLabel.setPadding(2, 2, 2, 2);
        startTimeLabel.setText(R.string.startTimeLabel);
        tr.addView(startTimeLabel);
        
        mStartTime = new TextView(context);
        mStartTime.setTextSize(18);
        mStartTime.setPadding(2, 2, 2, 2);
        mStartTime.setText(schedule.getStartTime());
        tr.addView(mStartTime);

        TextView endTimeLabel = new TextView(context);
        endTimeLabel.setPadding(2, 2, 2, 2);
        endTimeLabel.setText(R.string.endTimeLabel);
        tr.addView(endTimeLabel);
        
        mEndTime = new TextView(context);
        mEndTime.setTextSize(18);
        mEndTime.setPadding(2, 2, 2, 2);
        mEndTime.setText(schedule.getEndTime());
        tr.addView(mEndTime);
        
        timesLayout.addView(tr);
        addView(timesLayout, paramsWrapBoth);
        
        /*
         * volume
         */
        LinearLayout volumeLayout = new LinearLayout(context);
        volumeLayout.setOrientation(HORIZONTAL);
        volumeLayout.setGravity(Gravity.CENTER);
        
        TextView volumeLabel = new TextView(context);
        volumeLabel.setPadding(2, 2, 2, 2);
        volumeLabel.setText(R.string.volumeLabel);
        volumeLayout.addView(volumeLabel, paramsWrapBoth);
        
        mVolumeType = schedule.getVolumeType();
        
        mVolume = new SeekBar(context);
        mVolume.setEnabled(false);
        mVolume.setFocusable(false);
        mVolume.setFocusableInTouchMode(false);
        mVolume.setClickable(false);
        mVolume.setPadding(2, 2, 2, 2);
        mVolume.setMax(audio.getStreamMaxVolume(mVolumeType));
        mVolume.setProgress(schedule.getVolume());
        volumeLayout.addView(mVolume, paramsFillWrap);
        
        addView(volumeLayout, paramsFillWrap);
        
        /*
         * vibrate
         */
        LinearLayout vibrateLayout = new LinearLayout(context);
        
        TextView vibrateLabel = new TextView(context);
        vibrateLabel.setPadding(2, 2, 2, 2);
        vibrateLabel.setText(R.string.vibrateLabel);
        vibrateLayout.addView(vibrateLabel, paramsWrapBoth);
        
        mVibrate = new TextView(context);
        mVibrate.setPadding(2, 2, 2, 2);
        mVibrate.setText(schedule.isVibrate() ? "On" : "Off");
        vibrateLayout.addView(mVibrate, paramsWrapBoth);
        
        addView(vibrateLayout, paramsFillWrap);
    }
 
    /**
     * @param schedule
     */
    public void setFromSchedule(Schedule schedule) {
        
        setDay0(schedule.isDay0());
        setDay1(schedule.isDay1());
        setDay2(schedule.isDay2());
        setDay3(schedule.isDay3());
        setDay4(schedule.isDay4());
        setDay5(schedule.isDay5());
        setDay6(schedule.isDay6());
        setStartTime(schedule.getStartTime());
        setEndTime(schedule.getEndTime());
        setVolume(schedule.getVolume());
        
    }
    
    /**
     * @param day
     */
    public void setDay0(boolean day) {
        mDay0.setText(day ? "Sun" : "   ");
    }
 
    /**
     * @param day
     */
    public void setDay1(boolean day) {
        mDay1.setText(day ? "Mon" : "   ");
    }
 
    /**
     * @param day
     */
    public void setDay2(boolean day) {
        mDay2.setText(day ? "Tue" : "   ");
    }
 
    /**
     * @param day
     */
    public void setDay3(boolean day) {
        mDay3.setText(day ? "Wed" : "   ");
    }
 
    /**
     * @param day
     */
    public void setDay4(boolean day) {
        mDay4.setText(day ? "Thu" : "   ");
    }
 
    /**
     * @param day
     */
    public void setDay5(boolean day) {
        mDay5.setText(day ? "Fri" : "   ");
    }
 
    /**
     * @param day
     */
    public void setDay6(boolean day) {
        mDay6.setText(day ? "Sat" : "   ");
    }
    
    /**
     * @param startTime
     */
    public void setStartTime(String startTime) {
        mStartTime.setText(startTime);
    }
    
    /**
     * @param endTime
     */
    public void setEndTime(String endTime) {
        mEndTime.setText(endTime);
    }
    
    /**
     * @param volume
     */
    public void setVolume(int volume) {
        mVolume.setProgress(volume);
    }
    
    /**
     * @param vibrate
     */
    public void setVibrate(boolean vibrate) {
        mVibrate.setText(vibrate ? "On" : "Off");
    }
    
    /**
     * @param volumeType
     */
    public void setVolumeType(int volumeType) {
        mVolumeType = volumeType;
    }
    
}
